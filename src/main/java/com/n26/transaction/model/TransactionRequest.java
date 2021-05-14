package com.n26.transaction.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRequest {

    @NotNull
    private String amount;
    @NotNull
    private String timestamp;

    public BigDecimal getAmountInBigDecimal() {
        BigDecimal amountBigDecimal = new BigDecimal(amount) ;
        return amountBigDecimal;
    }

    public long getTimestampInMillis() {
        return Instant.parse(timestamp).toEpochMilli();
    }

}
