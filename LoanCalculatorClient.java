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
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class LoanCalculatorClient extends Application {

	// IO streams
	DataOutputStream osToServer = null;
	DataInputStream isFromServer = null;

	public void start(Stage primaryStage) {
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(10, 10, 10, 10));
		gp.setHgap(10);
		gp.setVgap(10);
		
		TextField tfAnnualInterestRate = new TextField();
		TextField tfNumOfYears = new TextField();
		TextField tfLoanAmount = new TextField();
		Button btSubmit = new Button("Submit");
		
		Text tAnRate = new Text("Annual interest rate:");
		Text tNYears = new Text("Number of years:");
		Text tLAmount = new Text("Loan amount:");
		
		gp.add(tAnRate, 0, 0);
		gp.add(tNYears, 0, 1);
		gp.add(tLAmount, 0, 2);
		gp.add(tfAnnualInterestRate, 1, 0);
		gp.add(tfNumOfYears, 1, 1);
		gp.add(tfLoanAmount, 1, 2);
		gp.add(btSubmit, 3, 1);

		
		BorderPane mainPane = new BorderPane();
		TextArea ta = new TextArea();
		mainPane.setCenter(new ScrollPane(ta));
		mainPane.setTop(gp);

		Scene scene = new Scene(mainPane, 400, 300);
		primaryStage.setTitle("Loan Calculator, Client");
		primaryStage.setScene(scene); 
		primaryStage.show(); 

		btSubmit.setOnAction(e -> {
			try {
				double interest = Double.parseDouble(tfAnnualInterestRate.getText().trim());
				double years = Double.parseDouble(tfNumOfYears.getText().trim());
				double loanAmount = Double.parseDouble(tfLoanAmount.getText().trim());

				osToServer.writeDouble(interest);
				osToServer.writeDouble(years);
				osToServer.writeDouble(loanAmount);
				
				osToServer.flush();

				double monthly = Math.round(isFromServer.readDouble());
				double total = Math.round(isFromServer.readDouble());

				ta.appendText(String.format("Annual interest rate:.......%.2f \n", interest));
				ta.appendText(String.format("Number of years:............%.2f \n", years));
				ta.appendText(String.format("Loan amount:...................%.2f \n", loanAmount));
				ta.appendText(String.format("Montly payment:.............%.2f \n", monthly));
				ta.appendText(String.format("Total payment:.................%.2f \n", total));
				
			} catch (IOException ex) {
				System.err.println(ex);
			}
		});

		try {
			Socket socket = new Socket("localhost", 8000);
			isFromServer = new DataInputStream(socket.getInputStream());
			osToServer = new DataOutputStream(socket.getOutputStream());
			
		} catch (IOException ex) {
			ta.appendText(ex.toString() + '\n');
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
