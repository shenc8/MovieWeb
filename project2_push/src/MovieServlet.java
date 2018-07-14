

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MovieServlet
 */
@WebServlet(name = "/MovieServlet", urlPatterns = "/api/movie")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		String title = request.getParameter("title");
		String add = request.getParameter("add");
//		String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
        response.setContentType("application/json");
    	PrintWriter out = response.getWriter();
        System.out.println("add is "+add);
        if (add != null)
    	{
        	JsonArray jsonArray = new JsonArray();
        	HttpSession session = request.getSession();
    		User previousUser = (User)session.getAttribute("user");
			System.out.println(previousUser.getUsername());
			previousUser.add_cart(title);
			System.out.println(previousUser.show_cart());
			session.setAttribute("user", previousUser);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("number", previousUser.cart_number());
			jsonArray.add(jsonObject);
			out.write(jsonArray.toString());
            response.setStatus(200);
    	}
        else {
        try {
//        	Class.forName("com.mysql.jdbc.Driver").newInstance();
//        	System.out.println("success");
//    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        	Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
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
    		String query = "select m.id, m.title, m.year, m.director, r.rating , GROUP_CONCAT(distinct g.name) as genres_list, GROUP_CONCAT(distinct s.name) as stars_name\r\n"+
    				"from genres as g\r\n"+
    				"inner join\r\n" + 
    				"genres_in_movies as gim on g.id = gim.genreId\r\n" + 
    				"inner join\r\n" + 
    				"ratings as r on r.movieId = gim.movieId\r\n" + 
    				"inner join\r\n" + 
    				"(select * from movies where title=?) as m on m.id = r.movieId\r\n" + 
    				"inner join\r\n" + 
    				"stars_in_movies as sim on sim.movieId = m.id\r\n" + 
    				"inner join\r\n" + 
    				"stars as s on s.Id = sim.starId\r\n" + 
    				"group by m.id, r.rating\r\n";
    		java.sql.PreparedStatement statement = connection.prepareStatement(query);
    		String new_title = new String(title);
    		if (new_title.substring(0,1).equals("'"))
  		  	{
  			  new_title = new_title.substring(1,new_title.length()-1);
  			  }
    		statement.setObject(1, new_title);
    		System.out.println(title);
    		System.out.println(statement.toString());
    		ResultSet resultSet = statement.executeQuery();
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
    			String[] stars_parts = stars_name.split(",");
    			String stars_url = "";
    			for (int i=0;i<stars_parts.length;i++)
    			{
    				stars_url += "<span><a href=\"star.html?star='"+stars_parts[i]+"'\">"+stars_parts[i]+"</a></span>\n";
    			}
    			String[] genres_parts = genres_list.split(",");
    			String genres_url = "";
    			for (int i=0;i<genres_parts.length;i++)
    			{
    				genres_url += "<span><a href=\"browse_genres_result.html?genre="+genres_parts[i]+"&page=1\">"+genres_parts[i]+"</a></span>\n";
    			}
    			
    			JsonObject jsonObject = new JsonObject();
    			HttpSession session = request.getSession();
        		User previousUser = (User)session.getAttribute("user");
    			jsonObject.addProperty("number", previousUser.cart_number());
    			jsonObject.addProperty("id", ID);
				jsonObject.addProperty("title", title_inside);
				jsonObject.addProperty("year", year_inside);
				jsonObject.addProperty("director", director_inside);
				jsonObject.addProperty("rating", rating);
				jsonObject.addProperty("genres_list", genres_url);
				jsonObject.addProperty("stars_name", stars_url);
				jsonArray.add(jsonObject);
            }
    		   out.write(jsonArray.toString());
               response.setStatus(200);
               resultSet.close();
               statement.close();
        }
        catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
		}
		out.close();
	}}


}
