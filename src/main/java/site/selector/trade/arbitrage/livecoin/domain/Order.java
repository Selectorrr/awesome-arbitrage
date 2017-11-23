package site.selector.trade.arbitrage.livecoin.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Stepan Litvinov on 10.11.17.
 */
@Data
public class Order {
    private String id;
    private String client_id;
    private OrderStatus status;
    private String symbol;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal remaining_quantity;
    private BigDecimal blocked;
    private BigDecimal blocked_remain;
    private BigDecimal commission_rate;
//    private List<Trade> trades;
}
