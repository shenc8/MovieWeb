

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
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class BrowseTitleServlet
 */
@WebServlet(name = "/BrowseTitleServlet" ,urlPatterns = "/api/browse_title_result")
public class BrowseTitleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String letter = request.getParameter("title");
//		String loginUser = "mytestuser";
//		String loginPasswd = "mypassword";
		String page = request.getParameter("page");
//	    String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
	    response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
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
    		String query = "select m.id, m.title, m.year, m.director, r.rating , GROUP_CONCAT(distinct g.name) as genres_list, GROUP_CONCAT(distinct s.name) as stars_name\r\n" + 
    				"from genres as g\r\n" + 
    				"inner join\r\n" + 
    				"genres_in_movies as gim on g.id = gim.genreId\r\n" + 
    				"inner join\r\n" + 
    				"ratings as r on r.movieId = gim.movieId\r\n" + 
    				"inner join\r\n" + 
    				"(select * from movies where title like ?) as m on m.id = r.movieId\r\n" + 
    				"inner join\r\n" + 
    				"stars_in_movies as sim on sim.movieId = m.id\r\n" + 
    				"inner join\r\n" + 
    				"stars as s on s.Id = sim.starId\r\n" + 
    				"group by m.id, r.rating\r\n" + 
    				" limit 20 offset ?";
    		java.sql.PreparedStatement statement = connection.prepareStatement(query);
    		statement.setString(1,letter+'%');
    		statement.setInt(2,(Integer.parseInt(page)-1)*20);
    		ResultSet resultSet = statement.executeQuery();
    		java.sql.PreparedStatement whetherstatement = connection.prepareStatement(query);
    		whetherstatement.setString(1,letter+'%');
    		whetherstatement.setInt(2,(Integer.parseInt(page))*20);
            ResultSet whetherSet = whetherstatement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            if (!whetherSet.next())
            {
            	JsonObject jsonObject = new JsonObject();
            	jsonObject.addProperty("has_next", "N");
            	jsonObject.addProperty("title",letter);
            	jsonObject.addProperty("next_value",Integer.parseInt(page)+1);
            	jsonArray.add(jsonObject);
            	}
            else
            {
            	JsonObject jsonObject = new JsonObject();
            	jsonObject.addProperty("has_next", "Y");
            	jsonObject.addProperty("title",letter);
            	jsonObject.addProperty("next_value",Integer.parseInt(page)+1);
            	jsonArray.add(jsonObject);
            }
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
    			String title_result = "<a href=\"movie.html?title='"+title_inside+"'\">"+title_inside+"</a>";
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("id", ID);
				jsonObject.addProperty("title", title_result);
				jsonObject.addProperty("year", year_inside);
				jsonObject.addProperty("director", director_inside);
				jsonObject.addProperty("rating", rating);
				jsonObject.addProperty("genres_list", genres_list);
				jsonObject.addProperty("stars_name", stars_url);
				jsonArray.add(jsonObject);
            }
            out.write(jsonArray.toString());
            response.setStatus(200);
            resultSet.close();
            statement.close();
            whetherstatement.close();
	    }
	    catch
	    (Exception e) {
	    	JsonObject jsonObject = new JsonObject();	
    		jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);		
    }
    		
	}


}
