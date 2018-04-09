package distributed.test.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;

/**
 * 两个特别的方法：
 * client.create().withProtection() ：保护模式创建节点
 * client.delete().guaranteed() ： 保证删除节点
 * 其它：
 * 回调 —— client.setData().inBackground(watcher)
 * 资源回收 —— CloseableUtils.closeQuietly(client)
 * 
 * @author Administrator
 *
 */
public class CuratorCrud {
	private static String zkLocation = "127.0.0.1:2181";
	private static String path = "/CuratorCrud";

	public static void main(String[] args) {
		ensurePath();
		CuratorFramework client = CuratorFrameworkFactory.newClient(zkLocation, new ExponentialBackoffRetry(1000, 3));
		client.start();
		try {
			// 尤其是临时排序节点，创建时加上保护模式，会在节点前增加GUID，如果创建过程中客户端未能正常得到创建结果
			// curator将在父节点下，使用普通的重试策略，寻找与GUID开头符合的节点，一旦找到，就认为创建成功
			// 这种处理，主要是防止排序锁的情况下，未能正确处理排序节点，从而导致的死锁
			client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
			        .forPath(path + "/seq", "hello".getBytes());
			client.getChildren().forPath(path).forEach(subnode -> {
				System.out.println("subnode: " + subnode);
				System.out.println("clearName: " + subnode.substring(subnode.indexOf("seq")));
				String totalPath = path + "/" + subnode;
				try {
					System.out.println("getData: " + new String(client.getData().forPath(totalPath)));
					client.setData().inBackground((ct, event) -> {
						System.out.println("setData: " + event.getType().equals(CuratorEventType.SET_DATA));
					}).forPath(totalPath, "world".getBytes());
					System.out.println("getData: " + new String(client.getData().forPath(totalPath)));
					// delete方法可能会失败，但是调用了guaranteed以后，curator会一直阻塞到确实删除了节点
					// 这简化了业务的代码，例如 锁失效时需要删除节点，使用guaranteed，可以不需要业务重试
					client.delete().guaranteed().forPath(totalPath);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			});
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			CloseableUtils.closeQuietly(client);
		}
	}

	private static void ensurePath() {
		CuratorFramework cf = CuratorFrameworkFactory.newClient(zkLocation, new ExponentialBackoffRetry(1000, 3));
		cf.start();
		try {
			if (cf.checkExists().forPath(path) == null) {
				cf.create().forPath(path, "".getBytes());
			}
		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		} finally {
			CloseableUtils.closeQuietly(cf);
		}
	}
}
