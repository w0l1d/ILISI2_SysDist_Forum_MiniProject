import org.ilisi.ForumImpl;
import org.ilisi.server.Forum;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
   private static final String default_host = "localhost";
   private static final int default_port = 9999;
   private static final String default_ref = "forum";

   public static void main(String[] args) throws RemoteException, MalformedURLException {
      System.out.println("Server is Running.");
      String host = default_host;
      int port = default_port;
      String ref = default_ref;

      if (args.length == 4) {
         host = args[0];
         port = Integer.parseInt(args[1]);
         ref = args[2];
      } else if (args.length == 0) {
         System.out.println("Warning : Run with 3 arguments to specify values :");
         System.out.println("Usage: java Main [host port ref]");
         System.out.println("Warning : Using default values.");
         System.out.println("Info : Default values are :");
         System.out.println("host : " + default_host);
         System.out.println("port : " + default_port);
         System.out.println("ref : " + default_ref);
      } else {
         System.out.println("Usage: java Main [host port ref]");
         System.exit(1);
      }
      LocateRegistry.createRegistry(port);
      System.out.println("RMI registry ready.");

      Forum forum = new ForumImpl();
      System.out.println("Forum ready.");

      Naming.rebind(String.format("rmi://%s:%s/%s", host, port, ref), forum);
      System.out.println("Forum bound in registry.");

      System.out.println("Server ready.");
   }
}