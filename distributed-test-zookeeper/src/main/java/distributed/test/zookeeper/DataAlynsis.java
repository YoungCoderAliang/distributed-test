package distributed.test.zookeeper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataAlynsis {
	public static void main(String[] args) throws Exception {
		// String[] ss = new String[] {"BD 32412-12-DF NOBU",
		// "BD-32412 12 DF NOBU", "BD 32412/12/DF NOBU", "BD 32412_12/DF NOBU"};
		// Pattern p = Pattern.compile("[ -/\\_][0-9]{5}[ -/\\_]");
		// for (String s : ss) {
		// Matcher m = p.matcher(s);
		// if (m.find()) {
		// int start = m.start();
		// int end = m.end();
		// System.out.println(s.substring(start, end));
		// }
		// }

		String[] filePaths = new String[] {"1705", "1801", "1802", "1803", "1804", "1805"};
		for (String filePath : filePaths) {
			BufferedReader br = new BufferedReader(new FileReader(
					"/public/test/" + filePath));
			List<String> sources = new LinkedList<String>();
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				sources.add(tmp);
			}
			br.close();
			Pattern p = Pattern.compile("[ -/\\_][0-9]{5}[ -/\\_]");
			FileOutputStream fos = new FileOutputStream("/public/test/"
					+ filePath + "_out_2");
			sources.forEach(str -> {
				Matcher m = p.matcher(str);
				if (!m.find()) {
					try {
						fos.write(("\n").getBytes());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						fos.write((str.substring(m.start() + 1, m.start() + 6) + "\n").getBytes());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			fos.close();
		}
	}
}
