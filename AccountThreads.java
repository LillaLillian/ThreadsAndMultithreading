import java.util.Random;

public class AccountThreads extends Thread {
	private Bank bank;
	private boolean debug;
	private int accountIndex;
	private int maxTransferAmount;
	private Random random;

	public AccountThreads(Bank bank, int index, int max, boolean debug) {
		this.bank = bank;
		this.accountIndex = index;
		this.maxTransferAmount = max;
		this.debug = debug;
	}

	public void run() {
		try {
			while (!interrupted()) {
				
					int toAccount = (int) (bank.numberOfAccounts() * Math.random());
					int amount = (int) (maxTransferAmount * Math.random());
					bank.transfer(accountIndex, toAccount, amount);
					sleep(15);
				}
			
		} catch (InterruptedException ignored) {
		}
	}
}
