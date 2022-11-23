import java.io.*;
import java.net.*;
import java.util.*;

	//client class
public class Client {
	public static int userID;
	
	
	//Client driver
	public static void main(String[] args) {
			// establish a connection by providing host and port number
            // change to what ever ip you make the server
		try (Socket socket = new Socket("server ip address here", 1234)) 
        {
			// writing to server
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			// reading from server
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			
				//send login message
			String username = "";
			String password = "";

			boolean notLoggedin = true;
        	boolean recieved = false;

        	int input = 0;

        	while (notLoggedin == true) 
            {
                //ui asks what operation they want to do, login or sign up
                switch(input) 
                {  
                case 1: 
                {   //login
                    //needs code to ask for username and password calling the UI
                    Message loginMessage = new Message("login", "connecting", username + "," + password);
                    out.writeObject(loginMessage);
                    recieved = false;
                    while (recieved == false) {
                        Message message = (Message)in.readObject();
                        if ("login".equals(message.getType())) {
                            recieved = true;
                            if ("success".equals(message.getStatus())) {
                                notLoggedin = false;
                            }
                        }
                    }
                    break;
                }
                case 2: 
                {   //sign up
                    //needs code to ask for username and password calling the UI
                    Message SignupMessage = new Message("signup", "creating new user", username + "," + password);
                    out.writeObject(SignupMessage);
                    recieved = false;
                    while (recieved == false) 
                    {
                        Message message = (Message)in.readObject();
                        if ("login".equals(message.getType())) 
                        {
                            recieved = true;
                            if ("success".equals(message.getStatus())) 
                            {
                                notLoggedin = false;
                            }
                        }
                    }
                    break;
                }
                default:
                {
                    break;
                }
            }
        
            // recieve login confermation
            Message loginConfirm = (Message)in.readObject();
            userID = Integer.parseInt(loginConfirm.getText());
            //System.out.println(loginConfirm.toString());

            // line scanner to read client text
            Scanner sc = new Scanner(System.in);
            String line = null;

            while (!"logout".equalsIgnoreCase(line)) 
            {
                System.out.println("text to send or type 'logout' to logout: \n");
                    // reading from user
                line = sc.nextLine();
            
                if (!"logout".equalsIgnoreCase(line)) 
                {
                        // sending the user input to server
                    Message message = new Message("text", "sending text to convert", line);
                    out.writeObject(message);

                        // displaying server reply
                    Message reply = (Message)in.readObject();
                    System.out.println("New text from server: "+ reply.getText());
                }
            }
            // logout sequence
            Message logoutMessage = new Message("logout", "transmit", "telling server to end communication");
            out.writeObject(logoutMessage);

            Message logoutConfirm = (Message)in.readObject();
            System.out.println(logoutConfirm.toString());

             // closing the scanner object
            sc.close();
            }
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
	}

    void doLogin() {

    }

    void doLogout() {

    }

    void doSignup() {

    }

    void doSendMessage() {

    }

    void doCreateChannel() {

    }

    void doJoinChannel() {

    }

    void doDisplayChannels() {

    }

    void doChangeChannel() {

    }

    void doLeaveChannel() {

    }

    void doDisconnect() {
        
    }

}