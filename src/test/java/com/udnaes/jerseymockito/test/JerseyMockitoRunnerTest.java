package com.udnaes.jerseymockito.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.udnaes.jerseymockito.JerseyMockitoRunner;
import com.udnaes.jerseymockito.JerseyMockitoPlaceholderBean;
import com.udnaes.jerseymockito.RestClientUtil;
import com.udnaes.jerseymockito.rest.OrderService;
import com.udnaes.jerseymockito.rest.PriceService;

/**
 * The purpose of this class is to demonstrate that classes can be 
 * mocked inside a Jersey using an using an in-process Grizzly container
 * 
 * Both multilevel beans (beans that have other beans injected) and re-mocking
 * of classes are tested.
 * 
 * @author Morten Udnæs, 2010
 * 
 */

public class JerseyMockitoRunnerTest {

	private static final String REST_CONTEXT = "/rest";
	private String REST_URL = "http://localhost";
	private int REST_PORT = 9998;

	private String SPRING_DUMMY_CONFIG = "classpath:spring-jersey-mockito-test.xml";
	private String JERSEY_CLASSES = "com.udnaes.jerseymockito";

	private String orderServiceBeanName = "orderService";
	private OrderService mockedOrderService;
	private String priceServiceBeanName = "priceService";
	private PriceService mockedPriceService;
	
	private JerseyMockitoRunner runner;
	

	@BeforeClass
	public static void turnOfLogging() {
		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.SEVERE);
	}

	@Before
	public void resetPlaceHolder() {
		JerseyMockitoPlaceholderBean.reset();
		
		mockedOrderService = mock(OrderService.class);
		JerseyMockitoPlaceholderBean.enableMock(orderServiceBeanName, mockedOrderService);

		mockedPriceService = mock(PriceService.class);
		JerseyMockitoPlaceholderBean.enableMock(priceServiceBeanName, mockedPriceService);
		
		runner = new JerseyMockitoRunner();
		runner.startServer(REST_URL, REST_PORT, REST_CONTEXT, SPRING_DUMMY_CONFIG, JERSEY_CLASSES);
	}
	
	@Test
	public void shouldBeAbleToCreateMockitoInjectedBean() {
		String testBeanName = "testbean";
		JerseyMockitoPlaceholderBean.enableMock(testBeanName, mockedOrderService);
		runner.createBean(testBeanName, JerseyMockitoPlaceholderBean.class);

		OrderService bean = (OrderService) runner.getSpringContext().getBean(testBeanName);
		assertNotNull(bean);
		assertTrue(bean instanceof OrderService);
		assertTrue(bean.getClass().getCanonicalName().contains("EnhancerByMockito"));
	}

	@Test
	public void restServiceShouldReturnMockedValue() {
		String orderId_1 = "10"; //Latte
		String mockedDescription_1 = "Tea";
		
		when(mockedOrderService.findOrder(orderId_1)).thenCallRealMethod();
		when(mockedOrderService.findDescription(orderId_1)).thenReturn(mockedDescription_1);

		JsonNode response = RestClientUtil.doGet(REST_URL, REST_PORT, REST_CONTEXT +"/order/" + orderId_1);
		assertEquals(mockedDescription_1, response.get("description").getValueAsText());
	}

	@Test
	public void changingAlreadyMockedValueShouldWork () {
		String orderId_1 = "10"; //Latte
		String mockedDescription_1 = "Tea";
		Integer mockedPrice_1 = 77;

		when(mockedOrderService.findOrder(orderId_1)).thenCallRealMethod();
		when(mockedOrderService.getPrice(mockedDescription_1)).thenReturn(mockedPrice_1);
		when(mockedOrderService.findDescription(orderId_1)).thenReturn(mockedDescription_1);
		
		JsonNode response = RestClientUtil.doGet(REST_URL, REST_PORT, REST_CONTEXT +"/order/" + orderId_1);
		assertEquals(mockedDescription_1, response.get("description").getValueAsText());
		assertEquals(mockedPrice_1, new Integer(response.get("price").getValueAsText()));
		
		String orderId_2 = "20"; //Cappuccino
		String mockedDescription_2 = "Water";
		Integer mockedPrice_2 = 66;

		when(mockedOrderService.findOrder(orderId_2)).thenCallRealMethod();
		when(mockedOrderService.findDescription(orderId_2)).thenReturn(mockedDescription_2);
		when(mockedOrderService.getPrice(mockedDescription_2)).thenReturn(mockedPrice_2);

		response = RestClientUtil.doGet(REST_URL, REST_PORT, REST_CONTEXT +"/order/" + orderId_2);
		assertEquals(mockedDescription_2, response.get("description").getValueAsText());
		assertEquals(mockedPrice_2, new Integer(response.get("price").getValueAsText()));
	}
	
	@Test
	public void mockingSpringBeansWithinSpringBeansShouldWork () {
		String orderId_1 = "10"; //Latte
		String mockedDescription_1 = "Tea";
		Integer mockedPrice_1 = 77;

		when(mockedOrderService.findOrder(orderId_1)).thenCallRealMethod();
		when(mockedOrderService.getPrice(mockedDescription_1)).thenCallRealMethod();
		when(mockedOrderService.findDescription(orderId_1)).thenReturn(mockedDescription_1);

		//Mock methon on bean that is injected on top level bean
		when(mockedPriceService.calculatePrice(mockedDescription_1)).thenReturn(mockedPrice_1);
		
		JsonNode response = RestClientUtil.doGet(REST_URL, REST_PORT, REST_CONTEXT +"/order/" + orderId_1);
		assertEquals(mockedDescription_1, response.get("description").getValueAsText());
		assertEquals(mockedPrice_1, new Integer(response.get("price").getValueAsText()));
	}
	

	@After
	public void stop() {
		runner.stopServer();
	}
}
