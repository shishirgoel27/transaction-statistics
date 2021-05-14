package com.n26.transaction.service;

import com.n26.transaction.controller.StatisticResponse;
import com.n26.transaction.model.TransactionRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TransactionServiceTest {

    TransactionService service ;

    @Before
    public void setup() {
        service = new TransactionService();
    }

    @After
    public void tearDown() {
        service = null;
    }

    @Test
    public void testAddTransaction_multipleValidTrxnInAMinute_success() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Instant now = Instant.now();
        int count = 1000;
        double amount = 1000.00;
        Instant requestTimestamp = now.minusMillis(100);
        TransactionRequest tr = new TransactionRequest();
        List<Future<Boolean>> results = new ArrayList<>();
        for(int i=0;i<count;i++){
            buildTransactionRequest(amount, requestTimestamp, tr);
            Future<Boolean> result = executorService.submit(()->service.addTransaction(tr, now.toEpochMilli()));
            amount += i;
            requestTimestamp = requestTimestamp.minusMillis(10);
            results.add(result);
        }
        int success = 0;
        executorService.shutdown();
        while(!results.isEmpty()) {
            Iterator<Future<Boolean>> iter = results.iterator();
            while (iter.hasNext()) {
                final Future<Boolean> next = iter.next();
                if (next.isDone()) {
                    if (next.get()) {
                        success++;
                    }
                    iter.remove();
                }
            }
        }
        Assert.assertEquals(count, success);
    }

    @Test
    public void testGetStatistics_success() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Instant now = Instant.now();
        long count = 2L;
        double amount = 1000.00;
        Instant requestTimestamp = now.minusSeconds(5);
        for(int i=0;i<count;i++){
            TransactionRequest tr = new TransactionRequest();
            buildTransactionRequest(amount, requestTimestamp, tr);
            service.addTransaction(tr, now.toEpochMilli());
            amount += 10;
            requestTimestamp = requestTimestamp.minusSeconds(5);
        }
        StatisticResponse response = service.getStatistics(now.plusSeconds(20).toEpochMilli());
        Assert.assertTrue(response.getCount() == count );
        Assert.assertTrue(response.getSum().equals("2010.00") );
        Assert.assertTrue(response.getMax().equals("1010.00"));
        Assert.assertTrue(response.getMin().equals("1000.00"));

    }

    @Test
    public void testGetStatistics_expiredTrxnDroppedFromStatistics_success() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Instant now = Instant.now();
        int count = 10;
        double amount = 1000.00;
        Instant requestTimestamp = now.minusSeconds(5);
        List<Future<Boolean>> results = new ArrayList<>();
        for(int i=0;i<count;i++){
            TransactionRequest tr = new TransactionRequest();
            buildTransactionRequest(amount, requestTimestamp, tr);
            Future<Boolean> result = executorService.submit(()->service.addTransaction(tr, now.toEpochMilli()));
            amount += i;
            requestTimestamp = requestTimestamp.minusSeconds(5);
            results.add(result);
        }
        executorService.shutdown();
        final Instant statTime = now.plusSeconds(20);
        StatisticResponse response = service.getStatistics(statTime.toEpochMilli());
        Assert.assertTrue(response.getCount() < count );
    }

    @Test
    public void testDeleteTransactions_success() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Instant now = Instant.now();
        int count = 5;
        double amount = 1000.00;
        Instant requestTimestamp = now.minusSeconds(5);
        List<Future<Boolean>> results = new ArrayList<>();
        for(int i=0;i<count;i++){
            TransactionRequest tr = new TransactionRequest();
            buildTransactionRequest(amount, requestTimestamp, tr);
            Future<Boolean> result = executorService.submit(()->service.addTransaction(tr, now.toEpochMilli()));
            amount += i;
            requestTimestamp = requestTimestamp.minusSeconds(5);
            results.add(result);
        }
        executorService.shutdown();
        Assert.assertTrue(service.deleteTransactions());
    }

    private void buildTransactionRequest(double amount, Instant requestTimestamp, TransactionRequest tr) {
        tr.setAmount(""+amount);
        tr.setTimestamp(requestTimestamp.toString());
    }
}
