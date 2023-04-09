package org.ilisi;

import org.ilisi.client.Proxy;
import org.ilisi.client.User;
import org.ilisi.server.Forum;
import org.ilisi.ui.ChatInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;

public class UserImpl extends UnicastRemoteObject implements User, Proxy {
   private final ChatInterface chatInterface;
   private final int id;
   private final String name;

   public UserImpl(Forum forum, String name) throws RemoteException {
      this.name = name;
      this.id = forum.entrer(this); // register to the forum
      chatInterface = new ChatInterface(id, name, forum);
      chatInterface.setTitle("Remote Forum - " + this);
      chatInterface.setVisible(true);
   }

   @Override
   public void ecrire(String msg) {
      System.out.println("Message envoye : " + msg);
      chatInterface.addChatMessage(msg, true);
   }

   @Override
   public void ecouter(String msg) throws RemoteException {
      System.out.println("Message recu : " + msg);
      chatInterface.addChatMessage(msg, false);
   }

   @Override
   public String toString() {
      return id + ":" + name;
   }

   public int getId() {
      return id;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof UserImpl)) return false;
      if (!super.equals(o)) return false;

      return id == ((UserImpl) o).id;
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), id);
   }
}

