import javatest.concurrency.models;

public class RVExample {

	private static int x;
	private static int y;
	private static Object lock = new Object();
	private static CtrlReentrantLock rl = new CtrlReentrantLock();
	
	public static void main(String[] args) {	
		TestApi.fj.obj = TestApi.fj.create_scheduler();
    	TestApi.fj.attach(TestApi.fj.obj);
		
		CtrlThread t1 = new CtrlThread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 2; i++) {
					synchronized (lock) {
						x = 0;
					}
					if (x > 0) {
						y++;
						x = 2;
					}
				}
			}

		});

		CtrlThread t2 = new CtrlThread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 2; i++) {
					if (x > 1) {
						if (y == 3) {
							System.out.println("error detected!!!");
						} else
							y = 2;
					}
				}
			}

		});
		t1.start();
		t2.start();

		for (int i = 0; i < 2; i++) {
			rl.lock();
				x = 1;
				y = 1;
			rl.unlock();
		}
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		TestApi.fj.detach(TestApi.fj.obj);
		
	}

	public void test() throws InterruptedException {
		try {
			x = 0;
			y = 0;
//			lock = new Object();
			RVExample.main(null);
		} catch (Exception e) {
			System.out.println("here");
			// fail();
		}
	}
}