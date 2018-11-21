import java.util.ArrayList;
import javafx.scene.input.KeyCode;
import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class ChatClient extends Application {
	private TextField tfUserInput = new TextField();
	private TextField tfUsername = new TextField();
	private TextArea taChatLog = new TextArea();
	private ObjectOutputStream toServer = null;
	private ObjectInputStream fromServer = null;
	private String username;
	private Socket socket;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {
		initializePrimaryStage(primaryStage);
		connectToServer();	
//		new Thread(
//			new Runnable() {
//			public void run() {
//				try {
//					while(true) {
//					receiveDataFromServer();
//					}
//				} catch (Exception ex) {
//					System.out.println("Feil i mottakelsestråd");
//				}
//			};
//		}).start();
//		Thread.sleep(1000);
	}
	
	public void stop(){
	}

	private void initializeUsernameTextField() {
		tfUsername.setPromptText("Write your username and press enter to log in...");
		
		if(username == null) {
		tfUsername.setOnKeyPressed(e -> {
			
			 if (e.getCode() == KeyCode.ENTER) {
				username = tfUsername.getText().trim();
				tfUsername.setEditable(false);
				tfUsername.setDisable(true);
				initializeUserInputTextField();
				sendToServer(username);
			 }
			});
		}
	}

	private void initializeUserInputTextField() {
		if(username != null) {
		tfUserInput.setPromptText("Write your message and press enter to send...");
		tfUserInput.setEditable(true);
		tfUserInput.setDisable(false);
		
		tfUserInput.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
			
				String userInput = tfUserInput.getText().trim();
				sendToServer(userInput);
				tfUserInput.clear();
			}
		});
		} else {
			tfUserInput.setPromptText("Choose a username to use the chat...");
			tfUserInput.setEditable(false);
			tfUserInput.setDisable(true);
		}
	}

	private void initializePrimaryStage(Stage primaryStage) {
		Scene scene = new Scene(getBorderPane(), 500, 700);
		primaryStage.setTitle("Chat, Client"); 
		primaryStage.setScene(scene); 
		primaryStage.show(); 
	}

	private BorderPane getBorderPane() {
		initializeUsernameTextField();
		initializeUserInputTextField();
		
		BorderPane bp = new BorderPane();
		VBox vbox = new VBox();
		taChatLog.setPrefWidth(480);
		taChatLog.setPrefHeight(640);
		taChatLog.setEditable(false);
		
		vbox.getChildren().addAll(tfUsername, tfUserInput);
		bp.setCenter(new ScrollPane(taChatLog));
		bp.setBottom(vbox);
		
		return bp;
	}

	private void connectToServer() {
		new Thread(() -> {
			try {
			establishServerConnection();
			} catch(Exception ex) {
				System.out.println("Can't connect to the server");
			}
		}).start();
	}

	private void establishServerConnection() {
		try {
			Socket socket = new Socket("localhost", 8000);
			fromServer = new ObjectInputStream(socket.getInputStream());
			toServer = new ObjectOutputStream(socket.getOutputStream());

		} catch (IOException ex) {
			 taChatLog.appendText(ex.toString() + '\n');
		}
	}

	private void receiveDataFromServer() throws IOException {
		try {
		String output = fromServer.readUTF();
		writeToLog(output);
		
		} catch (Exception ex) {
			System.out.println("receiveDataFromServer feil");
		}
	}

	private void sendToServer(String message) {
		try {
			toServer.writeUTF(message);
			toServer.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeToLog(String message) {
		taChatLog.appendText(message + "\n");
	}


}
