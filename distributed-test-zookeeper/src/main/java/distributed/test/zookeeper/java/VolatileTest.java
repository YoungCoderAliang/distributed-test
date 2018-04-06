package distributed.test.zookeeper.java;

public class VolatileTest {
	private static int data = 0;
	private static volatile int vdata = 0;

	public static void main(String[] args) {
		// 结果显示两者没有区别
		// 原理上来讲，volatile修饰符，使得变量的修改对其他线程是立即可见的。
		// 没有该修饰符，修改结果对其他线程也是可见的，只是可能稍微晚了一丁点儿。
		// 原因是 共享变量主要存储在内存中，即堆中。但是每个线程执行的时候，会占用一个cpu，而cpu是有cpu缓存的，cpu缓存比内存快很多
		// 普通变量的处理，都需要通过cpu缓存这一关。例如读取，要先从内存读到cpu缓存，再从cpu缓存读到cpu处理。写入，要先写到cpu缓存，再从cpu缓存刷到内存
		// volatile修饰符，使得该变量的读写，都不经过cpu缓存这一级。其实就是消除了，修改线程，改了cpu缓存，却还没有写入内存的情况。其实个人理解，这种情况就当作是线程还没有执行修改就好了，没有去处理的意义。。
		// 理论上说，volatile修饰符，会禁止编译器重排序优化。编译器重排序，是在保证单线程结果一致的情况下，对语句编译时，调整了语句顺序
		// 多线程下可能出现不一致（我不清楚他们怎么验证出来的）。使用volatile禁用，保证多线程执行结果，不会受到重排序的影响
		
		testCommonShare();
		
//		testVolatileShare();
	}

	private static void testVolatileShare() {
		Thread t = new Thread(() -> {
			while (true) {
				System.out.println(vdata);
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					break;
				}
			}
		});
		t.start();
		for (int i = 0; i < 10; i++) {
			vdata++;
			try {
				Thread.sleep(9);
			} catch (InterruptedException e) {
			}
		}
		t.interrupt();
    }

	/**
	 * 即使是普通的共享变量，被某个线程修改后，也能被其他线程读取
	 */
	private static void testCommonShare() {
		Thread t = new Thread(() -> {
			while (true) {
				System.out.println(data);
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					break;
				}
			}
		});
		t.start();
		for (int i = 0; i < 10; i++) {
			data++;
			try {
				Thread.sleep(9);
			} catch (InterruptedException e) {
			}
		}
		t.interrupt();
	}
}
