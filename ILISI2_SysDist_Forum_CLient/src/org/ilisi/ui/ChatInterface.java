package org.ilisi.ui;

import org.ilisi.server.Forum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Arrays;

public class ChatInterface extends JFrame implements ActionListener {
   // Interval in milliseconds for updating connected users
   private static final int CONNECTED_USERS_UPDATE_INTERVAL = 2500;
   private final JScrollPane chatScrollPane;    private final JTextField inputTextField;
   private final JButton sendButton;            private final JPanel chatPanel;
   private final Forum forum;                   private final JLabel connectedUsersLabel;
   private final int id;                        private final String username;

   public ChatInterface(int id, String username, Forum forum) {
      this.forum = forum;
      this.id = id;
      this.username = username;

      // Set up the main window
      setTitle("Chat Interface");
      setSize(710, 500);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
      addWindowListener(new java.awt.event.WindowAdapter() {
         @Override
         public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            try {
               forum.quiter(id);
            } catch (RemoteException e) {
               alertError("Error while quiting forum", e.getMessage());
            }
         }
      });

      // Create a top panel for navbar
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.setBackground(Color.WHITE); // Set background color

      // Create a label to display connected users
      connectedUsersLabel = new JLabel(getConnectedUsersText("Loading..."));
      topPanel.add(connectedUsersLabel, BorderLayout.NORTH); // Add the label to the top of the window
      connectedUsersLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding
      topPanel.add(connectedUsersLabel, BorderLayout.WEST); // Add the label to the left of the top panel

      // Create a label to display current user
      JLabel currentUserLabel = new JLabel(getCurrUserLblText(id, username));
      currentUserLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding
      topPanel.add(currentUserLabel, BorderLayout.EAST); // Add the label to the right of the top panel

      // Add the top panel to the top of the window
      add(topPanel, BorderLayout.NORTH);


      // Timer to periodically update connected users
      new Timer(CONNECTED_USERS_UPDATE_INTERVAL, e -> updateConnectedUsersLabel()).start();

      // Create the chat panel to hold chat messages
      chatPanel = new JPanel();
      chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
      chatScrollPane = new JScrollPane(chatPanel);
      chatScrollPane.setViewportBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      // Add the chat scroll pane to the center of the window
      add(chatScrollPane, BorderLayout.CENTER);

      // Create a JTextField for input text
      inputTextField = new JTextField();
      inputTextField.setFont(new Font("Arial", Font.PLAIN, 16));
      inputTextField.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(Color.GRAY, 2), // Add a gray border
              BorderFactory.createEmptyBorder(5, 10, 5, 10))); // Add padding to text field

      // Create a JButton for send button
      sendButton = new JButton("Send");
      sendButton.setFont(new Font("Arial", Font.BOLD, 16));
      sendButton.setBackground(new Color(163, 216, 208)); // Set background color to a shade of blue
      sendButton.setForeground(Color.DARK_GRAY); // Set text color
      sendButton.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(new Color(163, 216, 208), 2), // Add a blue border
              BorderFactory.createEmptyBorder(5, 10, 5, 10))); // Add padding to button
      sendButton.setFocusPainted(false); // Remove focus painting
      sendButton.addActionListener(this);


      // Add the input text field and send button to a panel
      JPanel inputPanel = new JPanel(new BorderLayout());
      inputPanel.add(inputTextField, BorderLayout.CENTER);
      inputPanel.add(sendButton, BorderLayout.EAST);

      // Add the input panel to the main window
      add(inputPanel, BorderLayout.SOUTH);
   }

   private static String getCurrUserLblText(int id, String username) {
      return "<html><div style='text-align:center;'><font size='5' color='#A3D8D0FF'>Current User:</font><br>"
              + id + ":" + username + "</div></html>";
   }

   private static String getConnectedUsersText(String qui) {
      return "<html><div style='text-align:center;'><font size='5' color='#A3D8D0FF'>Connected Users:</font><br>"
              + qui + "</div></html>";
   }

   private static String getLblText(boolean isSent, String wrappedMessage) {
      return "<html><body style='width: 250px; overflow-wrap: break-word; padding: 10px; margin: 5px; border-radius: 10px; background-color: " + (isSent ? "#007bff" : "#f8f9fa") + "; color: " + (isSent ? "#fff" : "#000") + ";'>" + wrappedMessage + "</body></html>";
   }

   /*
      * Wrap the message to fit the chat panel
      *
      * @param message The message to wrap
      *
      * @return The wrapped message
    */
   private static String wrapMessage(String message) {
      StringBuilder wrappedMessage = new StringBuilder();
      String[] words = message.split("\\s");
      System.out.println("words: " + Arrays.toString(words));
      int wrapThreshold = 55; // You can adjust this threshold as needed
      for (String word : words) {
         if (word.length() > wrapThreshold) {
            // Wrap the word
            String ws = word.replaceAll("(.{" + wrapThreshold + "})", "$1 ");
            wrappedMessage.append("\u200B").append(ws).append("\u200B");
         } else {
            wrappedMessage.append(word);
         }
         wrappedMessage.append(" ");
      }
      return wrappedMessage.toString();
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      // Check if the send button or input text field is clicked
      if (e.getSource() == sendButton || e.getSource() == inputTextField) {
         String input = inputTextField.getText();
         if (!input.isBlank()) { // Check if the input text field is not empty
            try {
               forum.dire(id, input); // Send the message to the server
               addChatMessage(input, true); // Add sent message on the right side
               inputTextField.setText(""); // Clear the input text field
               System.out.println("Message sent to server --" + input); // Print the message to the console
            } catch (RemoteException ex) {
               alertError("Error sending message to server", ex.getMessage());
            }
         }
      }
   }

   public void alertError(String title, String message) {
      JOptionPane.showMessageDialog(this, "Exception msg: " + message, title, JOptionPane.ERROR_MESSAGE);
      System.err.println(title + " : " + message);
   }

   // Add a chat message to the chat panel
   // isSent is true if the message is sent by the current user
   public void addChatMessage(String message, boolean isSent) {
      JPanel messagePanel = new JPanel();
      messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.LINE_AXIS));

      // Wrap long words in the message
      String wrappedMessage = wrapMessage(message);

      // Create the message label
      JLabel label = new JLabel(getLblText(isSent, wrappedMessage));

      // If the message is received, add space between friend and self messages
      if (!isSent) {
         messagePanel.add(Box.createRigidArea(new Dimension(30, 0)));
      } else {
         messagePanel.add(Box.createHorizontalGlue());
      }

      messagePanel.add(label);
      messagePanel.setAlignmentX(isSent ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

      chatPanel.add(messagePanel);
      chatPanel.revalidate();
      chatPanel.repaint();
      // Scroll to the bottom of the chat panel
      SwingUtilities.invokeLater(() -> {
         JScrollBar scrollbar = chatScrollPane.getVerticalScrollBar();
         scrollbar.setValue(scrollbar.getMaximum());
      });
   }

   // Method to update the connected users label
   private void updateConnectedUsersLabel() {
      try {
         String connectedUsers = forum.qui(); // Get the connected users from the forum
         connectedUsersLabel.setText(getConnectedUsersText(connectedUsers)); // Update the label with the latest connected users
      } catch (RemoteException e) {
         alertError("Error updating connected users", e.getMessage());
      }
   }
}
