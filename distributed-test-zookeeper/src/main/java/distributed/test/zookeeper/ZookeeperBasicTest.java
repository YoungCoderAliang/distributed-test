package distributed.test.zookeeper;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperBasicTest {
	private static ZooKeeper zk;
	private static String basicPath = "/testbasic";

	public static void main(String[] args) throws Exception {
		// 这里定义的watcher是一个默认watcher
		// 当 zk 接口中含有 boolean 型 watch 参数时，如果传值为true，则使用该watcher
		zk = new ZooKeeper("127.0.0.1:2181", 10000, e -> {
			// 连接建立的事件
			    System.out.println("default watch: " + e.toString());
		    });

		// testDefaultWatcher();
		// zookeeper的事件监听都是一次性的，服务器只会通知客户端一次
		// testEventOnceHandle();

		// 通过在监听回调中建立监听，持续的跟踪zookeeper节点数值变化
		// testEventIterationHandle();

		 testVersion();

		// testCreateMode();

	}

	private static void testCreateMode() throws KeeperException, InterruptedException {
		Stat s = zk.exists("/testNodeType", true);
		if (s == null) {
			zk.create("/testNodeType", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		zk.create("/testNodeType/ephemera", "ephemera".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		try {
			// 临时节点下不能创建子节点
			zk.create("/testNodeType/ephemera/inner", "inner".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		} catch (Exception e) {
			System.out.println("临时节点下不能创建子节点:" + e.getMessage());
		}
		System.out.println("永久节点属性：" + JSONObject.fromObject(s));
		// zk.create("/testNodeType/persistentSeq", "persistentSeq".getBytes(),
		// ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
		List<String> children = zk.getChildren("/testNodeType", false);
		children.forEach(x -> {
			if (x.startsWith("persistentSeq")) {
				try {
					Stat persistentSeq = zk.exists("/testNodeType/" + x, false);
					System.out.println("永久有序节点属性：" + JSONObject.fromObject(persistentSeq));
				} catch (Exception e) {
				}
			}
		});
		System.out.println("全部子节点：" + JSONArray.fromObject(children).toString());
		Stat ephemera = zk.exists("/testNodeType/ephemera", false);
		System.out.println("临时节点属性：" + JSONObject.fromObject(ephemera));
		children = zk.getChildren("/testNodeType", false);
		System.out.println("全部子节点：" + JSONArray.fromObject(children).toString());
		zk.create("/testNodeType/ephemeraSeq", "ephemeraSeq".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
		        CreateMode.EPHEMERAL_SEQUENTIAL);
		children = zk.getChildren("/testNodeType", false);
		children.forEach(x -> {
			if (x.startsWith("ephemeraSeq")) {
				try {
					Stat ephemeraSeq = zk.exists("/testNodeType/" + x, false);
					System.out.println("临时有序节点属性：" + JSONObject.fromObject(ephemeraSeq));
				} catch (Exception e) {
				}
			}
		});
		System.out.println("全部子节点：" + JSONArray.fromObject(children).toString());
	}

	private static void testVersion() throws KeeperException, InterruptedException {
		Stat s = zk.exists("/testVersion", true);
		if (s == null) {
			zk.create("/testVersion", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		// 创建子节点，父节点 cversion 自增
		// 更新数据，version 自增
		// 更新访问控制策略，aversion 自增
		Stat before = zk.exists("/testVersion", false);
		zk.create("/testVersion/v1", "v1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		Stat afterAddChild = zk.exists("/testVersion", false);
		Stat afterChangeDate = zk.setData("/testVersion", "hahaha".getBytes(), before.getVersion());
		Stat afterAclChange = zk.setACL("/testVersion", ZooDefs.Ids.OPEN_ACL_UNSAFE, before.getAversion());
		System.out.println(JSONObject.fromObject(before).toString());
		System.out.println(JSONObject.fromObject(afterAddChild).toString());
		System.out.println(JSONObject.fromObject(afterChangeDate).toString());
		System.out.println(JSONObject.fromObject(afterAclChange).toString());
	}

	private static void testDefaultWatcher() throws InterruptedException, KeeperException {
		Thread.sleep(100);
		zk.create("/haha", "haha".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		Stat haha = zk.exists("/haha", true);
		zk.delete("/haha", haha.getVersion());
	}

	public static void testEventOnceHandle() {
		try {
			Stat s = zk.exists(basicPath, true);
			if (s == null) {
				zk.create(basicPath, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			Stat ret = new Stat();
			System.out.println(new String(zk.getData(basicPath, e -> {
				System.out.println("testbasic changed : " + e.toString());
			}, ret)));
			System.out.println("after getData: " + JSONObject.fromObject(ret).toString());
			Stat set1 = zk.setData(basicPath, "456".getBytes(), ret.getVersion());
			System.out.println("after set1: " + JSONObject.fromObject(set1).toString());
			Stat set2 = zk.setData(basicPath, "789".getBytes(), set1.getVersion());
			System.out.println("after set2: " + JSONObject.fromObject(set2).toString());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// 迭代监控节点的值，并打印
	public static void testEventIterationHandle() {
		try {
			Stat s = zk.exists(basicPath, false);
			if (s == null) {
				zk.create(basicPath, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			zk.setData(basicPath, "123".getBytes(), zk.exists(basicPath, false).getVersion());

			printDataAndWatch();

			Thread.sleep(100);
			Stat set = zk.setData(basicPath, "456".getBytes(), zk.exists(basicPath, false).getVersion());
			System.out.println("set: " + JSONObject.fromObject(set).toString());
			Thread.sleep(1000);
			// zookeeper的数据变更，是不受监听处理的影响的。比如监听要暂停3秒，但是主线程1秒后去读，也已经可以读取到最新的数据
			// 监听方法被调用，只能说明这个变更已经可以被读取到了
			// 如果监听处理的比较慢，很可能读取到更加靠后更新的数据
			System.out.println(new String(zk.getData(basicPath, null, set)));
			System.out.println("set: " + JSONObject.fromObject(set).toString());
			Thread.sleep(100);
			set = zk.setData(basicPath, "789".getBytes(), set.getVersion());
			System.out.println("set: " + JSONObject.fromObject(set).toString());
			Thread.sleep(100);
			set = zk.setData(basicPath, "1011".getBytes(), set.getVersion());
			System.out.println("set: " + JSONObject.fromObject(set).toString());
			System.in.read();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static void printDataAndWatch() throws KeeperException, InterruptedException {
		System.out.println(new String(zk.getData(basicPath, e -> {
			try {
				Thread.sleep(5000);
				printDataAndWatch();
			} catch (Exception e1) {
			}
		}, null)));
	}
}
