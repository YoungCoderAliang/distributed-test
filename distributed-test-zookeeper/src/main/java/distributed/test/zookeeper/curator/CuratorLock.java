package distributed.test.zookeeper.curator;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

public class CuratorLock {
	private static SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
	private static String zkLocation = "127.0.0.1:2181";
	private static String path = "/CuratorLock";

	public static void main(String[] args) {
		ensurePath();
		System.out.println("Starting...");
		for (int i = 0; i < 3; i++) {
			new Thread(() -> {
				CuratorFramework framework = CuratorFrameworkFactory.newClient(zkLocation, new ExponentialBackoffRetry(1000,
				        3));
				framework.start();
				InterProcessMutex lock = new InterProcessMutex(framework, path);
				for (int j = 0; j < 10; j++) {
					try {
						lock.acquire();
						doSomething();
						lock.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				CloseableUtils.closeQuietly(framework);
			}).start();
		}
	}

	private static void doSomething() {
		try {
			log("running");
			Thread.sleep(1000);
			log("run over");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}

	private static void log(String content) {
		String name = Thread.currentThread().getName();
		System.out.println(sdf.format(new Date()) + " " + name + " " + content);
	}

	private static void ensurePath() {
		CuratorFramework cf = CuratorFrameworkFactory.newClient(zkLocation, new ExponentialBackoffRetry(1000, 3));
		cf.start();
		try {
			if (cf.checkExists().forPath(path) == null) {
				cf.create().forPath(path, "".getBytes());
			}
		} catch (Exception e1) {
		} finally {
			CloseableUtils.closeQuietly(cf);
		}
	}
}
