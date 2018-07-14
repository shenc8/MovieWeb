

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class IndexServlet
 */
@WebServlet(name = "/IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("This step");
		String title = request.getParameter("title");
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("title", title);
        response.getWriter().write(responseJsonObject.toString());
	}

	

}
