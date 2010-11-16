package com.udnaes.jerseymockito;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;

public class JerseyMockitoPlaceholderBean<T> implements BeanNameAware, FactoryBean {

	//Hash map of beans that have been mockito'ed
    private static Map<String, Object> beansByName = new HashMap<String, Object>();

    //Spring sets beanName automatically before getObject is called.
    private String beanName;  
  
    public void setBeanName(String beanName) {		
        this.beanName = beanName;
    }
    
    public static void enableMock(String beanName, Object bean) {
    	beansByName.put(beanName, bean);
    }
   
    public Object getObject() {
        if (beansByName.get(beanName)== null) {
           System.out.println("WARNING!!! Requested Spring bean was not found in beansByName hashmap. Beanname: " + this.beanName);
           System.out.println("           Use MockitoPlaceHolderBean.enableMock to correct this if needed...");
           return null;
        } else
            return beansByName.get(beanName);
    }
   
    public Class<?> getObjectType() {
        if (beansByName.get(beanName) == null) {
        	return null;
        }
        return beansByName.get(beanName).getClass();
    }
    
    public boolean isSingleton() {
        return true;
    }

    public static void reset() {
        beansByName = new HashMap<String, Object>();
    }
}