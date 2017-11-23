package site.selector.trade.arbitrage.livecoin.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Stepan Litvinov on 08.11.17.
 */
@Data
public class Trade {
    private Long time;
    private Long id;
    private BigDecimal price;
    private BigDecimal quantity;
    private TradeType type;
}
