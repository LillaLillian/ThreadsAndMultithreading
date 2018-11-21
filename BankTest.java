public class BankTest {
	private static final int ACCOUNT_AMOUNT = 10;
	private static boolean DEBUG;
	private static final int INITIAL_BALANCE = 10000;

	public static void main(String[] args) {
		Bank bank = new Bank(ACCOUNT_AMOUNT, INITIAL_BALANCE, DEBUG);

		for (int i = 0; i < ACCOUNT_AMOUNT; i++) {
			AccountThreads threads = new AccountThreads(bank, i, INITIAL_BALANCE, DEBUG);
			threads.setPriority(Thread.NORM_PRIORITY + i % 2);
			threads.start();
		}
	}
}





