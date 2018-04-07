package distributed.test.zookeeper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ZookeeperLockTest {
	private static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

	public static void main(String[] args) {
		for (int i = 0; i < 3; i++) {
			new Thread(() -> {
				ZookeeperLock lock = new ZookeeperLock("127.0.0.1:2181", "/ZookeeperLockTest");
				for (int j = 0; j < 2; j++) {
					try {
						String key = lock.lock();
						doSomething();
						lock.unlock(key);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	private static void doSomething() {
		log("running");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		log("run over");
	}

	private static void log(String content) {
		String name = Thread.currentThread().getName();
		System.out.println(sdf.format(new Date()) + " " + name + " " + content);
	}
}
