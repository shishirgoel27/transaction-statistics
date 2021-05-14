package com.n26.transaction.util;

public enum TimeWindow {
    ONE_MIN_IN_MILLIS(60000L);
    Long value;
    TimeWindow(Long value) {
        this.value=value;
    }

    public Long getValue() {
        return value;
    }
}
