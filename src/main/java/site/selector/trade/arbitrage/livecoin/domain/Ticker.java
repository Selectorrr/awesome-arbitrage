package site.selector.trade.arbitrage.livecoin.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Stepan Litvinov on 08.11.17.
 */
@Data
public class Ticker {

    private BigDecimal last;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal volume;
    private BigDecimal vwap;
    private BigDecimal max_bid;
    private BigDecimal min_ask;
    private BigDecimal best_bid;
    private BigDecimal best_ask;
}
