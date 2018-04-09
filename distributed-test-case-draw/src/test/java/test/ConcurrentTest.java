package test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.json.JSONObject;

import org.distributed.test.service.SimpleResponse;
import org.distributed.test.util.UUID;
import org.jsoup.Jsoup;

import com.google.common.collect.Lists;

public class ConcurrentTest {
	private static int success = 0;
	private static int updatefail = 0;
	private static Object lockSucc = new Object();
	private static Object lockUpfail = new Object();
	private static int handleCount = 0;

	// 2000 QPS ，如果是500w用户，需要处理 2500s，约41分钟
	
	/**
已处理 5 万请求
take time : 25632 ms
success : 10
updatefail : 0
	 */
	
	public static void main(String[] args) {
		ExecutorService es = Executors.newFixedThreadPool(500);
		List<Future> fs = Lists.newArrayList();
		long start = System.currentTimeMillis();
		int requestCount = 50000;
		for (int i = 0; i < requestCount; i++) {
			fs.add(es.submit(() -> {
				try {
					String uuid = UUID.get().toString();
					String res = Jsoup.connect("http://localhost:8080/draw/dodraw?userId=" + uuid).ignoreContentType(true).get()
					        .body().html();
					SimpleResponse resp = (SimpleResponse) JSONObject.toBean(JSONObject.fromObject(res),
					        SimpleResponse.class);
					if (resp.success()) {
						System.out.println("获奖成功：" + uuid);
						synchronized (lockSucc) {
							success++;
						}
					} else if (resp.getMsg().equals("未中奖：更新失败")) {
						System.out.println("抽奖成功，更新失败： " + uuid);
						synchronized (lockUpfail) {
							updatefail++;
						}
					}
				} catch (Exception e) {
//					e.printStackTrace();
				}
			}));
		}
		fs.forEach(future -> {
			try {
				future.get();
				handleCount++;
				if (handleCount % 10000 == 0) {
					System.out.println("已处理 " + (handleCount / 10000) + " 万请求");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		long end = System.currentTimeMillis();
		System.out.println("take time : " + (end - start));
		System.out.println("success : " + success);
		System.out.println("updatefail : " + updatefail);
	}
}
