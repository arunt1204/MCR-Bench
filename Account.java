import javatest.concurrency.models;

public class Account {
    double amount;
    String name;
    CtrlReentrantLock crl = new CtrlReentrantLock();

    public Account(String nm, double amnt) {
        amount = amnt;
        name = nm;
    }

    // functions
    //synchronized 
    void depsite(double money) {
        amount += money;
    }


     void withdraw(double money) {
    	 crl.lock();
        amount -= money;
        crl.unlock();
    }
     

void transfer(Account ac, double mn) {
	crl.lock();
        amount -= mn;
        	ac.amount += mn; // now yes. no acquire for the other lock!!
        	crl.unlock();      
    }

    public void print() {
        // TODO Auto-generated method stub
    }

}
