import com.google.gson.JsonObject;
import com.mysql.jdbc.PreparedStatement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

//
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(password);
        String gRecaptcha = request.getParameter("g-recaptcha-response");
        try {
        	//RecaptchaVerifyUtils.verify(gRecaptcha,RecaptchaConstants.SECRET_KEY);
        	int whether = verifyCredentials(username,password);
        	System.out.println(whether);
        	if (whether!=0)
        	{
        		
    			request.getSession().setAttribute("user", new User(username));
    	        JsonObject responseJsonObject = new JsonObject();
    	        if (whether==1)
    	        {
	    	        responseJsonObject.addProperty("status", "success");
	    	        responseJsonObject.addProperty("message", "user_success");
	    	        response.getWriter().write(responseJsonObject.toString());}
    	        else
    	        {
    	        	responseJsonObject.addProperty("status", "success");
	    	        responseJsonObject.addProperty("message", "TA_success");
	    	        response.getWriter().write(responseJsonObject.toString());
    	        }
        	}
        	else
        	{
        		JsonObject responseJsonObject = new JsonObject();
        		responseJsonObject.addProperty("status", "fail");
  				responseJsonObject.addProperty("message", "Login Information Wrong");
  				response.getWriter().write(responseJsonObject.toString());
        	}
        	
        }
        catch (Exception e) {
        		System.out.print(e.getMessage());
        		JsonObject responseJsonObject = new JsonObject();
        		responseJsonObject.addProperty("status", "false");
        		responseJsonObject.addProperty("message", "Recaptcha Not Pass");
        		response.getWriter().write(responseJsonObject.toString());		
    }
    
         
        }
    private static int verifyCredentials(String email, String password) throws Exception {
		
//		String loginUser = "mytestuser";
//		String loginPasswd = "mypassword";
//		String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
		int return_value = 0;
//		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
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
		boolean success = false;
		String query = "select password from customers where email=?";
		
		java.sql.PreparedStatement statement = connection.prepareStatement(query);
		statement.setString(1,email);
		System.out.println(statement.toString());
		ResultSet rs = statement.executeQuery();
		String TAquery = "select password from employees where email=?";
		System.out.println(TAquery);
		java.sql.PreparedStatement TAstatement = connection.prepareStatement(TAquery);
		TAstatement.setString(1,email);
		ResultSet TA_rs = TAstatement.executeQuery();
		if (rs.next()) {
		    // get the encrypted password from the database
			String encryptedPassword = rs.getString("password");
			// use the same encryptor to compare the user input password with encrypted password stored in DB
			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);	
		}
		if (success==true)
		{
			return_value = 1;
		}
		success=false;
		if (TA_rs.next()) {
		    // get the encrypted password from the database
			String encryptedPassword = TA_rs.getString("password");
			
			// use the same encryptor to compare the user input password with encrypted password stored in DB
			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
			System.out.println(success);
		}
		if (success==true)
		{
			return_value = 2;
		}
		TA_rs.close();
		rs.close();
		statement.close();
		connection.close();
		
		System.out.println("verify " + email + " - " + password);

		return return_value;
	}

}
  
