package com.udnaes.jerseymockito.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.api.spring.Autowire;
import com.sun.jersey.api.spring.AutowireMode;
import com.sun.jersey.spi.inject.Inject;


@Component
@Scope("singleton")
@Path("/order/{id}")
@Autowire(mode=AutowireMode.BY_NAME, dependencyCheck=true)
public class CoffeeShopEndpoint {

	@InjectParam private OrderService orderService;

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @GET
	@Produces("application/json")
	public CoffeeOrder getOrderName(@PathParam("id") String id){

		return orderService.findOrder(id);
	}
    
    
    
    

}
