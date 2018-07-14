

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.Object;
/**
 * Servlet implementation class SearchServlet
 */
@WebServlet(name = "/SearchResultServlet", urlPatterns = "/api/search_result")
public class SearchResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public SearchResultServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long TJ_Time = 0,TS_Time = 0;
		long TS_startTime = System.nanoTime();
		String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String page = request.getParameter("page");
//        String type = request.getParameter("type");
//        System.out.println("type is "+type);
//        String loginUser = "mytestuser";
//        String loginPasswd = "mypassword";
//        String loginUrl = "jdbc:mysql://ec2-18-216-229-230.us-east-2.compute.amazonaws.com:3306/moviedb";
//        response.setContentType("text/html");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
//        out.println("<html><head><title>MoviePage</title></head>");
//        out.println("<body><h1>MovieDB</h1>");
        try {
//        	Class.forName("com.mysql.jdbc.Driver").newInstance();
//        	System.out.println("success");
//    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		String query = "";
    		ArrayList<String> value = new ArrayList<String>();
    		int count = 0;
    		String one ="";
	        if (title != "" || year != "" || director != "")
	        {
	        	one += "where ";
	        }
	        if (title != "") {
	        	String[] titles = title.split(" ");
	        	for (int i = 0;i<titles.length;i++)
		        {	one += " match (title) against (? in boolean mode) ";
		        	value.add(titles[i]+"*");
		        	count += 1;
		        	if (titles.length>0)
		        	{	if (i!=titles.length-1)
			        	{
			        		one+=" and ";
			        	}
		        	}
		        }
	        	if (year != "" || director != "")
	        		one += " and ";
	        }
	        if (year != "") {
	        	one += "year = ?";
	        	value.add(year);
	        	if (director != "")
	        		one += " and ";
	        }
	        if (director != "") {
	        	one += "director like ?";
	        	value.add("%"+director+"%");
	        }
	        String two ="";
	        if (star != ""){
	        	two += "where name like ?";
	        	value.add("%"+star+"%");
	        }
	        query = "select m.id, m.title, m.year, m.director, r.rating , GROUP_CONCAT(distinct g.name) as genres_list, GROUP_CONCAT(distinct s.name) as stars_name\r\n";
	        query += "from (select * from movies "+one+") as m"+
	    		" inner join"+
	            " stars_in_movies as sim on sim.movieId = m.id"+
	            " inner join (select * from stars "+two+") as s";
	        query+=" on s.Id = sim.starId "+
	        	" inner join" + 
	        	" ratings as r on m.id = r.movieId" + 
	        	" inner join" + 
	        	" genres_in_movies as gim on r.movieId = gim.movieId" + 
	        	" inner join" + 
	        	" genres as g on g.id = gim.genreId"+
	        	" group by m.id, r.rating limit 20 offset ?";
	        long TJ_startTime = System.nanoTime();
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
            java.sql.PreparedStatement statement = connection.prepareStatement(query);
            java.sql.PreparedStatement whetherstatement = connection.prepareStatement(query);
            for (int i=0;i<value.size();i++)
            {
            	statement.setString(i+1, value.get(i));
            	whetherstatement.setString(i+1, value.get(i));
            }
            statement.setInt(value.size()+1, (Integer.parseInt(page)-1)*20);
            whetherstatement.setInt(value.size()+1, Integer.parseInt(page)*20);
            System.out.println("the statement is "+statement.toString());
            ResultSet whetherSet = whetherstatement.executeQuery();
            ResultSet resultSet = statement.executeQuery();
            long TJ_endTime = System.nanoTime();
            TJ_Time = TJ_endTime - TJ_startTime; 
            JsonArray jsonArray = new JsonArray();
            if (whetherSet.next() == false)
            {
            	JsonObject jsonObject = new JsonObject();
            	jsonObject.addProperty("has_next", "N");
            	jsonObject.addProperty("title",title);
            	jsonObject.addProperty("year",year);
            	jsonObject.addProperty("director",director);
            	jsonObject.addProperty("star",star);
            	jsonObject.addProperty("next_value",Integer.parseInt(page)+1);
            	jsonArray.add(jsonObject);
            	}
            else
            {
            	JsonObject jsonObject = new JsonObject();
            	jsonObject.addProperty("has_next", "Y");
            	jsonObject.addProperty("title",title);
            	jsonObject.addProperty("year",year);
            	jsonObject.addProperty("director",director);
            	jsonObject.addProperty("star",star);
            	jsonObject.addProperty("next_value",Integer.parseInt(page)+1);
            	jsonArray.add(jsonObject);
            }
            whetherSet.close();
            while (resultSet.next())
            {
            	String ID = resultSet.getString("id");
            	String title_inside = resultSet.getString("title");
    			String year_inside = resultSet.getString("year");
    			String director_inside = resultSet.getString("director");
    			String rating = resultSet.getString("rating");
    			String genres_list = resultSet.getString("genres_list");
    			String stars_name = resultSet.getString("stars_name");
    			String[] parts = stars_name.split(",");
    			String[] stars_parts = stars_name.split(",");
    			String stars_url = "";
    			for (int i=0;i<stars_parts.length;i++)
    			{
    				stars_url += "<span><a href=\"star.html?star='"+stars_parts[i]+"'\">"+stars_parts[i]+"</a></span>\n";
    			}
    			String title_result = "<a href=\"movie.html?title='"+title_inside+"'\">"+title_inside+"</a>";
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("id", ID);
				jsonObject.addProperty("title", title_result);
				jsonObject.addProperty("year", year_inside);
				jsonObject.addProperty("director", director_inside);
				jsonObject.addProperty("rating", rating);
				jsonObject.addProperty("genres_list", genres_list);
				jsonObject.addProperty("stars_name", stars_url);
				jsonArray.add(jsonObject);
            }
            out.write(jsonArray.toString());
            response.setStatus(200);
    		resultSet.close();
    		statement.close();
    		whetherstatement.close();
    		connection.close();
    		
	}
        catch (Exception e) {
        	JsonObject jsonObject = new JsonObject();	
    		jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
//			response.setStatus(500);		
    }
    out.close();
    System.out.println("elapsed time is " + TJ_Time);
    String contextPath = getServletContext().getRealPath("/");
    String FilePath=contextPath+"single_3_time.txt";
    System.out.println(FilePath);
    File file = new File(FilePath);
    if (!file.exists())
    {
    	file.createNewFile();
    }
    FileWriter fw = new FileWriter(file, true);
    BufferedWriter bw = new BufferedWriter(fw);
    PrintWriter pw = new PrintWriter(bw);
    long TS_endTime = System.nanoTime();
    TS_Time = TS_endTime-TS_startTime;
    pw.println("TJ: "+TJ_Time+" "+"TS: "+TS_Time);
    pw.close();
	}}
	

