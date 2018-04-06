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
		// 参数1，corePoolSize：核心线程数。当运行的线程少于corePoolSize时，任何新增的任务，都会触发新线程的生成。当线程池运行线程达到corePoolSize，以后线程池至少保持这个数量的线程。
		// 参数5，workQueue：任务队列。当运行的线程数达到或超过corePoolSize时，新增的任务会被放到这个队列中。
		// 参数2，maximumPoolSize：最大线程数。当任务堆满了workQueue以后，新增的任务将会触发新线程的生成，新线程从workQueue取任务执行。如果workQueue已满，并且总的线程数达到maximumPoolSize，则再来新的任务将会被拒绝。
		// 参数6，handler：拒绝任务的回调处理。默认的handler是抛出拒绝执行异常。
		// 参数3，keepAliveTime：线程最大空闲时间。如果已有线程数超过corePoolSize，那么当其中某线程的空闲时间超过
		// keepAliveTime以后，线程就会被销毁。
		// 参数4，unit：keepAliveTime的时间单位。
		ExecutorService es = new ThreadPoolExecutor(1, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(10), (
		        runnable, execotor) -> {
			System.out.println("reject : " + runnable.toString());
		});
		int tasksNum = 6; // 验证任务先放到队列，而不是创建线程
		tasksNum = 11;
		tasksNum = 12; // 验证达到队列上限时，开始构建超过 corePoolSize 的线程
		tasksNum = 15;
		tasksNum = 16; // 验证达到队列上限，并且线程构建达到 maximumPoolSize ，新增任务会被拒绝
		for (int i = 0; i < tasksNum; i++) {
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
