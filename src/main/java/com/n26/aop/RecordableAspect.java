package com.n26.aop;

import com.n26.annotation.Recordable;
import com.n26.api.TransactionStatisticsController;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.reflect.Method;

@Profile("test")
@Aspect
@Configuration
public class RecordableAspect {

    private Logger logger = LoggerFactory.getLogger(TransactionStatisticsController.class);

    @Around("@annotation(com.n26.annotation.Recordable)")
    public Object getRecording(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Recording for webservice is started");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Recordable annotation = method.getAnnotation(Recordable.class);
        logger.info("Recording file name should be " + annotation.fileName());
        return joinPoint.proceed();
        //logger.info("Recording for webservice is ended");
    }
}
