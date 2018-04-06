package distributed.test.zookeeper.java;

import java.util.Arrays;
import java.util.List;

public class FunctionalInterfaceTest {
	public static void main(String[] args) {
		// 函数式接口，匿名类实现，通过作为赋值理解
		TestInterface a = (x, y) -> {
			return x + y;
		};
		// 省略 return
		TestInterface b = (x, y) -> x + y;
		a.haha();
		System.out.println(a.deal(2, 5));

		// 函数式接口，匿名类实现，通过作为参数理解
		Thread t = new Thread(() -> System.out.println("running"));

		// 函数式接口 java.util.function.Consumer
		String[] ss = new String[] { "a", "b", "c", "d" };
		// ss.forEach
		List<String> list = Arrays.asList(ss);
		list.forEach(s -> System.out.println(s));
		list.forEach(System.out::println);
		// 函数式接口 java.util.function.Function
		list.stream().map(x -> x + "c");
		// 函数式接口 java.util.function.BinaryOperator
		String afterReduce = list.stream().map(x -> x + "c").reduce((p1, p2) -> p1 + "_" + p2).get();
		System.out.println(afterReduce);
	}

	// 函数式接口，需要满足的条件：有且只有一个抽象接口（也可以有其他接口，但是必须有default实现，或者是static方法）
	// @FunctionalInterface 注解可以帮助检查接口是否符合条件
	@FunctionalInterface
	public static interface TestInterface {
		public int deal(int a, int b);

		public default void haha() {
			System.out.println("haha");
		}
		// public int haha(int a);
	}
}
