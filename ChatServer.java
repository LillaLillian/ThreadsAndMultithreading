import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ChatServer extends Application {
	private ArrayList<ObjectOutputStream> outputToUsers = new ArrayList <ObjectOutputStream>();
	private TextArea taServer = new TextArea();
	private final int MAX_USERS = 10;
	private ServerSocket serverSocket;
	private boolean acceptingNewUsers = true;
	private int connectedUsers;
	private int port = 8000;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {
		initializePrimaryStage(primaryStage);
		
		new Thread(() -> {
			try {
				serverSocket = new ServerSocket(port);
				Platform.runLater(() -> taServer.appendText("Server started at " + new Date() + '\n'));
				
				while (true) {
				Socket socket = serverSocket.accept();
				
				if(connectedUsers < MAX_USERS) {
		          Platform.runLater( () -> { 
			            taServer.appendText("Starting thread for client " + 
			              " at " + new Date() + '\n'); 
			            });
		          new Thread(new AddUserToChat(socket)).start();
		          
		          
				} else {
					 refuseNewUsers();					
			            
				}
				}
			} catch (Exception ex) {
				System.out.println("stopp");
			}
		}).start();
		

	}
	
	public void stop() throws IOException{
		serverSocket.close();
	}
	
	private void initializePrimaryStage(Stage primaryStage) {
		taServer.setEditable(false);
		Scene scene = new Scene(new ScrollPane(taServer), 450, 200);
		primaryStage.setTitle("Chat, Server"); 
		primaryStage.setScene(scene); 
		primaryStage.show(); 
		
	}
	
	private void acceptUsers() {
		acceptingNewUsers = true;
		writeToLog("Accepting users.");
	}
	
	private void refuseNewUsers() {
		acceptingNewUsers = false;
		writeToLog("Maximum user capacity reached.");
	}
	
	private void writeToAll(String message) throws IOException{
		try {
		for (int i = 0; i < outputToUsers.size(); i++) {
			outputToUsers.get(i).writeUTF(message);
		}
		} catch (Exception ex) {
			System.out.println("Feil i writeToAll");
		}
	}
	
	private void writeToLog(String message) {
		try {
		Platform.runLater(() -> taServer.appendText(String.format("%tF %s", new Date(), message))); 
		} catch (Exception ex) {
			System.out.println("Problemer med å skrive til logg");
		}
	}
	
	private class AddUserToChat implements Runnable {
		private ObjectInputStream fromUser;
		private ObjectOutputStream toUser;
		private String username;
		private Socket socket;

		AddUserToChat(Socket socket) {
			this.socket = socket;
			connectedUsers++;
		}

		public void run() {
			try {
				establishUserConnection();
				readMessagesFromUser();
				
			} catch (Exception ex) {
				System.out.println("Feil i run() i Server");
			}
		}

		private void establishUserConnection() throws IOException {
			toUser = new ObjectOutputStream(socket.getOutputStream());
			fromUser = new ObjectInputStream(socket.getInputStream());
	
			username = fromUser.readUTF();
			outputToUsers.add(toUser);
			
			writeToLog(username + " joined the chat");
			writeToAll(username + " joined the chat");
		}

		private void removeUser() throws IOException {
			connectedUsers--;
			outputToUsers.remove(toUser);
			
			writeToLog(username + " left the chat");
			writeToAll(username + " left the chat");
			
			if(!acceptingNewUsers) {
				acceptUsers();
			}
		}

		private void readMessagesFromUser() throws IOException {
			while(true) {
				writeToAll(String.format("%s wrote: %s", username, fromUser.readUTF()));
			}
		}
	}
}
