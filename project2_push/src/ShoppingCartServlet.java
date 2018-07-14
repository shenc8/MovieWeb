

import java.io.IOException;
import java.util.ArrayList;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ShoppingCartServlet
 */
@WebServlet(name = "/ShoppingCartServlet", urlPatterns = "/api/shopping_cart")
public class ShoppingCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		String title = request.getParameter("title");
		String quantity = request.getParameter("quantity");
		String delete = request.getParameter("delete");
		System.out.println("title->"+title);
		System.out.println("quantity->"+quantity);
		System.out.println("delete->"+delete);
		HttpSession session = request.getSession();
		User previousUser = (User)session.getAttribute("user");
		System.out.println("before "+previousUser.show_cart());
		if (quantity != null)
		{
			previousUser.update(title, Integer.parseInt(quantity));
			session.setAttribute("user", previousUser);
		}
		System.out.println("after "+previousUser.show_cart());
    	PrintWriter out = response.getWriter();
		JsonArray jsonArray = new JsonArray();
		ArrayList<String> unique_list = previousUser.unique();
		for (String name: unique_list)
		{
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("title", name);
			jsonObject.addProperty("quantity", previousUser.count(name));
			jsonArray.add(jsonObject);
		}
		out.write(jsonArray.toString());
        response.setStatus(200);
        out.close();
	}

}
