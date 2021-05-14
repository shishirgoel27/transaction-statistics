package com.n26.transaction.store;

import com.n26.transaction.model.TransactionStatistics;

import java.util.LinkedHashMap;
import java.util.Map;

public class TransactionStatisticsStore<Long, TransactionStatistics> extends LinkedHashMap<Long, TransactionStatistics> {

    /**
     *  Transaction store capacity to hold last 60 transactions
     */
    private final static int STORE_CAPACITY = 60;

    public int getStoreCapacity() {
        return STORE_CAPACITY;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Long, TransactionStatistics> eldest) {
        return size() > getStoreCapacity();
    }
}
