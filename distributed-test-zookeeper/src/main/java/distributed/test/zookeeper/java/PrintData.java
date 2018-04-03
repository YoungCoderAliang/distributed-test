package distributed.test.zookeeper.java;
//评测题目: 1.+实现两个线程，线程A负责打印1、3、5。。。99，线程B负责打印2，4，6。。。100，保证整体的输出顺序是1、2、3、4、5、6。。。100
public class PrintData implements Runnable {
	  private static int data = 0;
	  private static Object[] locks = new Object[] {new Object(), new Object()};
	  private Object lock = null;
	  public PrintData(Object lock) {
	    this.lock = lock;
	  }
	  public void run() {
	    for (;data < 99;) {
	      synchronized (lock) {
	        try {
	          lock.wait();
	        }catch(Exception e) {
	        }
	        System.out.println(Thread.currentThread().getName() + " " + ++data);
	      }
	      notifyAnother();
	    }
	  }
	  private static void notifyAnother() {
	    Object lc = locks[data%2];
	      synchronized(lc) {
	        lc.notify();
	      }
	  }
	  
	  public static void main(String[] args) {
	    Thread task1 = new Thread(new PrintData(PrintData.locks[0]));
	    Thread task2 = new Thread(new PrintData(PrintData.locks[1]));
	    task1.start();
	    task2.start();
	    try {
	      Thread.sleep(10);
	    } catch(Exception e) {
	    }
	    synchronized(PrintData.locks[0]) {
	      PrintData.locks[0].notify();
	    }
	  }
	}
