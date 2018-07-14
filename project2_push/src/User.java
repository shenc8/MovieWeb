import java.util.ArrayList;

/**
 * This User class only has the username field in this example.
 * <p>
 * However, in the real project, this User class can contain many more things,
 * for example, the user's shopping cart items.
 */
public class User {

    private final String username;
    private ArrayList <String> shopping_cart;
    public User(String username) {
        this.username = username;
        this.shopping_cart = new ArrayList <String>();
    }

    public String getUsername() {
        return this.username;
    }
    
    public void add_cart(String movie) {
    	shopping_cart.add(movie);
    }
    public ArrayList<String> show_cart(){
    	return shopping_cart;
    }
    public int cart_number() {
    	return shopping_cart.size();
    }
    public void clean_cart() {
    	shopping_cart.clear();
    }
    public ArrayList <String> unique() {
    	ArrayList <String> unique_cart = new ArrayList <String>();
    	for (String movie: shopping_cart)
    		if (!unique_cart.contains(movie))
    			unique_cart.add(movie);
    	return unique_cart;
    }
    public int count(String movie) {
    	int count=0;
    	for (String i: shopping_cart)
    		if (i.equals(movie))
    			count+=1;
    	return count;
    }
    public void remove_all(String movie) {
    	
    	while(shopping_cart.contains("'"+movie+"'"))
    	{
    		shopping_cart.remove("'"+movie+"'");
    		
    	}
    }
    public void update (String movie, int quantity)
    {
    	int current= count("'"+movie+"'");
    	if (quantity>=0)
    	{
    	System.out.println("current has "+current);
    	System.out.println("quantity has "+quantity);
    	while (current<quantity)
    	{
    		shopping_cart.add("'"+movie+"'");
        	current= count("'"+movie+"'");
    	}
    	while (current>quantity)
    	{
    		shopping_cart.remove("'"+movie+"'");
    		current= count("'"+movie+"'");
    		
    	}
    	}
    }
}

