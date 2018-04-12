package distributed.test.zookeeper.java;

public class ThreadTest {
	private static Object obj = new Object();

	public static void main(String[] args) throws InterruptedException {
		// testWaitNotify();

		// testYield();

		testJoin();
	}

	private static void testJoin() throws InterruptedException {
		Thread t = new Thread(() -> {
			try {
	            Thread.sleep(1000);
            } catch (Exception e) {
            }
			System.out.println("t end");
		});
		t.start();
		t.join();
		System.out.println("end");
	}

	private static void testYield() {
		ThreadDemo demo = new ThreadDemo();
		Thread thread = new Thread(demo, "花花");
		Thread thread1 = new Thread(demo, "草草");
		thread.start();
		thread1.start();
	}

	private static class ThreadDemo implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < 5; i++) {
				if (i == 3) {
					System.out.println("当前的线程是     " + Thread.currentThread().getName());
					Thread.yield();
				}
				System.out.println("执行的是    " + Thread.currentThread().getName());
			}

		}
	}

	/*
	 * 1、执行之初： synchornized(obj) ：线程获得obj锁 wait 方法：线程进入 WAITING 状态，不再继续运行，并释放
	 * obj 锁 2、被notify唤醒： 线程进入 RUNNING状态，但是没有锁，开始竞争锁 3、notify的线程，同步代码块结束以后：
	 * notify线程释放掉了锁。此刻 wait 竞争到obj锁，开始执行后面的逻辑。 4、执行完同步代码块中的逻辑： 释放obj锁。
	 * 
	 * 5、notifyAll会唤醒obj上WAITING的所有线程，但是这些线程需要等notifyAll的同步代码块结束，然后竞争obj锁才执行后面的逻辑
	 */
	private static void testWaitNotify() {
		new Thread(() -> {
			// 如果 a 线程先 notify ，b 线程再 wait，则b线程不会被唤醒
			// try {
			// Thread.sleep(1000);
			// } catch (Exception e1) {
			// }
			    int times = 0;
			    while (true) {
				    synchronized (obj) {
					    try {
						    obj.wait();
					    } catch (Exception e) {
					    }
				    }
				    System.out.println(Thread.currentThread().getName() + ". wake up : " + times++);
			    }
		    }).start();
		new Thread(() -> {
			// try {
			// Thread.sleep(1000);
			// } catch (Exception e1) {
			// }
			    int times = 0;
			    while (true) {
				    synchronized (obj) {
					    try {
						    obj.wait();
					    } catch (Exception e) {
					    }
				    }
				    System.out.println(Thread.currentThread().getName() + ". wake up : " + times++);
			    }
		    }).start();

		try {
			Thread.sleep(1000);
		} catch (Exception e1) {
		}

		// notify 只会通知等待obj的一个线程
		// new Thread(() -> {
		// synchronized (obj) {
		// try {
		// obj.notify();
		// } catch (Exception e) {
		// }
		// }
		// }).start();

		// notifyAll 将会通知等待obj的所有线程
		new Thread(() -> {
			synchronized (obj) {
				try {
					obj.notifyAll();
				} catch (Exception e) {
				}
			}
		}).start();
	}
}
