package paxos.test.model.support;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	public static String date() {
		return sdf.format(new Date());
	}
}
