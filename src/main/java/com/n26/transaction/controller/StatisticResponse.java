package com.n26.transaction.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StatisticResponse {

    @JsonProperty(value = "sum", index = 0)
    private String sum;

    @JsonProperty(value = "avg", index = 1)
    private String avg;

    @JsonProperty(value = "max", index = 2)
    private String max;

    @JsonProperty(value = "min", index = 3)
    private String min;

    @JsonProperty(value = "count", index = 4)
    private Long count;

    public static StatisticResponseBuilder builder() {
        return new StatisticResponseBuilder();
    }

    public static class StatisticResponseBuilder {
        StatisticResponse sr = new StatisticResponse();
        public StatisticResponseBuilder withSum(BigDecimal sum) {
            sr.sum = sum.toString();
            return this;
        }
        public StatisticResponseBuilder withAvg(BigDecimal avg) {
            sr.avg = avg.toString();
            return this;
        }
        public StatisticResponseBuilder withMax(BigDecimal max) {
            sr.max = max.toString();
            return this;
        }
        public StatisticResponseBuilder withMin(BigDecimal min) {
            sr.min = min.toString();
            return this;
        }
        public StatisticResponseBuilder withCount(Long count) {
            sr.count = count;
            return this;
        }
        public StatisticResponse build() {
            return sr;
        }
    }
}
