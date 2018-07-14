

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Types;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;
import com.sun.org.apache.bcel.internal.generic.Type;

/**
 * Servlet implementation class InsertMovieServlet
 */
@WebServlet(name = "/InsertMovieServlet",urlPatterns = "/api/insert_movie")
public class InsertMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
//			String loginUser = "mytestuser";
//		    String loginPasswd = "mypassword";
//		    String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
		    String title = request.getParameter("title");
	        String year = request.getParameter("year");
	        String director = request.getParameter("director");
	        String genre = request.getParameter("genre");
	        String star = request.getParameter("star");
	        String birth = request.getParameter("star_birth");
    		if (title == "" || year == "" || director == "" || genre == "" || star == "")
    		{
    			JsonObject jsonObject = new JsonObject();
              	jsonObject.addProperty("success","No");
              	jsonObject.addProperty("message","All attributes except Star BirthYear should not be NULL");
              	response.getWriter().write(jsonObject.toString());
    		}
    		else
    		{
    			
    			Class.forName("com.mysql.jdbc.Driver").newInstance();
//	    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    			Context initCtx = new InitialContext();
    	        Context envCtx = (Context) initCtx.lookup("java:comp/env");
    	        // Look up our data source
    	        DataSource ds = (DataSource) envCtx.lookup("jdbc/MasterDB");
    	        Connection connection = ds.getConnection();
	    		CallableStatement m = connection.prepareCall("{call add_movie(?,?,?,?,?,?,?)}");
	    		m.setString(1, title);
	    		m.setInt(2, Integer.parseInt(year));
	    		m.setString(3,director);
	    		m.setString(4,star);
	    		if (birth=="")
				{
					m.setNull(5, Types.INTEGER);
				}
	    		else
	    		{
	    			m.setInt(5, Integer.parseInt(year));
	    		}
	    		m.setString(6, genre);
	    		m.registerOutParameter(7, Types.VARCHAR);
	    		m.execute();
	    		String message = m.getString(7);
	    		System.out.println(message);
	    		JsonObject jsonObject = new JsonObject();
              	jsonObject.addProperty("success","Yes");
              	jsonObject.addProperty("message",message);
              	response.getWriter().write(jsonObject.toString());
              	System.out.println(m.toString());
    		}
		}
		catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
          	jsonObject.addProperty("success","Yes");
          	jsonObject.addProperty("message","Attribute type is wrong, please check");
          	response.getWriter().write(jsonObject.toString());
		}
	}

}
