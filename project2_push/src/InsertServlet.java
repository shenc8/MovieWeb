

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;
import com.mysql.jdbc.StringUtils;

import jdk.internal.org.objectweb.asm.Type;

/**
 * Servlet implementation class InsertServlet
 */
@WebServlet(name = "/InsertServlet" , urlPatterns = "/api/insert_star")
public class InsertServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
        String birthday = request.getParameter("year");
//        String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
        response.setContentType("application/json");
        try {
        	if (name=="")
        	{
        		JsonObject jsonObject = new JsonObject();
              	jsonObject.addProperty("success","No");
              	jsonObject.addProperty("message","Star Name should not be NULL");
              	response.getWriter().write(jsonObject.toString());
        	}
        	else
        	{
//        		Class.forName("com.mysql.jdbc.Driver").newInstance();
//	    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		Context initCtx = new InitialContext();
                Context envCtx = (Context) initCtx.lookup("java:comp/env");
                // Look up our data source
                DataSource ds = (DataSource) envCtx.lookup("jdbc/MasterDB");
                Connection connection = ds.getConnection();
	    		java.sql.PreparedStatement m = connection.prepareStatement("select max(id) from stars");
	    		ResultSet ID_set = m.executeQuery();
				ID_set.next();
				System.out.println("This step");
				String old_ID=ID_set.getString("max(id)");
				System.out.println("ID is "+old_ID);
				int ID_number =Integer.parseInt(old_ID.substring(2))+1;
				System.out.println(old_ID.substring(0,2)+"--->"+ID_number);
				String ID = old_ID.substring(0,2)+ID_number;
				System.out.println(ID);
				ID_set.close();
				m.close();
				String insert_query = "insert into stars (id,name,birthYear) values(?,?,?)";
				java.sql.PreparedStatement n = connection.prepareStatement(insert_query);
				n.setString(1, ID);
				n.setString(2, name);
				if (birthday=="")
				{
					n.setNull(3,Type.INT);
				}
				else
				{
					n.setInt(3, Integer.parseInt(birthday));
				}
				n.executeUpdate();
				JsonObject jsonObject = new JsonObject();
              	jsonObject.addProperty("success","Yes");
              	jsonObject.addProperty("message","Star Information Updated");
              	System.out.println("insert_query is"+n.toString());
              	response.getWriter().write(jsonObject.toString());
              	n.close();
        	}}
        catch (Exception e) {
        	JsonObject jsonObject = new JsonObject();
          	jsonObject.addProperty("success","No");
          	jsonObject.addProperty("message","BirthYear should be either int or NULL");
          	response.getWriter().write(jsonObject.toString());
        }
        }

}
