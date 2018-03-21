package paxos.test.model.support;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.DefaultDefaultValueProcessor;
import paxos.test.Main;
import paxos.test.model.PaxosMsg;

public class NetworkMock {
	private static Map<String, LinkedBlockingQueue<PaxosMsg>> storeMsgs = new ConcurrentHashMap<String, LinkedBlockingQueue<PaxosMsg>>();

	private static JsonConfig jsonConfig = new JsonConfig();
	static {
		jsonConfig.registerDefaultValueProcessor(Integer.class, new DefaultDefaultValueProcessor() {
			public Object getDefaultValue(Class type) {
				return "-";
			}
		});
	}

	public static ExecutorService es = Executors.newFixedThreadPool(Main.pushThreads);
	private static Random random =  new Random(System.currentTimeMillis());

	public static void sendMsg(final PaxosMsg msg) {
		es.submit(new Runnable() {
			public void run() {
				if (random.nextInt(Main.losePer) == 1) {
					return;
				}
//				LogUtil.log(JSONObject.fromObject(msg, jsonConfig).toString());
				try {
	                Thread.sleep(Main.msgDelayFixed + random.nextInt(Main.msgDelayRandom));
                } catch (InterruptedException e) {
                }
				if (!storeMsgs.containsKey(msg.getTo())) {
					storeMsgs.put(msg.getTo(), new LinkedBlockingQueue<PaxosMsg>());
				}
				storeMsgs.get(msg.getTo()).add(msg);
			}
		});
	}

	public static PaxosMsg getMsg(String ip, Integer milliSeconds) {
		LinkedBlockingQueue<PaxosMsg> list = storeMsgs.get(ip);
		if (list == null) {
			try {
				Long sleep = 100L;
				if (milliSeconds != null) {
					sleep = Long.valueOf(milliSeconds.intValue());
				}
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
			}
			return null;
		}
		if (milliSeconds == null) {
			return list.poll();
		}
		try {
			return list.poll(milliSeconds, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return null;
		}
	}
}
