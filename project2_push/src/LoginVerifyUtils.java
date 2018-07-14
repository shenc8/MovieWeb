import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.jasypt.util.password.StrongPasswordEncryptor;

import com.google.gson.JsonObject;

public class LoginVerifyUtils {
    
    public static JsonObject verifyUsernamePassword(String username, String password) throws Exception {
        // after recatpcha verfication, then verify username and password
    	boolean success = verifyCredentials(username,password);
        if (success==true) {
            // login success:

            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");
            
            return responseJsonObject;

        } else {
            // login fail
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", username + " "+password);

            return responseJsonObject;
        }
    }
private static boolean verifyCredentials(String email, String password) throws Exception {
		
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
		Statement statement = connection.createStatement();

		String query = String.format("SELECT * from customers where email='%s'", email);

		ResultSet rs = statement.executeQuery(query);

		boolean success = false;
		String encryptedPassword = "fail";
		if (rs.next()) {
		    // get the encrypted password from the database
			encryptedPassword = rs.getString("password");
			
			// use the same encryptor to compare the user input password with encrypted password stored in DB
			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
		}

		rs.close();
		statement.close();
		connection.close();
		
		System.out.println("verify " + email + " - " + password);

		return success;
	}
}
