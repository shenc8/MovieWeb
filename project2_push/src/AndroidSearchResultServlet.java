

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Map;

/**
 * Servlet implementation class AndroidSearchResultServlet
 */
@WebServlet(name = "/AndroidSearchResultServlet", urlPatterns = "/api/android_search_result")
public class AndroidSearchResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public AndroidSearchResultServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> map = request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println(key);
            System.out.println(map.get(key)[0]);
        }
		String title = request.getParameter("title");
		String loginUser = "mytestuser";
	    String loginPasswd = "mypassword";
	    String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
	    response.setContentType("application/json");
        PrintWriter out = response.getWriter();
//        out.println("<html><head><title>MoviePage</title></head>");
//        out.println("<body><h1>MovieDB</h1>");
        try {
        	Class.forName("com.mysql.jdbc.Driver").newInstance();
        	System.out.println("success");
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		String query = "";
    		String one = "";
    		if (title != "")
            {
            	one += "where";
            }
            String two ="";
            if (title != "") {
            	String[] titles = title.split(" ");
            	for (int i = 0;i<titles.length;i++)
    	        {	
    	        	two += " match (title) against ('"+titles[i]+"*' in boolean mode)";
    	        	if (titles.length>0)
    	        	{	if (i!=titles.length-1)
    		        	{
    		        		two+=" and ";
    		        	}
    	        	}		        	
    	        }	
            }
            one+=two;
	        query = "select m.id, m.title, m.year, m.director, r.rating , GROUP_CONCAT(distinct g.name) as genres_list, GROUP_CONCAT(distinct s.name) as stars_name\r\n";
	        query += "from (select * from movies "+one+") as m"+
	    		" inner join"+
	            " stars_in_movies as sim on sim.movieId = m.id"+
	            " inner join (select * from stars) as s";
	        query+=" on s.Id = sim.starId "+
	        	" inner join" + 
	        	" ratings as r on m.id = r.movieId" + 
	        	" inner join" + 
	        	" genres_in_movies as gim on r.movieId = gim.movieId" + 
	        	" inner join" + 
	        	" genres as g on g.id = gim.genreId"+
	        	" group by m.id, r.rating limit 20 offset 0";
	        
	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        JsonArray jsonArray = new JsonArray();
	        while (resultSet.next())
            {
            	String ID = resultSet.getString("id");
            	String title_inside = resultSet.getString("title");
    			String year_inside = resultSet.getString("year");
    			String director_inside = resultSet.getString("director");
    			String rating = resultSet.getString("rating");
    			String genres_list = resultSet.getString("genres_list");
    			String stars_name = resultSet.getString("stars_name");
    			String[] parts = stars_name.split(",");
    			String[] stars_parts = stars_name.split(",");
//    			String stars_url = "";
//    			for (int i=0;i<stars_parts.length;i++)
//    			{
//    				stars_url += "<span><a href=\"star.html?star='"+stars_parts[i]+"'\">"+stars_parts[i]+"</a></span>\n";
//    			}
//    			String title_result = "<a href=\"movie.html?title='"+title_inside+"'\">"+title_inside+"</a>";
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("id", ID);
				jsonObject.addProperty("title", title_inside);
				jsonObject.addProperty("year", year_inside);
				jsonObject.addProperty("director", director_inside);
				jsonObject.addProperty("rating", rating);
				jsonObject.addProperty("genres_list", genres_list);
				jsonObject.addProperty("stars_name", stars_name);
				jsonArray.add(jsonObject);
            }
	        
            out.write(jsonArray.toString());
            connection.close();
    		resultSet.close();
        }catch (Exception e) {
        	JsonObject jsonObject = new JsonObject();	
    		jsonObject.addProperty("errorMessage", title);
			out.write(jsonObject.toString());
        }
        out.close();
    }
}

	


