package com.n26.transaction.validator;

import com.n26.transaction.exception.InputValidationException;
import com.n26.transaction.model.TransactionRequest;
import org.springframework.stereotype.Component;

@Component
public class JsonValidator implements Validator {
    @Override
    public void validate(TransactionRequest transactionRequest, long now) throws InputValidationException {
        try {
            transactionRequest.getTimestampInMillis();
            transactionRequest.getAmountInBigDecimal();
        } catch(Exception e) {
            throw new InputValidationException("422");
        }
    }
}
