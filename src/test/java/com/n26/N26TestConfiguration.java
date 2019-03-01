package com.n26;

import com.n26.service.TransactionStatisticsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class N26TestConfiguration {

    @Bean(name = "transactionStatisticsService1")
    public TransactionStatisticsService getTransactionStatisticsService() {
        return new TransactionStatisticsService();
    }
}
