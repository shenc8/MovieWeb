

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

import com.google.gson.JsonObject;

/**
 * Servlet implementation class MetadataServlet
 */
@WebServlet("/_dashboard/metadata.html")
public class MetadataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] tables = {"creditcards","customers","employees","genres","genres_in_movies","movies","ratings","sales","stars","stars_in_movies"};
//		String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
        response.setContentType("text/html"); 
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>MetaData</title></head>");
        try {
//    		Class.forName("com.mysql.jdbc.Driver").newInstance();
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
    		String query = "SELECT column_name, column_type "+
    				  "FROM information_schema.columns "+ 
    				  "WHERE (table_name = ?)";
    		out.println("<body>");
    		for (String table : tables)
    		{
    			java.sql.PreparedStatement statement = connection.prepareStatement(query);
    			
    			statement.setString(1,table);
    			ResultSet resultSet = statement.executeQuery();		
        		out.println("<table id=title_table class='table table-striped'>");
        		out.println("<thead>");
        		out.println("<tr >");
        		out.println("<th>Attribute</th>");
        		out.println("<th>Attribute Type</th>");
        		out.println("</tr>");
        		out.println("</thead>");
    			while (resultSet.next())
                {
                	
    				String column_name = resultSet.getString("column_name");
        			String column_type = resultSet.getString("column_type");
        			out.println("<tr align=\"middle\">");
//        			if (time==0)
//        			{	out.println("<td>" + table + "</td>");}
//        			else
//        			{
//        				out.println("<td></td>");
//        			}
        			out.println("<td>" + column_name + "</td>");
        			out.println("<td>" + column_type + "</td>");
//        			time++;
        			out.println("</tr>");
                }
    			out.println("<h1 align=\"middle\">"+table+"</h1>"); 
        		resultSet.close();
    			statement.close();
    		}
    		out.println("</table>");
    		out.println("</body>");
    		out.println("<style>\r\n" + 
    				"body {\r\n" + 
    				"   background-image: url(\"backgound.jpg\");\r\n" + 
    				"   background-repeat:no-repeat ;\r\n" + 
    				"   background-size:100% 100%; \r\n" + 
    				"   background-attachment: fixed;     \r\n" + 
    				"}\r\n" + 
    				"</style>");
    		out.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
        }
        catch
	    (Exception e) {
	    	JsonObject jsonObject = new JsonObject();	
    		jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			response.setStatus(500);
        }
        out.close();
	}


}
