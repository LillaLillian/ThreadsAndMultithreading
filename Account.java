import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
	private int balance;
	private int accountNumber;
	private Lock lock;
	private Condition lockCondition;

	public Account(int accountNumber, int balance) {
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.lock = new ReentrantLock();
		this.lockCondition = lock.newCondition();
	}

	void withdraw(int amount) {
		lock.lock();
		try {
			while (balance < amount) {

				lockCondition.await();
			}
			balance -= amount;
		} catch (InterruptedException e) {

		} finally {
			lock.unlock();
		}
	}

	void deposit(int amount) {
		lock.lock();
		try {
			balance += amount;
			lockCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	int getAccountNumber() {
		return accountNumber;
	}

	public int getBalance() {
		return balance;
	}

	public Lock getLock() {
		return lock;
	}
}
