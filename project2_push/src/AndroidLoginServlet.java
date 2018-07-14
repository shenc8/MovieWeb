import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/*
 * We create a separate android login Servlet here because
 *   the recaptcha secret key for web and android are different.
 * 
 */
@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/android-login")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AndroidLoginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("user");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        
        Map<String, String[]> map = request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println(key);
            System.out.println(map.get(key)[0]);
        }
        
        // verify recaptcha first
        try {
           //RecaptchaVerifyUtils.verify(gRecaptchaResponse, RecaptchaConstants.ANDROID_SECRET_KEY);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        
        // then verify username / password
        JsonObject loginResult = null;
		try {
			loginResult = LoginVerifyUtils.verifyUsernamePassword(username, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if (loginResult.get("status").getAsString().equals("success")) {
            // login success
            request.getSession().setAttribute("user", new User(username));
            response.getWriter().write(loginResult.toString());
        } else {
            response.getWriter().write(loginResult.toString());
        }

    }

}
