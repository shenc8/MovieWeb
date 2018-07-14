

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

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
 * Servlet implementation class CheckoutServlet
 */
@WebServlet(name = "/CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String id = request.getParameter("id");
        String date = request.getParameter("date");
//        String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
        response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    JsonArray jsonArray = new JsonArray();
	    try {
//        	Class.forName("com.mysql.jdbc.Driver").newInstance();
//        	System.out.println("success");
//    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	    	Context initCtx = new InitialContext();
	        Context envCtx = (Context) initCtx.lookup("java:comp/env");
	        // Look up our data source
	        DataSource ds = (DataSource) envCtx.lookup("jdbc/MasterDB");
	        Connection connection = ds.getConnection();
    		String query = "select * from creditcards where id =?"+
    				" and firstName = ?"+
    				" and lastName = ?"+
    				" and expiration = ?";
    		System.out.println(query);
    		java.sql.PreparedStatement statement = connection.prepareStatement(query);
    		statement.setString(1,id);
    		statement.setString(2, firstName);
    		statement.setString(3, lastName);
    		statement.setString(4, date);
    		ResultSet resultSet = statement.executeQuery();
    		if (!resultSet.next())
             {
             	JsonObject jsonObject = new JsonObject();
             	jsonObject.addProperty("success","No");
            	jsonArray.add(jsonObject);}
    		 else
    		 {
    			System.out.println("correct step");
    			HttpSession session = request.getSession();
    			User previousUser = (User)session.getAttribute("user");
    			String username = previousUser.getUsername();
    			java.sql.PreparedStatement m = connection.prepareStatement("select id from sales order by -id");
    			ResultSet ID_set = m.executeQuery();
    			ID_set.next();
    			int ID = Integer.parseInt(ID_set.getString("id"));
    			ID_set.close();
    			m.close();
    			Date d = new Date();
    			String order = "<p>Order Confirmation</p>";
    			for (String movie: previousUser.unique())
    			{
    				ID+=1;
    				System.out.println(previousUser.count(movie));
    				String new_query = "select m.id as mid ,c.id as cid from movies as m , customers as c where title =?"+
    						" and email = ?" ;
    				java.sql.PreparedStatement n = connection.prepareStatement(new_query);
    				String new_title = new String(movie);
    	    		if (new_title.substring(0,1).equals("'"))
    	  		  	{
    	  			  new_title = new_title.substring(1,movie.length()-1);
    	  			  }
    				n.setString(1, new_title);
    				n.setString(2, username);
    				System.out.println(new_query);
    				System.out.println(n.toString());
    				ResultSet new_set = n.executeQuery();
    				new_set.next();
    				String mid = new_set.getString("mid");
                	String cid = new_set.getString("cid");
                	System.out.println("ID"+ID+" mid"+mid+" cid"+cid);
                	order+="#"+ID+" ";
                	order+="<p>Customer ID: "+cid+" Movie ID:"+mid+" Quantity: "+previousUser.count(movie)+"</p>";
                	SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
                	System.out.println(ft.format(d).toString());
                	String insert_query="insert into sales (id,customerId,movieId,saleDate) values(?,?,?,?)";
                	java.sql.PreparedStatement insert_n = connection.prepareStatement(insert_query);
                	insert_n.setString(1, ""+ID+"");
                	insert_n.setString(2, cid);
                	insert_n.setString(3, mid);
                	insert_n.setString(4, ft.format(d).toString());
                	insert_n.executeUpdate();
                	System.out.println(insert_n.toString());
                	insert_n.close();
    				new_set.close();
    				n.close();
    			}
    			previousUser.clean_cart();
    			System.out.println(order);
    			session.setAttribute("user", previousUser);
    			JsonObject jsonObject = new JsonObject();
              	jsonObject.addProperty("success","Yes");
              	jsonObject.addProperty("message",order);
             	jsonArray.add(jsonObject);
    			 }
	    out.write(jsonArray.toString());
        response.setStatus(200);
        resultSet.close();
        statement.close();
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
