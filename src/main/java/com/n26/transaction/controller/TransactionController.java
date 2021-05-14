package com.n26.transaction.controller;

import com.n26.transaction.model.TransactionRequest;
import com.n26.transaction.service.TransactionService;
import com.n26.transaction.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Collection;

@RestController
@RequestMapping("/")
public class TransactionController {

    @Autowired
    private Collection<Validator> validators;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("transactions")
    public ResponseEntity<?> addTransaction(@Valid  @RequestBody TransactionRequest transactionRequest) {
        long now = Instant.now().toEpochMilli();
        validators.forEach(v->v.validate(transactionRequest, now));
        boolean added = transactionService.addTransaction(transactionRequest, now);
        if(added) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("statistics")
    public ResponseEntity<StatisticResponse> getStatistics() {
        long now = Instant.now().toEpochMilli();
        StatisticResponse response = transactionService.getStatistics(now);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("transactions")
    public ResponseEntity<StatisticResponse> deleteTransactions() {
        boolean deleted = transactionService.deleteTransactions();
        if(deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
