import javatest.concurrency.models;


public class AccountTest extends Failable {

    public static void main(String[] args) throws Exception {
    	
    	TestApi.fj.obj = TestApi.fj.create_scheduler();
    	TestApi.fj.attach(TestApi.fj.obj);
        AccountTest accountTest = new AccountTest();
//        accountTest.test2RingTransferAndInvariantCheck();
//        accountTest.test3RingTransferAndInvariantCheck();
//        accountTest.test5RingTransferAndInvariantCheck();
//        accountTest.test2ThreadDepositAndCheckInvariant();
//        accountTest.test3ThreadDepositAndCheckInvariant();
//        accountTest.test5ThreadDepositAndCheckInvariant();
//        accountTest.test2ThreadWithdrawAndCheckInvariant();
//        accountTest.test3ThreadWithdrawAndCheckInvariant();
//        accountTest.test5ThreadWithdrawAndCheckInvariant();
//        accountTest.test2ThreadDepositAndWithdrawAndCheckInvariant();
//        accountTest.test3ThreadDepositAndWithdrawAndCheckInvariant();
        accountTest.test5ThreadDepositAndWithdrawAndCheckInvariant();
        TestApi.fj.detach(TestApi.fj.obj);
    }

//    @Test
    public void test5RingTransferAndInvariantCheck() throws Exception {
        performRingTransfersAndCheckInvariant(10);
    }

//    @Test
    public void test5ThreadWithdrawAndCheckInvariant() throws Exception {
        performWithdrawalsAndCheckInvariant(5);
    }

//    @Test
    public void test5ThreadDepositAndCheckInvariant() throws Exception {
        performDepositsAndCheckInvariant(5);
    }

 //   @Test
    public void test5ThreadDepositAndWithdrawAndCheckInvariant() throws Exception {
        performDepositsAndWithdrawalsAndCheckInvariant(5);
    }

//    @Test
    public void test3RingTransferAndInvariantCheck() throws Exception {
        performRingTransfersAndCheckInvariant(3);
    }

//    @Test
    public void test3ThreadWithdrawAndCheckInvariant() throws Exception {
        performWithdrawalsAndCheckInvariant(3);
    }

//    @Test
    public void test3ThreadDepositAndCheckInvariant() throws Exception {
        performDepositsAndCheckInvariant(3);
    }

//    @Test
    public void test3ThreadDepositAndWithdrawAndCheckInvariant() throws Exception {
        performDepositsAndWithdrawalsAndCheckInvariant(3);
    }

//    @Test
    public void test3TransferToTheSame() throws Exception {
        performTransferAndCheckInvariant(3);
    }

//    @Test
    public void test2RingTransferAndInvariantCheck() throws Exception {
        performRingTransfersAndCheckInvariant(2);
    }

//    @Test
    public void test2ThreadWithdrawAndCheckInvariant() throws Exception {
        performWithdrawalsAndCheckInvariant(2);
    }

//    @Test
    public void test2ThreadDepositAndCheckInvariant() throws Exception {
        performDepositsAndCheckInvariant(2);
    }

//    @Test
    public void test2ThreadDepositAndWithdrawAndCheckInvariant() throws Exception {
        performDepositsAndWithdrawalsAndCheckInvariant(2);
    }

    public void performRingTransfersAndCheckInvariant(int numOfThreads) throws Exception {
        ManageAccount.num = numOfThreads;
        ManageAccount[] bank = new ManageAccount[ManageAccount.num];
        String[] accountName = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
        for (int j = 0; j < ManageAccount.num; j++) {
            bank[j] = new ManageAccount(accountName[j], 100);
        }
        for (ManageAccount thread : bank) {
            thread.start();
        }
        for (ManageAccount thread : bank) {
            thread.join();
        }
        // flags which will indicate the kind of the bug
        boolean less = false, more = false;
        for (int i = 0; i < ManageAccount.num; i++) {
            Account account = ManageAccount.accounts[i];
            if (account.amount < 300) {
                less = true;
            } else if (account.amount > 300) {
                more = true;
            }
        }
        if (less && more) {
            fail("There is amount with more than 300 and there is amount with less than 300");
        }
        if (!less && more) {
            fail("There is amount with more than 300");
        }
        if (less && !more) {
            fail("There is amount with less than 300");
        }
    }

    private class DepositThread implements Runnable {

        private final Account account;
        private final double amount;

        public DepositThread(Account account, double amount) {
            this.account = account;
            this.amount = amount;
        }

        @Override
        public void run() {
            account.depsite(amount);
        }

    }

    public void performDepositsAndCheckInvariant(int numThreads) throws Exception {

        Account account = new Account("The Account", 100);
        CtrlThread[] depositThreads = new CtrlThread[numThreads];

        for (int i = 0; i < depositThreads.length; i++) {
            depositThreads[i] = new CtrlThread(new DepositThread(account, 100));
        }
        for (CtrlThread depositThread : depositThreads) {
            depositThread.start();
        }
        for (CtrlThread depositThread : depositThreads) {
            depositThread.join();
        }

        if (account.amount != 100 * (numThreads + 1)) {
            fail("Multi-threaded deposits caused incorrect account state!");
        }

    }

    private class WithdrawThread implements Runnable {

        private final Account account;
        private final double amount;

        public WithdrawThread(Account account, double amount) {
            this.account = account;
            this.amount = amount;
        }

        @Override
        public void run() {
            account.withdraw(amount);
        }

    }

    private class TransferThread implements Runnable {
        private final Account src;
        private final Account dst;

        public TransferThread(Account src, Account dst) {
            this.src = src;
            this.dst = dst;
        }

        @Override
        public void run() {
            src.transfer(dst, 100);
        }
    }

    private void performTransferAndCheckInvariant(int numThreads) throws Exception {
        Account account = new Account("account", 0);
        CtrlThread[] transferThread = new CtrlThread[numThreads];
        Account accounts[] = new Account[numThreads];

        for (int i = 0; i < transferThread.length; i++) {
            accounts[i] = new Account("src", 110);
            transferThread[i] = new CtrlThread(new TransferThread(accounts[i], account));
        }

        for (CtrlThread t : transferThread) {
            t.start();
        }
        for (CtrlThread t : transferThread) {
            t.join();
        }

        for (int i = 0; i < accounts.length; i++)
            if (accounts[i].amount != 10)
                fail("Multi-threaded transfer caused incorrect account state!");

        if (account.amount != numThreads * 100) {
        	System.out.println(account.amount);
            fail("Multi-threaded transfer caused incorrect account state!");
        }
    }

    public void performWithdrawalsAndCheckInvariant(int numThreads) throws Exception {

        Account account = new Account("The Account", 100 * (numThreads + 1));
        CtrlThread[] withdrawalThreads = new CtrlThread[numThreads];

        for (int i = 0; i < withdrawalThreads.length; i++) {
            withdrawalThreads[i] = new CtrlThread(new WithdrawThread(account, 100));
        }
        for (CtrlThread withdrawalThread : withdrawalThreads) {
            withdrawalThread.start();
        }
        for (CtrlThread withdrawalThread : withdrawalThreads) {
            withdrawalThread.join();
        }

        if (account.amount != 100) {
            fail("Multi-threaded withdrawals caused incorrect account state!");
        }

    }

    public void performDepositsAndWithdrawalsAndCheckInvariant(int numThreads) throws Exception {

        Account account = new Account("The Account", 100);
        numThreads /= 2;
        CtrlThread[] withdrawalThreads = new CtrlThread[numThreads];
        CtrlThread[] depositThreads = new CtrlThread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            depositThreads[i] = new CtrlThread(new DepositThread(account, 100));
            withdrawalThreads[i] = new CtrlThread(new WithdrawThread(account, 100));

        }
        for (int i = 0; i < numThreads; i++) {
            depositThreads[i].start();
            withdrawalThreads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            depositThreads[i].join();
            withdrawalThreads[i].join();
        }

        if (account.amount != 100) {
        	fail("result is not correct.");
//            fail("Multi-threaded deposits and withdrawals caused incorrect account state!");
        	System.out.println("Multi-threaded deposits and withdrawals caused incorrect account state!");
        }

    }

}