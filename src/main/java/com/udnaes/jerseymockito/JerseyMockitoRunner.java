package com.udnaes.jerseymockito;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.ws.rs.core.UriBuilder;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.sun.grizzly.http.SelectorThread;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.jersey.api.container.grizzly.GrizzlyServerFactory;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;

/**
 * The purpose of this class is to be able to use Mockito to mock
 * server side classes. This can't be done with Jersey Test Framework
 * or ordinary mockito classes.
 * 
 * To mock classes inside beans, the bean must be defined in a xml-based Spring context.
 * Beans should be defined with class="com.udnaes.jerseymockito.JerseyMockitoPlaceholderBean"
 * 
 * Next the beans should be mocked using standard mockito (i.e mock(...)) and then added
 * to the placeholder bean using JerseyMockitoPlaceholderBean.enableMock(..)
 * 
 * See {@link JerseyMockitoRunnerTest} for details
 * 
 * @author Morten Udnæs, 2010
 *
 */
public class JerseyMockitoRunner{
	private SelectorThread server;
	
    public XmlWebApplicationContext getSpringContext() {
        ServletAdapter adapter = (ServletAdapter) server.getAdapter();
        ServletContext servletContext = adapter.getServletInstance().getServletConfig().getServletContext();
        return (XmlWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }

    public Object getBean(String beanName) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) getSpringContext().getBeanFactory();
        return beanFactory.getBean(beanName);
    }

    @SuppressWarnings("rawtypes")
    public void createBean(String beanName,  Class clazz) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) getSpringContext().getBeanFactory();
        beanFactory.registerBeanDefinition(beanName, BeanDefinitionBuilder.genericBeanDefinition(clazz.getName()).getBeanDefinition());
    }

  
    public void startServer(String url, int port, String context, String springConfigForTest, String jerseyAnnotatedClasses) {

        try {
            ServletAdapter adapter;
 
            final URI baseUri = UriBuilder.fromUri(url+context).port(port).build();
            adapter = new ServletAdapter();
            adapter.addInitParameter("com.sun.jersey.config.property.packages", jerseyAnnotatedClasses);
            adapter.addContextParameter("contextConfigLocation", springConfigForTest);
            adapter.addServletListener("org.springframework.web.context.ContextLoaderListener");
            adapter.setServletInstance(new SpringServlet());

            adapter.setContextPath(baseUri.getPath());
            adapter.setProperty("load-on-startup", "1");

            server = GrizzlyServerFactory.create(baseUri, adapter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void stopServer() {
    	server.stopEndpoint();
   }
}
