package distributed.test.zookeeper.java;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
	private static int index = 0;

	public static void main(String[] args) {
		testParam();
	}

	private static void testParam() {
		ExecutorService es = new ThreadPoolExecutor(1, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10), (
		        runnable, execotor) -> {
			System.out.println("reject : " + runnable.toString());
		});
		for (int i = 0; i < 20; i++) {
			es.submit(() -> {
				int id = index++;
				System.out.println("running : " + id);
				try {
					Thread.sleep(10000);
				} catch (Exception e) {
				}
				System.out.println("end : " + id);
			});
		}
	}
}
