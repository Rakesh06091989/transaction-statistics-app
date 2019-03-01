package com.n26.service;

import com.n26.annotation.Recordable;
import com.n26.api.TransactionStatisticsController;
import com.n26.constant.TransactionStatisticsConstants;
import com.n26.dao.TransactionStatisticsDao;
import com.n26.exception.TransactionException;
import com.n26.model.TransactionEntity;
import com.n26.model.TransactionStatisticsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TransactionStatisticsService {

    private Logger logger = LoggerFactory.getLogger(TransactionStatisticsController.class);

    @Autowired
    private TransactionStatisticsDao transactionStatisticsDao;

    @Autowired TransactionStatisticsService transactionStatisticsService;


    /**
     * This method will create transaction if it is eligible
     * @param transactionEntity
     * @throws TransactionException
     */

    @Recordable(fileName = "createTransaction")
    public void createTransaction(TransactionEntity transactionEntity) throws TransactionException {
        if(isTransactionEligible(transactionEntity)){
            transactionStatisticsDao.createTransaction(transactionEntity);
        }
    }

    /**
     * This method will check eligibility of the transaction
     * @param transactionEntity
     * @return
     * @throws TransactionException
     */

    protected boolean isTransactionEligible(TransactionEntity transactionEntity) throws TransactionException {
        try {
            // get currentDate in XMLGregorian Format
            XMLGregorianCalendar currentDate = getCurrentDateInXmlGregorianFormat();
            //get the different between currentDate and Transaction Date
            long diffInSeconds = getDifferenceInSeconds(transactionEntity.getTimeStamp(), currentDate);
            if (diffInSeconds < 0) {
                logger.error(TransactionStatisticsConstants.STATUS_CODE_422);
                throw new TransactionException(TransactionStatisticsConstants.STATUS_CODE_422);
            } else if (diffInSeconds >= 600){
                logger.error(TransactionStatisticsConstants.STATUS_CODE_204);
                throw new TransactionException(TransactionStatisticsConstants.STATUS_CODE_204);
            }
        } catch (DatatypeConfigurationException e){
            throw new TransactionException(TransactionStatisticsConstants.STATUS_CODE_422,e.getCause());
        }
        return true;
    }

    /**
     * this method will delete all the transaction
     * @param transactionEntity
     */

    public void deleteAllTransactions(TransactionEntity transactionEntity) {
        transactionStatisticsDao.deleteAllTransactions(transactionEntity);
    }

    /**
     * This method will get the transaction statistics of all eligible transaction
     * @return
     * @throws TransactionException
     */
    //@Recordable(fileName = "transactions")
    public TransactionStatisticsEntity getTransactionStatistics() throws TransactionException {
        try {
            //create clone of all available transaction list from system
            List<TransactionEntity> allTransactions = new CopyOnWriteArrayList<>(transactionStatisticsDao.getAllTransactions());
            List<BigDecimal> amountListOfEligibleTransaction = new CopyOnWriteArrayList<>();

            //find the transactions which not older than transaction validity time and delete the older one
            Iterator itr = allTransactions.iterator();
            while (itr.hasNext()) {
                TransactionEntity transactionEntity = (TransactionEntity) itr.next();
                XMLGregorianCalendar currentDateInXmlGregorianFormat = getCurrentDateInXmlGregorianFormat();
                long diffInSeconds = getDifferenceInSeconds(transactionEntity.getTimeStamp(), currentDateInXmlGregorianFormat);
                if (diffInSeconds > 600) {
                    allTransactions.remove(transactionEntity);
                } else {
                    amountListOfEligibleTransaction.add(transactionEntity.getAmount());
                }
            }
            //remove the older transaction
            transactionStatisticsDao.updateTransactionList(allTransactions);
            transactionStatisticsService.aopTestMethod();
            //aopTestMethod();
            //get the transaction statistics from eligible transaction list
            if(!amountListOfEligibleTransaction.isEmpty()) {
                return getTransactionStatistics(amountListOfEligibleTransaction);
            }
            return new TransactionStatisticsEntity();
        } catch (DatatypeConfigurationException e) {
            logger.error(TransactionStatisticsConstants.STATUS_CODE_422);
            throw new TransactionException(TransactionStatisticsConstants.STATUS_CODE_422,e.getCause());
        }
    }

    @Recordable(fileName = "aopTest")
    protected void aopTestMethod() {
        logger.info("AOP Testing...!!!");
    }

    /**
     * This method will generate the transaction statistics based eligible transaction list
     * @param listOfEligibleTransaction
     * @return
     */

    protected TransactionStatisticsEntity getTransactionStatistics(List<BigDecimal> listOfEligibleTransaction) {
        TransactionStatisticsEntity transactionStatisticsEntity = new TransactionStatisticsEntity();
        BigDecimal sum = BigDecimal.ZERO;
        long count = listOfEligibleTransaction.size();
        transactionStatisticsEntity.setMax(Collections.max(listOfEligibleTransaction));
        transactionStatisticsEntity.setMin(Collections.min(listOfEligibleTransaction));
        transactionStatisticsEntity.setCount(count);
        for(BigDecimal item : listOfEligibleTransaction){
            sum = sum.add(item);
        }
        transactionStatisticsEntity.setSum(sum);
        transactionStatisticsEntity.setAvg(sum.divide(BigDecimal.valueOf(count)));
        return transactionStatisticsEntity;
    }

    /**
     * This method will return current date in XMLGregorian format
     * @return
     * @throws DatatypeConfigurationException
     */
    private XMLGregorianCalendar getCurrentDateInXmlGregorianFormat() throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    /**
     * This method will return different between current time and transaction time in seconds
     * @param transactionDate
     * @param currentDate
     * @return
     */
    private long getDifferenceInSeconds(XMLGregorianCalendar transactionDate, XMLGregorianCalendar currentDate) {
        long transactionDateInSeconds = transactionDate.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null
                , null).getTimeInMillis()/1000;
        long currentDateInSeconds = currentDate.toGregorianCalendar(TimeZone.getTimeZone("UTC"), null
                , null).getTimeInMillis()/1000;
        return (currentDateInSeconds - transactionDateInSeconds);
    }

}
