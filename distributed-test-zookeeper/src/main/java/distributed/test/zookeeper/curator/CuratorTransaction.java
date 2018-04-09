package distributed.test.zookeeper.curator;

import java.util.Collection;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;

public class CuratorTransaction {
	private static String zkLocation = "127.0.0.1:2181";

	public static void main(String[] args) {
		CuratorFramework client = CuratorFrameworkFactory.newClient(zkLocation, new ExponentialBackoffRetry(1000, 3));
		client.start();
		try {
			client.delete().forPath("/path");
		} catch (Exception e1) {
		}
		try {
			CuratorOp createOp = client.transactionOp().create().withMode(CreateMode.EPHEMERAL)
			        .forPath("/path", "some data".getBytes());
			CuratorOp setDataOp = client.transactionOp().setData().forPath("/path", "other data".getBytes());
			CuratorOp deleteOp = client.transactionOp().delete().forPath("/yet/another/path");
			Collection<CuratorTransactionResult> results = client.transaction().forOperations(createOp, setDataOp, deleteOp);
			results.forEach(System.out::println);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
	        System.out.println(new String(client.getData().forPath("/path")));
        } catch (Exception e) {
	        e.printStackTrace();
        }
		CloseableUtils.closeQuietly(client);
	}
}
