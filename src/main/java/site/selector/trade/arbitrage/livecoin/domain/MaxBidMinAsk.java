package site.selector.trade.arbitrage.livecoin.domain;

import lombok.Data;

import java.util.List;

/**
 * Created by Stepan Litvinov on 10.11.17.
 */
@Data
public class MaxBidMinAsk {

    private List<CurrencyPair> currencyPairs;
}
