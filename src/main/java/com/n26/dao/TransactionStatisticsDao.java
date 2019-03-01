package com.n26.dao;

import com.n26.api.TransactionStatisticsController;
import com.n26.model.TransactionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class TransactionStatisticsDao {

    private Logger logger = LoggerFactory.getLogger(TransactionStatisticsController.class);

    private static List<TransactionEntity> tranctionList = new CopyOnWriteArrayList<>();

    public void createTransaction(TransactionEntity transactionEntity) {
        logger.info("Creating transaction");
        tranctionList.add(transactionEntity);
    }

    public List<TransactionEntity> getAllTransactions(){
        return tranctionList;
    }
    public void updateTransactionList(List<TransactionEntity> updateTransactionList){
        logger.info("Updating transaction list");
        tranctionList.clear();
        tranctionList.addAll(updateTransactionList);
    }

    public void deleteAllTransactions(TransactionEntity transactionEntity) {
        logger.info("Deleting transaction");
        if(transactionEntity.getAmount() != null){
            tranctionList.remove(transactionEntity);
        } else {
            tranctionList.clear();
        }
    }
}
