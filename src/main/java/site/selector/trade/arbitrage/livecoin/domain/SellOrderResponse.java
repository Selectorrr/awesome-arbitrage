package site.selector.trade.arbitrage.livecoin.domain;

import lombok.Data;

/**
 * Created by Stepan Litvinov on 10.11.17.
 */
@Data
public class SellOrderResponse {
    private Boolean success;
    private Boolean added;
    private String orderId;
    private String exception;
}
