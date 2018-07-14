

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet(name = "/SearchServlet" , urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("title", title);
        responseJsonObject.addProperty("year", year);
        responseJsonObject.addProperty("director", director);
        responseJsonObject.addProperty("star", star);
        response.getWriter().write(responseJsonObject.toString());
	}
}
