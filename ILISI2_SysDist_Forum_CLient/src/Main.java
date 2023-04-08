import org.ilisi.UserImpl;
import org.ilisi.server.Forum;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {
   private static final String default_host = "localhost";
   private static final int default_port = 9999;
   private static final String default_ref = "forum";
   private static final String default_name = "UNNAMED";


   public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException {
      System.out.println("Client is Running.");
      String host = default_host;
      int port = default_port;
      String ref = default_ref;
      String name = default_name;

      if (args.length == 4) {
         name = args[0];
         host = args[1];
         port = Integer.parseInt(args[2]);
         ref = args[3];
      } else if (args.length == 0) {
         System.out.println("Warning : Run with 4 arguments to specify values :");
         System.out.println("Usage: java Main [name host port ref]");
         System.out.println("Warning : Using default values.");
         System.out.println("Info : Default values are :");
         System.out.println("host : " + default_host);
         System.out.println("port : " + default_port);
         System.out.println("ref : " + default_ref);
         System.out.println("name : " + default_name);
      } else {
         System.out.println("Usage: java Main [name host port ref]");
         System.exit(1);
      }
      Forum forum = (Forum) Naming.lookup(String.format("rmi://%s:%d/%s", host, port, ref));
      System.out.println("Forum found in registry.");

      UserImpl user = new UserImpl(forum, name);
      System.out.println("User ready.");
   }
}