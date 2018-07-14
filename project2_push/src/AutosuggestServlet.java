

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/auto-suggestion")
public class AutosuggestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static HashMap<String, HashSet<String>> moviesMap = new HashMap<>();
    public AutosuggestServlet() {
        super();
    }
    private static void generate_movies(String query) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, NamingException
    {
//    	String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
//		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    	Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        // Look up our data source
        DataSource ds =null;
        if(Math.random() < 0.5)
        {
        	ds = (DataSource) envCtx.lookup("jdbc/MasterDB");
        	}
        else
        {
        	ds = (DataSource) envCtx.lookup("jdbc/SlaveDB");
        }
        Connection connection = ds.getConnection();
    	System.out.println("success");
    	String q = "select m.title\r\n" + 
    			"from \r\n" + 
    			"(select * from movies where ";
    	String[] titles = query.split(" ");
    	ArrayList<String> value = new ArrayList<String>();
    	HashSet<String> movies =new HashSet<String> ();
    	int count = 0;
    	for (int i = 0;i<titles.length;i++)
        {	q += " match (title) against (? in boolean mode) ";
        	value.add(titles[i]+"*");
        	count += 1;
        	if (titles.length>0)
        	{	if (i!=titles.length-1)
	        	{
	        		q +=" and ";
	        	}
        	}
        }
    	q+= ") as m inner join stars_in_movies as sim on sim.movieId = m.id inner join (select * from stars ) as s on s.Id = sim.starId  inner join ratings as r on m.id = r.movieId inner join genres_in_movies as gim on r.movieId = gim.movieId inner join genres as g on g.id = gim.genreId group by m.id, r.rating limit 10 offset 0\r\n"  
    			;
    	java.sql.PreparedStatement statement = connection.prepareStatement(q);
    	System.out.println("Before->"+statement.toString());
    	for (int i=0;i<value.size();i++)
        {
        	statement.setString(i+1, value.get(i));
        }
    	System.out.println("After->"+statement.toString());
    	ResultSet resultSet = statement.executeQuery();
    	while(resultSet.next())
    	{
    		movies.add(resultSet.getString("title"));
    	}
    	System.out.println("All Right");
		moviesMap.put(query, movies);
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try {
			// setup the response json arrray
			JsonArray jsonArray = new JsonArray();
			
			// get the query string from parameter
			String query = request.getParameter("query").toLowerCase();
			
			// return the empty json array if query is null or empty
			if (query == null || query.trim().isEmpty()) {
				response.getWriter().write(jsonArray.toString());
				return;
			}	
			boolean has_key = false;
			for (String id : moviesMap.keySet()) {
				if (id.equals(query))
					has_key = true;
			}
			if (has_key==false)
			{
				generate_movies(query);
			}
			System.out.println("Has Key is"+has_key);
			System.out.println("movie map is "+moviesMap);
			for (String id : moviesMap.keySet()) {
				HashSet<String> titles = moviesMap.get(id);
				if (id.equals(query))
				{	
					for (String title:titles)
					{	jsonArray.add(generateJsonObject(title));
							};
				}
			}
			response.getWriter().write(jsonArray.toString());
			return;
			
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			response.sendError(500, e.getMessage());
		}
		
	}
	private static JsonObject generateJsonObject(String title) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", title);
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", "Movies");
		additionalDataJsonObject.addProperty("title", title);
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}
}


