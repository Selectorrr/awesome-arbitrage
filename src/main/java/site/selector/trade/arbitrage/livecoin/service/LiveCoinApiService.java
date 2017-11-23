package site.selector.trade.arbitrage.livecoin.service;

import com.google.common.collect.ImmutableMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import site.selector.trade.arbitrage.livecoin.domain.CancelOrderResponse;
import site.selector.trade.arbitrage.livecoin.domain.MaxBidMinAsk;
import site.selector.trade.arbitrage.livecoin.domain.Order;
import site.selector.trade.arbitrage.livecoin.domain.SellOrderResponse;
import site.selector.trade.arbitrage.livecoin.domain.Ticker;
import site.selector.trade.arbitrage.livecoin.domain.Trade;

import java.util.List;

/**
 * Created by Stepan Litvinov on 08.11.17.
 */
@Service
public class LiveCoinApiService {

    @Value("${arbitrage.livecoin.currency}")
    private String currency;
    Logger log = LogManager.getLogger(LiveCoinApiService.class);

    @Autowired
    private LiveCoinRequestService liveCoinRequestService;

    public Order getOrder(String orderId) {
        return liveCoinRequestService.get("/exchange/order", ImmutableMap.of("orderId", orderId), new ParameterizedTypeReference<Order>() {
        });
    }

    public List<Trade> getLastTrades(String currencyPair, String minutesOrHour) {
        return liveCoinRequestService.get("/exchange/last_trades",
                ImmutableMap.of(
                        "currencyPair", currencyPair,
                        "minutesOrHour", minutesOrHour
                ), new ParameterizedTypeReference<List<Trade>>() {
                });
    }

    public Ticker getTicker() {
        return liveCoinRequestService.get("/exchange/ticker", ImmutableMap.of("currencyPair", currency), new ParameterizedTypeReference<Ticker>() {
        });
    }


    public MaxBidMinAsk getMaxBidMinAsk() {

        return liveCoinRequestService.get("/exchange/maxbid_minask", ImmutableMap.of("currencyPair", currency),
                new ParameterizedTypeReference<MaxBidMinAsk>() {
                });
    }

    public SellOrderResponse selLimit(String price, String quantity) {
        return liveCoinRequestService.post("/exchange/selllimit", ImmutableMap.of("currencyPair", currency, "price", price, "quantity", quantity),
                new ParameterizedTypeReference<SellOrderResponse>() {
                });
    }

    public CancelOrderResponse cancelLimit(String orderId) {
        return liveCoinRequestService.post("/exchange/cancellimit", ImmutableMap.of("currencyPair", currency, "orderId", orderId),
                new ParameterizedTypeReference<CancelOrderResponse>() {
                });
    }
}
