package com.n26.transaction.validator;

import com.n26.transaction.exception.InputValidationException;
import com.n26.transaction.model.TransactionRequest;
import com.n26.transaction.util.TimeWindow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TimestampValidator implements Validator {

    @Override
    public void validate(TransactionRequest transactionRequest, long now) throws InputValidationException {
        if(transactionRequest.getTimestampInMillis() > now) {
            throw new InputValidationException("422");
        }
        if(now - transactionRequest.getTimestampInMillis() > TimeWindow.ONE_MIN_IN_MILLIS.getValue()) {
            throw new InputValidationException("204");
        }
    }
}
