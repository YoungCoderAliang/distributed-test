package distributed.test.zookeeper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperElectionTest {
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		for (int i = 0; i < 3; i++) {
			new Thread(() -> {
				try {
					elect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	private static void elect() throws IOException {
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 10000, e -> {
			// 连接建立的事件
			    System.out.println("default watch: " + e.toString());
		    });
		String path = "/ZookeeperElectionTest";
		while (true) {
			try {
				zk.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				doSomething();
				zk.delete(path, 0);
				Thread.sleep(10);
			} catch (KeeperException e1) {
				if (e1 instanceof KeeperException.NodeExistsException) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e2) {
					}
				}
			} catch (InterruptedException e1) {
			}
		}
	}

	private static void doSomething() {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		String name = Thread.currentThread().getName();
		System.out.println(sdf.format(new Date()) + " " + name + " running");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		System.out.println(sdf.format(new Date()) + " " + name + " run over");
	}
}
