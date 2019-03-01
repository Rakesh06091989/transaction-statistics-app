package com.n26.api;

import com.n26.constant.TransactionStatisticsConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.n26.exception.TransactionException;


@ControllerAdvice
public class TransctionExceptionHandler {

    private static Logger logger =  LoggerFactory.getLogger(TransactionException.class);

    @ExceptionHandler (TransactionException.class)
    public ResponseEntity<String> exceptionHandler(Exception ex){
        logger.error(ex.getMessage(),ex);
        if(ex.getMessage().equalsIgnoreCase(TransactionStatisticsConstants.STATUS_CODE_204)) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.NO_CONTENT);
        } else if(ex.getMessage().equals(TransactionStatisticsConstants.STATUS_CODE_422)){
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
