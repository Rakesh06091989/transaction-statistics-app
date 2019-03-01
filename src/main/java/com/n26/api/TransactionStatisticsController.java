package com.n26.api;

import com.n26.exception.TransactionException;
import com.n26.model.TransactionEntity;
import com.n26.model.TransactionStatisticsEntity;
import com.n26.service.TransactionStatisticsService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
public class TransactionStatisticsController {

    private Logger logger = LoggerFactory.getLogger(TransactionStatisticsController.class);

    @Autowired
    private TransactionStatisticsService transactionStatisticsService;

    @PostMapping(path = "/create",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "create transaction for given transaction entity",
            notes = "it returns transaction creation message with status code")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionEntity transactionEntity) throws TransactionException {
        logger.info("In createTransaction method");
        transactionStatisticsService.createTransaction(transactionEntity);
        return new ResponseEntity<String>("The Trasanction has been Created",HttpStatus.CREATED);
    }

    /**
     *
     *
     * @return
     * @throws TransactionException
     */
    @GetMapping(path = "/getStatistics",produces = MediaType.APPLICATION_JSON_VALUE)
    public TransactionStatisticsEntity getTransactionStatistics() throws TransactionException {
        logger.info("In getTransactionStatistics method");
        return transactionStatisticsService.getTransactionStatistics();
    }

    @GetMapping(path = "/getStatisticsList",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TransactionStatisticsEntity> getTransactionStatisticsList() throws TransactionException {
        logger.info("In getTransactionStatistics method");
        List<TransactionStatisticsEntity> transactionStatisticsEntities = new ArrayList<>();
        transactionStatisticsEntities.add(transactionStatisticsService.getTransactionStatistics());
        return transactionStatisticsEntities;
    }

    @DeleteMapping(path = "/remove",produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteAllTransactions(@RequestBody TransactionEntity transactionEntity) {
        logger.info("In deleteAllTransactions method");
        transactionStatisticsService.deleteAllTransactions(transactionEntity);
        return new ResponseEntity<String>("All the trasanction has been deleted",HttpStatus.NO_CONTENT);
    }
}
