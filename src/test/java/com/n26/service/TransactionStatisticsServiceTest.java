package com.n26.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.N26TestConfiguration;
import com.n26.constant.TransactionStatisticsConstants;
import com.n26.dao.TransactionStatisticsDao;
import com.n26.exception.TransactionException;
import com.n26.model.TransactionEntity;
import com.n26.model.TransactionStatisticsEntity;
import com.n26.utils.FileUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import javax.servlet.annotation.WebInitParam;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Import(N26TestConfiguration.class)
@ContextConfiguration(name = "TransactionStatisticsService")
public class TransactionStatisticsServiceTest {


    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private TransactionStatisticsDao transactionStatisticsDao;

    @InjectMocks
    private TransactionStatisticsService transactionStatisticsService;

    @Resource(name = "transactionStatisticsService1")
    private TransactionStatisticsService transactionStatisticsService1;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp(){
        objectMapper = new ObjectMapper();
        transactionStatisticsService = Mockito.spy(TransactionStatisticsService.class);
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testCreateTransactionWithCurrentDateInput() throws TransactionException, DatatypeConfigurationException {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTimeStamp(getCurrentDateWithXmlGregorianFormat());
        transactionEntity.setAmount(BigDecimal.valueOf(50.45));
        transactionStatisticsService.createTransaction(transactionEntity);
    }

    @Test(expected = TransactionException.class)
    public void testCreateTransactionWhenTransactionDateIsOlderThenSixtySecond() throws IOException, TransactionException {
        String createTransactionInput = FileUtils.readStringFromFile("/createTransactionInput.json");
        TransactionEntity transactionEntity = objectMapper.readValue(createTransactionInput,TransactionEntity.class);
        transactionStatisticsService.createTransaction(transactionEntity);
     //   exception.expect(TransactionException.class);
       // exception.expectMessage(TransactionStatisticsConstants.STATUS_CODE_204);
    }

    @Test(expected = TransactionException.class)
    public void testCreateTransactionWithFutureDateInput() throws IOException, TransactionException {
        String createTransactionInput = FileUtils.readStringFromFile("/createTransactionInput_withFutureDate.json");
        TransactionEntity transactionEntity = objectMapper.readValue(createTransactionInput,TransactionEntity.class);
        transactionStatisticsService.createTransaction(transactionEntity);
        exception.expect(TransactionException.class);
        exception.expectMessage(TransactionStatisticsConstants.STATUS_CODE_422);
    }
    @Test
    public void testGetTransactionStatistics() throws IOException, TransactionException, DatatypeConfigurationException {
        List<TransactionEntity> transactionEntities = getTransactionEntities();
        Mockito.when(transactionStatisticsDao.getAllTransactions()).thenReturn(transactionEntities);
        TransactionStatisticsEntity transactionStatistics = transactionStatisticsService1.getTransactionStatistics();
        Assert.assertEquals(BigDecimal.valueOf(140.50).setScale(2, RoundingMode.HALF_UP),transactionStatistics.getMax());
        Assert.assertEquals(BigDecimal.valueOf(140.50).setScale(2, RoundingMode.HALF_UP),transactionStatistics.getSum());
        Assert.assertEquals(Long.valueOf(1),transactionStatistics.getCount());
    }

    @Test
    public void testDeleteAllTransactions(){
        Mockito.doNothing().when(transactionStatisticsDao).deleteAllTransactions(Mockito.any());
        transactionStatisticsService.deleteAllTransactions(Mockito.any());
    }

    protected List<TransactionEntity> getTransactionEntities() throws DatatypeConfigurationException, IOException {
        List<TransactionEntity> transactionEntities = new ArrayList<>();
        XMLGregorianCalendar xmlGregorianDate = getCurrentDateWithXmlGregorianFormat();
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setAmount(BigDecimal.valueOf(140.50));
        transactionEntity.setTimeStamp(xmlGregorianDate);
        transactionEntities.add(transactionEntity);
        String createTransactionInput = FileUtils.readStringFromFile("/createTransactionInput.json");
        TransactionEntity transactionEntityFromInput = objectMapper.readValue(createTransactionInput,TransactionEntity.class);
        transactionEntities.add(transactionEntityFromInput);
        return transactionEntities;
    }

    private XMLGregorianCalendar getCurrentDateWithXmlGregorianFormat() throws DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }
}
