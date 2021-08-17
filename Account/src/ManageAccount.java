import javatest.concurrency.models;

public class ManageAccount extends CtrlThread {
	final Account account;
	// we may add more later to increase the parallelism level
	final static Account[] accounts = new Account[10];

	// the number of the accounts
	static int num = 2;
	// index to insert the next account
	static int accNum = 0;
	// the index
	int i;

	public ManageAccount(String name, double amount) {
		account = new Account(name, amount);
		i = accNum;
		accounts[i] = account;
		accNum = (accNum + 1) % num;// the next index in a cyclic order
	}

	public void run() {
		account.depsite(300);
		account.withdraw(100);
		/* TO RE-INTRODUCE BUG uncomment the lines below */
		Account acc = accounts[(i + 1) % num];// transfering to the next account
		account.transfer(acc, 100);
	}

	static public void printAllAccounts() {
		for (int j = 0; j < num; j++) {
			if (ManageAccount.accounts[j] != null) {
				ManageAccount.accounts[j].print();
				;// print it
			}
		}
	}

}