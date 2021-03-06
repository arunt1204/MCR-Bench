import javatest.concurrency.models;

public final class StringBuffer
//        implements java.io.Serializable, CharSequence
{

    private char value[];

    private int count;

    private boolean shared;
    
    private CtrlReentrantLock crl = new CtrlReentrantLock();

    //synchronized
    public synchronized int length() {
        return count;
    }

    private final void copy() {
        char newValue[] = new char[value.length];
        System.arraycopy(value, 0, newValue, 0, count);
        value = newValue;
        shared = false;
    }

   
    private void expandCapacity(int minimumCapacity) {
        int newCapacity = (value.length + 1) * 2;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        } else if (minimumCapacity > newCapacity) {
            newCapacity = minimumCapacity;
        }

        char newValue[] = new char[newCapacity];
        System.arraycopy(value, 0, newValue, 0, count);
        value = newValue;
        shared = false;
    }

  
    //synchronized
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
    	crl.lock();
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
        crl.unlock();
    }
    
    public StringBuffer(int length) {
        value = new char[length];
        shared = false;
    }
    
    public StringBuffer(String str) {
        this(str.length() + 16);
        append(str);
    }
    
    public StringBuffer append(String str) {
    	crl.lock();
        if (str == null) {
            str = String.valueOf(str);
        }

        int len = str.length();
        int newcount = count + len;
        if (newcount > value.length)
            expandCapacity(newcount);
        str.getChars(0, len, value, count);
        count = newcount;
        crl.unlock();
        return this;
    }


    public StringBuffer append(StringBuffer sb) {
    	crl.lock();
        int len = sb.length();
        int newcount = count + len;
        if (newcount > value.length)
            expandCapacity(newcount);
        
        sb.getChars(0, len, value, count);
        count = newcount;
        crl.unlock();
        return this;
    }

    public StringBuffer delete(int start, int end) {
    	crl.lock();
        int len = end - start;
        if (len > 0) {
            if (shared)
                copy();
            System.arraycopy(value, start+len, value, start, count-end);
            count -= len;
        }
        crl.unlock();
        return this;
    }
    
    
    public String toString() {
    	return new String(value);
        }
}