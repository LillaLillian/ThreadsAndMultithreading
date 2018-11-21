import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {
	private Account[] accounts;
	private boolean debug; // Definerer om toString metoden bør være mer utfyllende
	private int deviationCount;
	private int initialBalance = 10000;
	private Lock lock = new ReentrantLock(); // Låser banken når "transfer" sjekker om toString eller test skal kalles
	private static final int TEST_FREQUENCY = 10000; // Frekvens for å kalle test
	private int testCount; // Antall ganger test er kjørt
	private static final int TO_STRING_FREQUENCY = 1000; // Frekvens for å kalle toString
	private long transactionCount;

	public Bank(int accountAmount, int initialBalance, boolean debug) {
		accounts = new Account[accountAmount];

		for (int i = 0; i < accounts.length; i++) {
			accounts[i] = new Account(i, initialBalance);
		transactionCount = 0;
		}
	}

	public int getDeviationCount() {
		// henter devation count fra forventet total
		return deviationCount;
	}

	public double getErrorPercentage() {
		// henter prosent av feil, kalkulert basert på antall tester
		return 0;
	}

	public long getTransactionCount() {
		// henter antall transaksjoner
		return transactionCount;
	}

	public int numberOfAccounts() {
		// returnerer antall accounts i banken
		return accounts.length;
	}

	public void test() {
		int sum = 0;

		for (int i = 0; i < accounts.length; i++) {
			sum += accounts[i].getBalance();
		}
		testCount++;

		System.out.println("Transactions:" + getTransactionCount() + " Sum: " + sum + "\n");
	}

	public String toString() {
		// returnerer all relevant info om banken og skriver til string
		String sbank = "Banken har " + numberOfAccounts() + " kontoer\nDisse har saldo:\n";
		String saccount = "";
		for (int i = 0; i < numberOfAccounts(); i++) {
			saccount += "Konto nr. " + (i + 1) + ": " + accounts[i].getBalance() + "\n";
		}
		return sbank + saccount;
	}

	public void transfer(int from, int to, int amount) {
		accounts[from].withdraw(amount);
		accounts[to].deposit(amount);
		transactionCount++;
		if (transactionCount % TEST_FREQUENCY == 0) {	
			test();
		}
		if(transactionCount % TO_STRING_FREQUENCY == 0) {
			System.out.println(toString());
		}
	}

}
