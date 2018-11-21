	import java.io.*; 
	import java.net.*; 
	import java.util.Date; 
	import javafx.application.Application; 
	import javafx.application.Platform; 
	import javafx.scene.Scene; 
	import javafx.scene.control.ScrollPane; 
	import javafx.scene.control.TextArea; 
	import javafx.stage.Stage; 
	
public class LoanCalculatorMultipleServer extends Application {
	  private TextArea ta = new TextArea(); 
	  private int clientNo = 0; 

	  public void start(Stage primaryStage) { 
	    // Create a scene and place it in the stage 
	    Scene scene = new Scene(new ScrollPane(ta), 450, 200); 
	    primaryStage.setTitle("Loan Calculator, Multiple Server");
	    primaryStage.setScene(scene); 
	    primaryStage.show();

	    new Thread( () -> { 
	      try { 
	        ServerSocket serverSocket = new ServerSocket(8000); 
	        ta.appendText("MultiThreadServer started at "  
	          + new Date() + '\n'); 
	     
	        while (true) { 
	          Socket socket = serverSocket.accept(); 
	          clientNo++; 
	           
	          Platform.runLater( () -> { 
	            ta.appendText("Starting thread for client " + clientNo + 
	              " at " + new Date() + '\n'); 

	            // Find the client's host name, and IP address 
	            InetAddress inetAddress = socket.getInetAddress(); 
	            ta.appendText("Client " + clientNo + "'s host name is " 
	              + inetAddress.getHostName() + "\n"); 
	            ta.appendText("Client " + clientNo + "'s IP Address is " 
	              + inetAddress.getHostAddress() + "\n"); 
	          }); 
	          new Thread(new HandleAClient(socket)).start(); 
	        } 
	      } 
	      catch(IOException ex) { 
	        System.err.println(ex); 
	      } 
	    }).start(); 
	  } 

	  class HandleAClient implements Runnable { 
	    private Socket socket; 

	    public HandleAClient(Socket socket) { 
	      this.socket = socket; 
	    } 

	    public void run() { 
	      try { 
	        DataInputStream inputFromClient = new DataInputStream(socket.getInputStream()); 
	        DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream()); 
 
	        while (true) { 
	        	double interest = inputFromClient.readDouble();
				double years = inputFromClient.readDouble();
				double loanAmount = inputFromClient.readDouble();
				
				Loan loan = new Loan(interest, (int)years, loanAmount); 

				double monthly = loan.getMonthlyPayment();
				double total = loan.getTotalPayment();
				
				outputToClient.writeDouble(monthly);
				outputToClient.writeDouble(total);

				Platform.runLater(() -> {
					ta.appendText(String.format("Annual interest rate:.......%.2f \n", interest));
					ta.appendText(String.format("Number of years:............%.2f \n", years));
					ta.appendText(String.format("Loan amount:...................%.2f \n", loanAmount));
					ta.appendText(String.format("Montly payment:.............%.2f \n", monthly));
					ta.appendText(String.format("Total payment:.................%.2f \n", total)); 
	        }); 
	      } 
	      } catch(IOException ex) { 
	        ex.printStackTrace(); 
	      } 
	    } 
	  } 
	   
	  public static void main(String[] args) { 
	    launch(args); 
	  } 
	} 

