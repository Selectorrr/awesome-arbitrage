package site.selector.trade.arbitrage.livecoin.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Stepan Litvinov on 10.11.17.
 */
@Data
public class CurrencyPair {
    private String symbol;
    private BigDecimal maxBid;
    private BigDecimal minAsk;
}
