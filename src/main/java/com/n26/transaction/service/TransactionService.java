package com.n26.transaction.service;

import com.n26.transaction.controller.StatisticResponse;
import com.n26.transaction.model.TransactionRequest;
import com.n26.transaction.model.TransactionStatistics;
import com.n26.transaction.store.TransactionStatisticsStore;
import com.n26.transaction.util.TimeWindow;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class TransactionService {

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private TransactionStatisticsStore<Long, TransactionStatistics> transactionStatisticsStore = new TransactionStatisticsStore<>();

    public boolean addTransaction(TransactionRequest transactionRequest, long now) {
        final long transactionRequestTimestampInMillis = transactionRequest.getTimestampInMillis();
        final BigDecimal amount = transactionRequest.getAmountInBigDecimal();
        if(now - transactionRequestTimestampInMillis < TimeWindow.ONE_MIN_IN_MILLIS.getValue()) {
            Long key = getStoreKey(transactionRequestTimestampInMillis);
            readWriteLock.writeLock().lock();
            try {
                TransactionStatistics transactionStatistics = transactionStatisticsStore.get(key);
                if(transactionStatistics == null) {
                    transactionStatistics = new TransactionStatistics(amount);
                    transactionStatisticsStore.put(key, transactionStatistics);
                }else {
                    // Aggregate transactions which occur concurrently.
                    // Save aggregate in transaction store at same location as key would be same due to same request timestamp.
                    transactionStatistics.aggregateStatistics(amount);
                }
                return true;
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }
        return false;
    }

    /**
     * It gets the statistics aggegated for max 60 transactions in worst case.
     * This is an O(60) ~= O(1) and does not depend on how many transactions are posted in one minute
     * @param now
     * @return
     */
    public StatisticResponse getStatistics(long now) {
        TransactionStatistics transactionStatistics = new TransactionStatistics();
        Long key = getStoreKey(now);
        long capacity = transactionStatisticsStore.getStoreCapacity();
        readWriteLock.readLock().lock();
        try {
            transactionStatisticsStore.entrySet().stream().filter(e-> ((key - e.getKey()) < capacity)).forEach(e->transactionStatistics.aggregateStatistics(e.getValue()));
        }finally {
            readWriteLock.readLock().unlock();
        }
        BigDecimal avg;
        Long trxnCount = transactionStatistics.getCount();
        if(trxnCount == 0L) {
            transactionStatistics.setMin(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP));
            avg = BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_HALF_UP);
        }else {
                avg = transactionStatistics.getSum().divide(BigDecimal.valueOf(trxnCount), BigDecimal.ROUND_HALF_UP);
        }
        return StatisticResponse.builder().withSum(transactionStatistics.getSum())
                                          .withCount(transactionStatistics.getCount())
                                          .withMax(transactionStatistics.getMax())
                                          .withMin(transactionStatistics.getMin())
                                          .withAvg(avg)
                                          .build();
    }

    /**
     * This method provides the request timestamp(in seconds) as the key for stored transaction
     * @param transactionRequestTimestampInMillis
     * @return
     */
    private Long getStoreKey(long transactionRequestTimestampInMillis) {
        return ((transactionRequestTimestampInMillis * transactionStatisticsStore.getStoreCapacity()) / TimeWindow.ONE_MIN_IN_MILLIS.getValue());
    }

    public boolean deleteTransactions() {
        boolean deleted = false;
        readWriteLock.writeLock().lock();
        try {
            transactionStatisticsStore.clear();
            deleted = transactionStatisticsStore.isEmpty();
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return deleted;
    }
}
