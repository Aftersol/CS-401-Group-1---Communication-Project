import java.util.Scanner;

import java.util.List;
import java.util.ArrayList;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.lang.Runnable;
import java.lang.Thread;

public class Server {

    int numUsers = 0;
    int numITUsers = 0;
    int numChannels = 0;
    int numPrivateChannels = 0;

    List<User> users;
    List<ITUser> ITUsers;

    List<Channel> channels;
    List<PrivateChannels> privateMessages;

    void loadUsers()
    {
        File fp = new File("users.txt");
        try
        {
            Scanner scan = new Scanner(fp);

            while(scan.hasNextLine())
            {
                String line = scan.nextLine();
                String tokens[] = line.split(";");

                users.add(new User(Integer.parseInt(tokens[0]), tokens[1], tokens[2]));
                numUsers += 1;
            }
            scan.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void saveUsers()
    {
        String str = "";

		for (int user = 0; user < users.size(); user++)
		{
			str += Integer.toString(user)+ ";" + 
            users.get(user).getUsername() + ";" +
            users.get(user).getPassword() + "\n";
			
		}
        
		FileWriter fp;
		
		try
		{
			fp = new FileWriter("users.txt");
			fp.write(str);
		}
		catch (Exception e)
		{
			System.out.println("Cannot write " + "users.txt");
			return;
		}
		try {
		fp.close();
		}
		catch(Exception e)
		{
			return;
		}
		
    }

    void loadChannels()
    {
        File fp = new File("channels.txt");

        try {
            Files.createDirectory(Paths.get("channels"));
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        try
        {
            Scanner scan = new Scanner(fp);
            while (scan.hasNextLine())
            {
                String line = scan.nextLine();

                // https://stackoverflow.com/a/18893443
                String tokens[] = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                tokens[1] = tokens[1].substring(1, tokens.length - 1);

                Channel channel = new Channel(Integer.parseInt(tokens[0]), tokens[1]);
                if (Boolean.parseBoolean(tokens[2]))
                {
                    channel.hideChannel();
                }
                
                // Read message from file in channels directory
                try
                {
                    File channelFP = new File("channels/" + channel.getName() + ".txt");

                    Scanner scan0 = new Scanner(channelFP);
                    while (scan0.hasNextLine())
                    {
                        String secondLine = scan0.nextLine();
                        String secondTokens[] = secondLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                        Message msg = new Message(
                            Integer.parseInt(secondTokens[0]),
                            Integer.parseInt(secondTokens[1]),
                            secondTokens[3].substring(1, secondTokens.length - 1)
                        );

                        if (Boolean.parseBoolean(secondTokens[2]))
                        {
                            msg.hideMessage();
                        }

                        channel.addMessage(msg);
                    }

                    scan0.close();
                }
                catch (Exception e0)
                {
                    e0.printStackTrace();
                }

                channels.add(channel);
                numChannels += 1;
            }

            scan.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    void saveChannels()
    {
        
        String str = "";
        try {
            Files.createDirectory(Paths.get("channels"));
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        List<String> channelMetadata = new ArrayList<>(channels.size());

		for (int channel = 0; channel < channels.size(); channel++)
		{
            try
            {
                String channelStr = "";

                String channelLine = 
                    Integer.toString(channels.get(channel).getID()) + ";" +
                    "\"" + channels.get(channel).getName() + "\"" + ";" +
                    Boolean.toString(channels.get(channel).isHidden());

                channelMetadata.add(channelLine);


                FileWriter fp = new FileWriter("channels/" + channels.get(channel).getName() + ".txt");
                for (int message = 0; message < channels.get(channel).messages.size(); message++)
                {
                    str += 
                        Integer.toString(channels.get(channel).messages.get(message).getID()) + ";" +
                        Integer.toString(channels.get(channel).messages.get(message).getUserID()) + ";" +
                        Boolean.toString(channels.get(channel).messages.get(message).isHidden()) + ";" +
                        "\"" + channels.get(channel).messages.get(message).getMessageContent() + "\""+ "\n";

                }
                fp.write(channelStr);
                fp.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

		}
        
		FileWriter fp;
		
		try
		{
			fp = new FileWriter("channels.txt");

            for (int channel = 0; channel < channelMetadata.size(); channel++)
            {
                str += channelMetadata.get(channel) + "\n";
            }

			fp.write(str);
		}
		catch (Exception e)
		{
			System.out.println("Cannot write " + "channels.txt");
			return;
		}
		try {
		fp.close();
		}
		catch(Exception e)
		{
			return;
		}
    }

    void loadITUser()
    {
        File fp = new File("ITUsers.txt");
        try
        {
            Scanner scan = new Scanner(fp);

            while(scan.hasNextLine())
            {
                String line = scan.nextLine();
                String tokens[] = line.split(";");

                ITUsers.add(new ITUser(Integer.parseInt(tokens[0]), tokens[1], tokens[2]));
                numITUsers += 1;
            }
            scan.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }

    void saveITUsers()
    {
        String str = "";

		for (int user = 0; user < ITUsers.size(); user++)
		{
			str += Integer.toString(user)+ ";" + 
            ITUsers.get(user).getUsername() + ";" +
            ITUsers.get(user).getPassword() + "\n";
		}
        
		FileWriter fp;
		
		try
		{
			fp = new FileWriter("ITUsers.txt");
			fp.write(str);
		}
		catch (Exception e)
		{
			System.out.println("Cannot write " + "users.txt");
			return;
		}
		try {
		fp.close();
		}
		catch(Exception e)
		{
			return;
		}
    }

    void broadcastMessages()
    {

    }

		//server main code
        public static void main(String[] args) {
		
            ServerSocket server = null;
            try {
                server = new ServerSocket(1234);
                server.setReuseAddress(true);
                while (true) {
                        //accepts client connections
                    Socket client = server.accept();
    
                    ClientHandler handler = new ClientHandler(client);
    
                        //creates a new thread 
                    new Thread(handler).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (server != null) {
                    try {
                        server.close();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
    
        }
    
            //Client Handler. interacts with clients
        private static class ClientHandler implements Runnable {
            private final Socket clientSocket;
            
                //constructor
            ClientHandler(Socket newSocket) {
                this.clientSocket = newSocket;
            }
            
            public void run() {
                    // allows objects to be recieced and sent
                ObjectOutputStream out = null;
                ObjectInputStream in = null;
                try {
                        // connects the in and out with the client
                    out = new ObjectOutputStream(clientSocket.getOutputStream());
                    in = new ObjectInputStream(clientSocket.getInputStream());
                    
                    boolean waitLogin = true;
                    Message msg = (Message)in.readObject();
                    while (waitLogin == true) {
                        if ("login".equals(msg.getType())) {
                                //searches through users and checks if correct
                            String userpass[] = msg.getText().split(",");
                            boolean success = false;
                            for (int i = 0; i < users.size(); i++) { // checks if the password is in there
                                if (users.get(i).getUsername().equals(userpass.get(0)) && users.get(i).getPassword().equals(userpass.get(1))) {
                                    Message returnMessage = new Message("login","success", user(i).getID);
                                    out.writeObject(returnMessage);
                                    success = true;
                                }
                            }
                            if (success == false) {
                                Message returnMessage = new Message("login","fail", "");
                                out.writeObject(returnMessage);
                            }
                            
                        }
                        else if ("signup".equals(message.getType())) {
                                //pulls the name and password from the message text part 
                            String userpass[] = message.getText().split(",");
                            boolean success = true;
                            for (int i = 0; i < users.size(); i++) { // checks if the password is in there
                                if (users.get(i).getUsername().equals(userpass.get(0))) {
                                    success = false;
                                }
                            }
                            if (success == false) {
                                Message returnMessage = new Message("signup","success", numUsers);
                                out.writeObject(returnMessage);
                                createUser(userpass.get(0), userpass.get(1));
                            }
                            //then sends a reply saying success or fail
                        }
                        msg = (Message)in.readObject();
                    }
                    
                    boolean shouldQuit = false;
    
                    while (shouldQuit == false) {
                        msg = (Message)in.readObject();
                        switch(msg.getNetworkMessageType()) {
                            case None: {
                                break;
                            }
                            case Logout: { //user wants to logout
                                NetworkMessage logOutSucess;
                                shouldQuit = true;
                                outputMessages.add(logOutSucess);
                                break;   
                            }
                            case createchannel: { //user wants new channel
                                String newChannelName = "";
                                for (int i = 0; i < channels.size(); i++) {
                                    if (channels.get(i).getName().equals(newChannelName)) {
                                        break;        
                                    }
                                }
    
                                Channel newChannel = new Channel(numChannels, newChannelName);
                                channels.add(newChannel);
                                break;
                            }
                            case hidechannel: { //user wants to not see a channel
                                String channelName = "";
                                for (int i = 0; i < channels.size(); i++) {
                                    if (channels.get(i).getName().equals(channelName)) {
                                        channels.get(i).hideChannel();
                                        break;
                                    }
    
                                }
                                break;
                            }
                            case text: { //sent a message to channel
                                String message = "";
                                String channel = "";
    
                                for (int i = 0; i < channels.size(); i++) {
                                    if (channels.get(i).getName().equals(channel)) {
                                        Message newMessage = new Message(channels.get(i).messages.size(), 0, message);
                                        channels.get(i).addMessage(newMessage);
                                        break;
                                    }
    
                                }   
                                break;
                            }
                            case hidemessage: { //user wants to not see a message
                                break;
                            }
                            case 
                            default: {
                                break;
                            }
                        }
    
                        
                    }
    
                    clientSocket.close();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        if(clientSocket != null)
                            if (clientSocket.isClosed() == false)
                            clientSocket.close();
                    }
                    catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
    
            }
    
        }
    
}