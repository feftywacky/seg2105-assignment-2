// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
    sendToServer("#login " + loginID);
    
    
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	if (message.charAt(0)=='#')
    	{
    		handleCommand(message);
    	}
    	else
    	{	
    		sendToServer(message);
    	}
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommand(String command) throws IOException
  {
	  String[] command_parts = command.split(" ");
	  if (command_parts.length==1) 
	  {
		  if (command_parts[0].equals("#quit") && command_parts.length==1) {
			  quit();
		  }
		  else if (command_parts[0].equals("#logoff") && command_parts.length==1) {
			  this.closeConnection();
		  }
		  else if (command_parts[0].equals("#login") && command_parts.length==1) {
			  if (!this.isConnected())
			  {
				  this.openConnection();
			  }
		  }
		  else if (command_parts[0].equals("#gethost") && command_parts.length==1) {
			  clientUI.display(this.getHost());
		  }
		  else if (command_parts[0].equals("#getport") && command_parts.length==1) {
			  clientUI.display(String.valueOf(this.getPort()));
		  }
		  else if (command_parts[0].equals("#sethost") && command_parts.length==1) {
			  clientUI.display("use command #sethost <host_name_here>");
		  }
		  else if (command_parts[0].equals("#setport") && command_parts.length==1) {
			  clientUI.display("use command #setport <port_number_here>");
		  }
		  else {
			  clientUI.display("not a valid command");
		  }
	  }
	  else if (command_parts.length==2) {
		  if (command_parts[0].equals("#sethost")) {
			  if (!this.isConnected()) {
				  String host = command_parts[1];
				  this.setHost(host);
			  }
			  else
			  {
				  clientUI.display("cannot set host when logged in");
			  }
		  }
		  else if (command_parts[0].equals("#setport")) {
			  if (!this.isConnected()) {
				  String temp = command_parts[1];
				  int port = Integer.parseInt(temp);
				  this.setPort(port);
			  }
			  else {
				  clientUI.display("cannot set port when logged in");
			  }  
		  }
		  else {
			  clientUI.display("not a valid command");
		  }
	  }
	  else {
		  clientUI.display("not a valid command");
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  
  	/**
	 * Implements the hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
  		clientUI.display("connection has been closed");
	}

	/**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
	@Override
	protected void connectionException(Exception exception) {
		clientUI.display("server has shutdown");
		quit();
	}
	
}
//End of ChatClient class
