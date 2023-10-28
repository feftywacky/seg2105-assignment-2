package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  
  
  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    System.out.println("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * Implements the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  String client_name = client.toString();
	  String msg = "client " + client_name + " has connected";
	  System.out.println(msg);
	  this.sendToAllClients(msg);
  }

  /**
   * Implements the hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  String client_name = client.toString();
	  String msg = "client " + client_name + " has disconnected";
	  System.out.println(msg);
	  this.sendToAllClients(msg);
  }
  
  /**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI
 * @throws IOException 
   */
  public void handleMessageFromServerUI(String message) throws IOException
  {
    if (message.charAt(0) == '#')
      handleCommand(message);
    else{
      // send message to clients
      serverUI.display(message);
      this.sendToAllClients("SERVER MSG> " + message);
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
		  else if (command_parts[0].equals("#stop") && command_parts.length==1) {
			  stopListening();
		  }
		  else if (command_parts[0].equals("#close") && command_parts.length==1) {
			  close();
		  }
		  else if (command_parts[0].equals("#start") && command_parts.length==1) {
			  if (!isListening())
			  {
				  listen();
			  }
			  else
			  {
				  serverUI.display("server is already listening to clients");
			  }
		  }
		  else if (command_parts[0].equals("#getport") && command_parts.length==1) {
			  serverUI.display(String.valueOf(this.getPort()));
		  }
		  else if (command_parts[0].equals("#setport") && command_parts.length==1) {
			  serverUI.display("use command #setport <port_number_here>");
		  }
		  else {
			  serverUI.display("not a valid command");
		  }
	  }
	  else if (command_parts.length==2) {
		  if (command_parts[0].equals("#setport")) {
			  if (!isListening() && getNumberOfClients()==0) {
				  String temp = command_parts[1];
				  int port = Integer.parseInt(temp);
				  this.setPort(port);
			  }
			  else {
				  serverUI.display("cannot set port when logged in");
			  }  
		  }
		  else {
			  serverUI.display("not a valid command");
		  }
	  }
	  else {
		  serverUI.display("not a valid command");
	  }
  }
  
  /**
   * Terminates the server.
   */
  public void quit()
  {
    try
    {
    	close();	
    }
    catch(IOException e)
    {
    	System.exit(0);
    }
  }
  
  
}
//End of EchoServer class
