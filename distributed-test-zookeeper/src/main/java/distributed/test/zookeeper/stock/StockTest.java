package distributed.test.zookeeper.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.google.common.collect.Lists;

public class StockTest {
	private static class BuyInfo {
		private BigDecimal price;
		private BigDecimal amount;
		private BigDecimal money;
		private BigDecimal totalMoney;
		private BigDecimal totalAmount;
		private BigDecimal totalPrice;

		public BuyInfo(BigDecimal price, BigDecimal amount, BigDecimal money, BigDecimal totalAmount, BigDecimal totalMoney) {
			this.price = price;
			this.amount = amount;
			this.money = money;
			this.totalAmount = totalAmount;
			this.totalMoney = totalMoney;
			this.totalPrice = totalMoney.divide(totalAmount, 10, RoundingMode.CEILING);
		}
	}

	public static void main(String[] args) {
		BigDecimal priceDown = new BigDecimal("0.1");
		BigDecimal priceFocus = new BigDecimal("0.9");
		BigDecimal initPrice = new BigDecimal("100");
		BigDecimal initMoney = new BigDecimal("100000");
		BigDecimal initAmount = initMoney.divide(initPrice);
		BigDecimal totalMoney = new BigDecimal("100000");
		BigDecimal totalAmount = initMoney.divide(initPrice);
		List<BuyInfo> eachTime = Lists.newArrayList(new BuyInfo(initPrice, initAmount, initMoney, totalAmount, totalMoney));
		for (int i = 0; i < 5; i++) {
			BigDecimal currentPrice = initPrice.multiply(new BigDecimal(1).subtract(new BigDecimal(i + 1)
			        .multiply(priceDown)));
			if (currentPrice.multiply(totalAmount).divide(totalMoney, 10, RoundingMode.CEILING).compareTo(priceFocus) < 0) {
				/** newMoney / currentPrice = newAmount */
				/**
				 * currentPrice * (newAmount + totalAmount) / (newMoney +
				 * totalMoney) = 0.9
				 */
				/**
				 * currentPrice * (newMoney / currentPrice + totalAmount) = 0.9
				 * * (newMoney + totalMoney)
				 */
				/**
				 * newMoney + currentPrice * totalAmount = 0.9 * (newMoney +
				 * totalMoney)
				 */
				/**
				 * (1 - 0.9) * newMoney + currentPrice * totalAmount = 0.9 *
				 * totalMoney
				 */
				/**
				 * newMoney = (0.9 * totalMoney - currentPrice * totalAmount) /
				 * (1 - 0.9)
				 */

				BigDecimal newMoney = priceFocus.multiply(totalMoney).subtract(currentPrice.multiply(totalAmount));
				newMoney = newMoney.divide(new BigDecimal(1).subtract(priceFocus));
				BigDecimal newAmount = newMoney.divide(currentPrice, 10, RoundingMode.CEILING);
				totalMoney = totalMoney.add(newMoney);
				totalAmount = totalAmount.add(newMoney.divide(currentPrice, 10, RoundingMode.CEILING));
				eachTime.add(new BuyInfo(currentPrice, newAmount, newMoney, totalAmount, totalMoney));
			}
		}
		eachTime.forEach(info -> {
			log(info.price);
			log(info.amount);
			log(info.money);
			System.out.print(" |   ");
			log(info.totalPrice);
			log(info.totalAmount);
			log(info.totalMoney);
			System.out.println();
		});
	}

	private static void log(BigDecimal bd) {
		System.out.printf("%-10s  ", bd.setScale(2, RoundingMode.DOWN).toPlainString());
	}
}
