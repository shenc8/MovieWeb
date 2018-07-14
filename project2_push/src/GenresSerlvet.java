

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
import com.mysql.jdbc.PreparedStatement;

/**
 * Servlet implementation class GenresSerlvet
 */
@WebServlet(name ="GenresServlet", urlPatterns = "/api/browse_genres")
public class GenresSerlvet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
//        String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
//        	Class.forName("com.mysql.jdbc.Driver").newInstance();
        	System.out.println("success");
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
            String query = "select name from genres";
            java.sql.PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String genre = rs.getString("name");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("genre", genre);
                jsonArray.add(jsonObject);
            }
            
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
        out.close();

    }

}
