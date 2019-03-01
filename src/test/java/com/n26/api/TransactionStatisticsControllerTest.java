package com.n26.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.model.TransactionStatisticsEntity;
import com.n26.service.TransactionStatisticsService;
import com.n26.utils.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URI;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionStatisticsControllerTest {

	private MockMvc mockMvc;

	@LocalServerPort
	int randomServerPort;

	@Autowired
	private TestRestTemplate template;

	@InjectMocks
	private TransactionStatisticsController transactionStatisticsController;

	@Mock
	private TransactionStatisticsService transactionStatisticsService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(transactionStatisticsController).build();
	}
	@Test
	public void testCreateTransaction() throws Exception {

	    String message = "The Trasanction has been Created";
		String createTransactionInput = FileUtils.readStringFromFile("/createTransactionInput.json");
		Mockito.doNothing().when(transactionStatisticsService).createTransaction(Mockito.any());
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transactions")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(createTransactionInput)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		Assert.assertEquals("Created", HttpStatus.CREATED.value(),mvcResult.getResponse().getStatus());
		Assert.assertEquals(message,mvcResult.getResponse().getContentAsString());
	}

	@Test
	//@Transactional
	//@Rollback(value = true)
	public void testGetTransaction_withRestTemplate() throws Exception {

		final String baseUrl = "http://localhost:" + randomServerPort + "/transactions/statistics";
		URI uri = new URI(baseUrl);
		String transactionStatisticsOutput = FileUtils.readStringFromFile("/transactionStatistics.json");
		ObjectMapper objectMapper = new ObjectMapper();
		TransactionStatisticsEntity transactionStatisticsEntity = objectMapper.readValue(transactionStatisticsOutput,TransactionStatisticsEntity.class);
		Mockito.when(transactionStatisticsService.getTransactionStatistics()).thenReturn(transactionStatisticsEntity);
		ResponseEntity<String> result = template/*.withBasicAuth("user","password")*/.getForEntity(uri, String.class);

		//Verify request succeed
		Assert.assertEquals(200, result.getStatusCodeValue());
		//Assert.assertEquals(true, result.getBody().contains("employeeList"));

		//RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/statistics")
		//		.accept(MediaType.APPLICATION_JSON_VALUE);
		//MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		//Assert.assertEquals("Status OK", HttpStatus.OK.value(),mvcResult.getResponse().getStatus());
		//Assert.assertNotNull(mvcResult.getResponse().getContentAsString());
	}

    @Test
    public void testGetTransactionStatistics() throws Exception {

        String transactionStatisticsOutput = FileUtils.readStringFromFile("/transactionStatistics.json");
        ObjectMapper objectMapper = new ObjectMapper();
        TransactionStatisticsEntity transactionStatisticsEntity = objectMapper.readValue(transactionStatisticsOutput,TransactionStatisticsEntity.class);
        Mockito.when(transactionStatisticsService.getTransactionStatistics()).thenReturn(transactionStatisticsEntity);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("transactions/statistics")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals("Status OK", HttpStatus.OK.value(),mvcResult.getResponse().getStatus());
        Assert.assertNotNull(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void testDeleteAllTransactions() throws Exception {

		String deleteTransactionInput = "{}";
		Mockito.doNothing().when(transactionStatisticsService).deleteAllTransactions(Mockito.any());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/transactions")
                .accept(MediaType.APPLICATION_JSON_VALUE)
				.content(deleteTransactionInput)
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);;
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals("Status OK", HttpStatus.NO_CONTENT.value(),mvcResult.getResponse().getStatus());
        Assert.assertNotNull(mvcResult.getResponse().getContentAsString());
    }
}
