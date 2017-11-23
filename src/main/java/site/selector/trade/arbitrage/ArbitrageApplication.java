package site.selector.trade.arbitrage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import site.selector.trade.arbitrage.livecoin.service.LiveCoinOrchestration;

@SpringBootApplication
public class ArbitrageApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ArbitrageApplication.class, args);
		context.getBean(LiveCoinOrchestration.class).start();
	}
}
