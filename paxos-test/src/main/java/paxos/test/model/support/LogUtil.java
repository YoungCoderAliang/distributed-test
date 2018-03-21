package paxos.test.model.support;

public class LogUtil {
	public static void log(String content) {
		System.out.println(DateUtil.date() + "  " + Thread.currentThread().getName() + "  " + content);
	}
}
