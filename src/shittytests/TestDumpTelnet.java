package shittytests;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class TestDumpTelnet {
   public static void main(String[] args){
	   Socket s;
	   try {
		   s = new Socket("localhost",1234);

		   int c;
		   while( true )
		   {
			   c = s.getInputStream().read(); 
			   System.out.println(c);
		   }
	   } catch (UnknownHostException e) {
		   e.printStackTrace();
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
   }
}
