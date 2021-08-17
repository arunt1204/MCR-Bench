import javatest.concurrency.models;

public class Airline extends CtrlThread {

    public int numberOfSeatsSold = 0;
    public final int maximumCapacity;
    private boolean stopSales = false;
    private final int numOfTickets;
    
    public Airline(int numOfTickets) {
        /* Issuing 10% more tickets for sale */
        int numOfExtra = numOfTickets / 10;
        numOfExtra = numOfExtra == 0 ? 1 : numOfExtra;
        maximumCapacity = numOfTickets - numOfExtra;
        this.numOfTickets = numOfTickets;
    }

    public void makeBookings() throws Exception {        
        final CtrlThread[] threadArr = new CtrlThread[numOfTickets];

        for (int i = 0; i < numOfTickets; i++) {

            if (!stopSales) {
                threadArr[i] = new CtrlThread(this);
                /**
                 * THE BUG : StopSales is updated by the selling posts ( public
                 * void run() ), and by the time it is updated more tickets then
                 * are alowed to be are sold by other threads that are still
                 * running
                 */
                threadArr[i].start(); // "make the sale !!!"
            }
        }

        for (CtrlThread thread : threadArr) {
            if (thread != null) {
                thread.join();
            }
        }

    }

    /**
     * the selling post: making the sale & checking if limit was reached ( and
     * updating "StopSales" ),
     */
    public/* TO RE-INTRODUCE BUG remove the synchronized */void run() {
            if (numberOfSeatsSold >= maximumCapacity) // checking
            {
                stopSales = true; // updating
            } else {
                numberOfSeatsSold++;
            }
    }

}