package distributed.test.zookeeper.curator;

import java.io.IOException;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import com.google.common.collect.Lists;

/**
 * Curator 的操作，需要和 zookeeper 服务的版本号一致
 * 具体的看 pom.xml 中 curator 依赖的zookeeper 版本
 * @author Administrator
 *
 */
public class CuratorElection {
	private static String zkLocation = "127.0.0.1:2181";
	private static String path = "/CuratorElection";

	public static void main(String[] args) {
		ensurePath();
		List<CuratorFramework> cflist = Lists.newArrayList();
		List<LeaderSelector> llist = Lists.newArrayList();
		try {
            System.out.println("Starting...");
			for (int i = 0; i < 3; i++) {
				final int index = i;
				CuratorFramework framework = CuratorFrameworkFactory.newClient(zkLocation, new ExponentialBackoffRetry(1000,
				        3));
				cflist.add(framework);
				framework.start();
				LeaderSelector leaderSelector = new LeaderSelector(framework, path, new LeaderSelectorListenerAdapter() {
					@Override
					public void takeLeadership(CuratorFramework client) throws Exception {
						System.out.println("start leader : " + index);
						Thread.sleep(1000);
						System.out.println("end leader : " + index);
						Thread.sleep(1000);
						if (index == 0) {
							throw new RuntimeException("try break it");
						}
					}
				});
				llist.add(leaderSelector);
				// 本轮lead结束以后，重新排队竞争lead
				leaderSelector.autoRequeue();
				leaderSelector.start();
			}
			System.in.read();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
            System.out.println("Shutting down...");
			cflist.forEach(obj -> CloseableUtils.closeQuietly(obj));
			llist.forEach(obj -> CloseableUtils.closeQuietly(obj));
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
		} finally {
			CloseableUtils.closeQuietly(cf);
		}
	}
}
