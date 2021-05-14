package com.n26.transaction.validator;

import com.n26.transaction.exception.InputValidationException;
import com.n26.transaction.model.TransactionRequest;

public interface Validator {
     void validate(TransactionRequest transactionRequest, long now) throws InputValidationException;
}
