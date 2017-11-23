package site.selector.trade.arbitrage.livecoin.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.selector.trade.arbitrage.livecoin.domain.MaxBidMinAsk;
import site.selector.trade.arbitrage.livecoin.domain.Order;
import site.selector.trade.arbitrage.livecoin.domain.OrderStatus;
import site.selector.trade.arbitrage.livecoin.domain.SellOrderResponse;
import site.selector.trade.arbitrage.livecoin.domain.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;
import static site.selector.trade.arbitrage.livecoin.domain.OrderStatus.*;

/**
 * Created by Stepan Litvinov on 08.11.17.
 */
@Service
public class LiveCoinOrchestration {

    @Value("${arbitrage.livecoin.currency}")
    private String currency;
    Logger log = LogManager.getLogger(LiveCoinOrchestration.class);

    @Value("${arbitrage.livecoin.lowerPrice}")
    private BigDecimal lowerPrice;

    @Autowired
    private LiveCoinApiService liveCoinApiService;
    @Value("${arbitrage.livecoin.sellQuantity}")
    private String quantity;
    @Value("${arbitrage.livecoin.waitingOrderSeconds}")
    private int waitingOrderSeconds;

    public void start() {
        log.info("start");
        while (true) {
            BigDecimal price = getPrice();
            log.info("calculated price: " + price);
            if (price.compareTo(lowerPrice) < 0) {
                log.error("we have reached the bottom");
                break;
            }
            log.info("create order quantity: " + quantity + " price: " + price);
            SellOrderResponse sellOrderResponse = liveCoinApiService.selLimit(String.valueOf(price), quantity);
            String orderId = sellOrderResponse.getOrderId();
            try {
                if (!sellOrderResponse.getSuccess()) {
                    log.error("can't create order");
                    break;
                }
                log.info("order was created: " + orderId);

                Instant orderCloseTime = Instant.now().plusSeconds(waitingOrderSeconds);

                log.info("waiting " + waitingOrderSeconds +" seconds for sale ...");
                while (!isOrderHasStatus(orderId, ImmutableSet.of(EXECUTED))) {
                    if (Instant.now().isAfter(orderCloseTime)) {
                        log.info("order was expired, cancel");
                        liveCoinApiService.cancelLimit(orderId);
                        break;
                    }
                    if (getMinAsk().compareTo(price)<0) {
                        log.info("there is a cheaper order, cancel");
                        liveCoinApiService.cancelLimit(orderId);
                        break;
                    }
                }
                log.info("order was realised");
            } catch (Exception e) {
                log.error("error was occurred, cancel order");
                liveCoinApiService.cancelLimit(orderId);
                throw new RuntimeException(e);
            }

        }
    }

    private boolean isOrderHasStatus(String orderId, Set<OrderStatus> statuses) {
        Order order = liveCoinApiService.getOrder(orderId);
        return statuses.contains(order.getStatus());
    }

    private BigDecimal getPrice() {
        BigDecimal lastTradePrice = getLastTradePrice();
        log.info("last trade price: "+ lastTradePrice);
        BigDecimal minAsk = getMinAsk();

        return minAsk.add(lastTradePrice)
                     .divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP)
                     .setScale(8, RoundingMode.HALF_UP);
    }

    private BigDecimal getMinAsk() {
        MaxBidMinAsk maxBidMinAsk = liveCoinApiService.getMaxBidMinAsk();

        return maxBidMinAsk.getCurrencyPairs().get(0).getMinAsk();
    }

    private BigDecimal getLastTradePrice() {
        List<Trade> lastTrades = liveCoinApiService.getLastTrades(currency, "false");

        Trade lastTrade = Iterables.getFirst(lastTrades, null);

        BigDecimal lastTradePrice = null;
        if (nonNull(lastTrade)) {
            lastTradePrice = lastTrade.getPrice();

        }
        return lastTradePrice;
    }

}
