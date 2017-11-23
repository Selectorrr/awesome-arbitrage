package site.selector.trade.arbitrage.livecoin.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Stepan Litvinov on 10.11.17.
 */
@Data
public class CancelOrderResponse {
    private Boolean success;
    private Boolean cancelled;
    private String message;
    private BigDecimal quantity;
    private BigDecimal tradeQuantity;
}
