package distributed.test.zookeeper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperElectionTest {
	private static String path = "/ZookeeperElectionTest";
	private static String[] nodePaths = new String[3];

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		// way1();
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 10000, e -> {
			// 连接建立的事件
			    System.out.println("default watch: " + e.toString());
		    });
		if (zk.exists(path, false) == null) {
			zk.create(path, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		way2();
	}

	private static void way2() {

		for (int i = 0; i < 3; i++) {
			final int index = i;
			new Thread(() -> {
				try {
					tryMinnode(index);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}

		try {
			System.in.read();
		} catch (IOException e) {
		}
	}

	private static void tryMinnode(int index) throws Exception {
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 10000, e -> {
			// 连接建立的事件
			    System.out.println("default watch: " + e.toString());
		    });
		nodePaths[index] = zk.create(path + "/seq", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
		        CreateMode.EPHEMERAL_SEQUENTIAL);
		waitForMin(zk, index);
	}

	/**
	 * 迭代监听子节点变动，
	 * 如果不是leader节点则不做任何处理，
	 * 如果是的话，先进行业务处理，然后删除之前建立的节点，新建一个新的序列临时节点
	 * @param zk
	 * @param index
	 * @throws Exception
	 */
	private static void waitForMin(ZooKeeper zk, int index) throws Exception {
		List<String> list = zk.getChildren(path, e -> {
			try {
				waitForMin(zk, index);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		Collections.sort(list);
		if (!list.isEmpty() && nodePaths[index].endsWith(list.get(0))) {
			doSomething();
			zk.delete(nodePaths[index], 0);
			nodePaths[index] = zk.create(path + "/seq", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
			        CreateMode.EPHEMERAL_SEQUENTIAL);
		} else {
			// do nothing
		}
	}

	private static void way1() {
		for (int i = 0; i < 3; i++) {
			new Thread(() -> {
				try {
					tryCreate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	private static void tryCreate() throws IOException {
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 10000, e -> {
			// 连接建立的事件
			    System.out.println("default watch: " + e.toString());
		    });
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
