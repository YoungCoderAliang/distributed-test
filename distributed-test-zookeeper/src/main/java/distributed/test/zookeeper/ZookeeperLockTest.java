package distributed.test.zookeeper;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

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

	private static void log(String content) {
		String name = Thread.currentThread().getName();
		System.out.println(sdf.format(new Date()) + " " + name + " " + content);
	}

	private static void doSomething() {
		log("running");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		log("run over");
	}

	public static class ZookeeperLock {
		private String location;
		private String parentNode;
		private ZooKeeper zk;
		private Object lock = new Object();

		public ZookeeperLock(String zkLocation, String path) {
			location = zkLocation;
			parentNode = path;
			try {
				zk = new ZooKeeper(location, 10000, e -> {
					// 连接建立的事件
					    System.out.println("default watch: " + e.toString());
				    });
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			try {
				ensurePath();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private void ensurePath() throws KeeperException, InterruptedException {
			int indexFrom = 1;
			while (true) {
				int next = parentNode.indexOf("/", indexFrom);
				if (next == -1) {
					next = parentNode.length();
				}
				String sub = parentNode.substring(0, next);
				if (zk.exists(sub, false) == null) {
					try {
						zk.create(sub, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					} catch (KeeperException.NodeExistsException e) {
					}
				}
				indexFrom = next + 1;
				if (indexFrom > parentNode.length()) {
					break;
				}
			}
		}

		public String lock() throws KeeperException, InterruptedException {
			String nodePath = zk.create(parentNode + "/lock", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
			        CreateMode.EPHEMERAL_SEQUENTIAL);
//			log("create " + nodePath);
			BooleanHolder holder = new BooleanHolder();
			holder.shouldListen = true;
			if (!listen(nodePath, holder)) {
				while (true) {
					synchronized (lock) {
						// 1 防止异步已经通知成为最小节点，完成notify，但主线程还没有进入wait的情况
						lock.wait(50);
					}
					// 1 防止异步已经通知成为最小节点，完成notify，但主线程还没有进入wait的情况
					if (confirmLock(nodePath)) {
						break;
					}
				}
			}
			return nodePath;
		}

		public void unlock(String nodePath) throws InterruptedException, KeeperException {
			zk.delete(nodePath, 0);
//			log("delete " + nodePath);
		}

		private boolean confirmLock(String nodePath) throws KeeperException, InterruptedException {
			List<String> nodes = zk.getChildren(parentNode, false);
			Collections.sort(nodes);
			if (!nodes.isEmpty() && nodePath.endsWith(nodes.get(0))) {
				return true;
			}
			return false;
		}

		private boolean listen(String nodePath, BooleanHolder holder) throws KeeperException, InterruptedException {
			if (!holder.shouldListen) {
				return false;
			}
			List<String> nodes = zk.getChildren(parentNode, e -> {
				try {
					listen(nodePath, holder);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			Collections.sort(nodes);
			if (nodePath.endsWith(nodes.get(0))) {
				holder.shouldListen = false;
				synchronized (lock) {
					lock.notify();
				}
				return true;
			}
			return false;
		}

		private static class BooleanHolder {
			private boolean shouldListen;
		}
	}
}
