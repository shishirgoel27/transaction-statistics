package com.n26.transaction.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionStatistics {

    private BigDecimal sum ;
    private BigDecimal max ;
    private BigDecimal min ;
    private Long count ;

    public TransactionStatistics() {
        sum = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
        max = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
        min = BigDecimal.valueOf(Double.MAX_VALUE).setScale(2,BigDecimal.ROUND_HALF_UP);
        count = 0L;
    }

    public TransactionStatistics(BigDecimal amount) {
        sum = amount;
        max = amount;
        min = amount;
        count = 1L;
        setScale();
    }

    private void setScale() {
        sum = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        max = max.setScale(2, BigDecimal.ROUND_HALF_UP);
        min = min.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void aggregateStatistics(BigDecimal amount) {
        addStats(amount, amount, amount, 1L);
    }

    public void aggregateStatistics(TransactionStatistics statistics) {
        addStats(statistics.getSum(), statistics.getMax(), statistics.getMin(), statistics.getCount());
    }

    private void addStats(BigDecimal sum, BigDecimal maximum, BigDecimal minimum, Long increment) {
        this.sum = this.sum.add(sum);
        this.max = this.max.max(maximum);
        this.min = this.min.min(minimum);
        this.count += increment;
    }

}
