package site.selector.trade.arbitrage.livecoin.domain;

/**
 * Created by Stepan Litvinov on 10.11.17.
 */
public enum OrderStatus {
    ALL,
    OPEN,
    CLOSED,
    EXPIRED,
    CANCELLED,
    NOT_CANCELLED,
    PARTIALLY,
    EXECUTED,
    PARTIALLY_FILLED
}
