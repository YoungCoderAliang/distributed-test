package distributed.test.zookeeper.java;

public class StringInternTest {
	// -XX:MaxMetaspaceSize=5m -XX:MetaspaceSize=2m
	public static void main(String[] args) {
	    String s = "base";
		for (int i = 0; i < 100000000; i++) {
	    	s = s + s;
	    	s.intern();
	    	if (i % 1000000 == 0) {
	    		System.out.println(i / 1000000);
	    	}
	    }
    }
}
