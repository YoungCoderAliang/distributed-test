package org.distributed.test.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

public class UUID {
    private static Long vmId = null;
    private static Long lastTs = System.currentTimeMillis();
    private static Long seria = 0L;
    private static final int seriaMax = 4096;
    private static int[] bits = new int[] { 44, 12, 8 };

    public static void main(String args[]) {
		for(int i=0;i<289;i++){
			System.out.println(get());
		}
    }

    public static synchronized String get20() {
	return fillFixedDigit(get(), 20);
    }

    public static synchronized String get(int length) {
	return fillFixedDigit(get(), length);
    }

    /**
     * 获取uuid
     * 
     * @return
     */
    public static synchronized Long get() {
	if (vmId == null) {
	    initVmId();
	}
	Long ts = System.currentTimeMillis();
	if (ts < lastTs) {
	    throw new RuntimeException();
	}
	if (ts > lastTs) {
	    seria = 0L;
	    lastTs = ts;
	} else {
	    seria += 1;
	    if (seria >= seriaMax) {
		lastTs = ts = resetTs(ts);
		seria = 0L;
	    }
	}
	// System.out.println(lastTs + " " + seria + " " + vmId);
	return (lastTs << (bits[1] + bits[2])) | (seria << bits[2]) | vmId;
    }

    public static String fillFixedDigit(Long id, int charNum) {
	String idStr = id.toString();
	if (idStr.length() > charNum) {
	    throw new RuntimeException();
	}
	StringBuilder sb = new StringBuilder();
	sb.append(idStr);
	for (int i = 0; i < charNum - idStr.length(); i++) {
	    sb.insert(0, "1");
	}
	return sb.toString().trim();
    }

    public static String fill0(Long id, int charNum) {
	String idStr = id.toString();
	if (idStr.length() > charNum) {
	    throw new RuntimeException();
	}
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < charNum - idStr.length(); i++) {
	    sb.insert(0, "0");
	}
	sb.append(idStr);
	return sb.toString();
    }

    public static String fillSpace(Long id, int charNum) {
	String idStr = id.toString();
	if (idStr.length() > charNum) {
	    throw new RuntimeException();
	}
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < charNum - idStr.length(); i++) {
	    sb.insert(0, " ");
	}
	sb.append(idStr);
	return sb.toString();
    }

    private static Long resetTs(Long ts) {
	Long tmp = new Long(ts);
	while (tmp <= ts) {
	    tmp = System.currentTimeMillis();
	}
	return tmp;
    }

    private static void initVmId() {
	Long ipId = getIpId();
	if (ipId != null) {
	    vmId = ipId;
	} else {
	    vmId = new Long(new Random(System.currentTimeMillis()).nextInt(256));
	    // throw new RuntimeException();
	}
    }

    private static Long getIpId() {
	try {
	    Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
	    while (allNetInterfaces.hasMoreElements()) {
		NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
		Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
		while (addresses.hasMoreElements()) {
		    InetAddress ip = (InetAddress) addresses.nextElement();
		    if (ip != null && ip instanceof Inet4Address) {
			String ipStr = ip.getHostAddress();
			if (!"127.0.0.1".equals(ipStr)) {
			    // System.out.println(ipStr);
			    return Long.valueOf(ipStr.split("[.]")[3]);
			}
		    }
		}
	    }
	} catch (SocketException e) {
	}
	return null;
    }

	public static String[] chars = new String[] { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };


	public static String getShort() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			//每4个为一组，然后通过模26+10操作，结果作为索引取出字符
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x24]);
		}
		return shortBuffer.toString();
	}

}
