package org.ilisi;

import org.ilisi.client.Proxy;
import org.ilisi.server.Forum;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class ForumImpl extends UnicastRemoteObject implements Forum {

   // An AtomicInteger is used to generate unique identifiers for users of the forum
   // Can handle concurrent access (Thread-safe)
   private final AtomicInteger id;
   // A Map is used to store the connected users of the forum
   private final Map<Integer, Proxy> users;
   public ForumImpl() throws RemoteException {
      users = new Hashtable<>();
      id = new AtomicInteger(0);
   }

   // The method entrer() is used to register a user to the forum
   @Override
   public int entrer(Proxy proxy) throws RemoteException {
      int i = id.incrementAndGet();
      users.put(i, proxy);
      return i;
   }

   // The method dire() is used to broadcast a message to all users of the forum
   @Override
   public void dire(int id, String msg) throws RemoteException {
      List<Proxy> proxies = new Vector<>(users.values());
      for (Proxy proxy : proxies) {
         if (!proxy.equals(users.get(id)))
            proxy.ecouter(msg);
      }
   }

   // The method qui() is used to get the list of connected users
   @Override
   public String qui() throws RemoteException {
      return String
              .join(", ", users.keySet()
                      .toString());
   }

   // The method quiter() is used to unregister a user from the forum
   @Override
   public void quiter(int id) throws RemoteException {
      if (users.get(id) == null)
         throw new RemoteException("Utilisateur inconnu");
      users.remove(id);
   }
}
