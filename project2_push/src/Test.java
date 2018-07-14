import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Test {
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		long TJ_sum = 0; 
		long TS_sum = 0;
		int num = 0;
		BufferedReader br = new BufferedReader(new FileReader("C:/Users/user/desktop/single_3_time.txt"));
		for(String line =""; (line = br.readLine()) != null; ) {
			System.out.println(line);
			String[] tokens = line.split(" ");
			TJ_sum += Long.parseLong(tokens[1]);
			TS_sum += Long.parseLong(tokens[3]);
			num++;   
		}
		
		System.out.println("Num "+num);
		System.out.println("TJ Average "+TJ_sum/(num*1000000.0)+" TS Average "+TS_sum/(num*1000000.0));
	}

}
