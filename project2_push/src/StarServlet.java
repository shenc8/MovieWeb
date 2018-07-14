

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
 * Servlet implementation class StarServlet
 */
@WebServlet(name = "/StarServlet", urlPatterns = "/api/star")
public class StarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		String star = request.getParameter("star");
//		String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
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
    		Statement statement = connection.createStatement();
    		String query = "select s.name, s.birthYear, GROUP_CONCAT(distinct m.title) as movies\r\n" + 
    		"from (select * from stars where name = "+star+") as s\r\n" + 
    		"inner join\r\n" + 
    		"stars_in_movies as sim on s.Id = sim.starId\r\n" + 
    		"inner join\r\n" + 
    		"movies as m on sim.movieId = m.id\r\n" + 
    		"group by s.name, s.birthYear";
    		System.out.println(query);
    		ResultSet resultSet = statement.executeQuery(query);
    		JsonArray jsonArray = new JsonArray();
    		while (resultSet.next())
            {
    			String birth = resultSet.getString("birthYear");
    			String movies_list = resultSet.getString("movies");
    			String[] movies_parts = movies_list.split(",");
    			String movies_url = "";
    			for (int i=0;i<movies_parts.length;i++)
    			{
    				movies_url+="<a href=\"movie.html?title='"+movies_parts[i]+"'\">"+movies_parts[i]+"</a> ";
    			}
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("star", star);
				jsonObject.addProperty("birth", birth);
				jsonObject.addProperty("movies", movies_url);
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
	}


}
