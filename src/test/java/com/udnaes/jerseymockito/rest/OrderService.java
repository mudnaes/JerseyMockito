package com.udnaes.jerseymockito.rest;

import com.sun.jersey.api.core.InjectParam;

public class OrderService {
	@InjectParam private PriceService priceService;
	
	public void setPriceService (PriceService theService) {
		this.priceService = theService;
	}

    public String findDescription(String id) {
		if (id.equals("10")) {
			return "Latte";
		} else if (id.equals("20")) {
			return "Cappuccino";
		} else {
			throw new IllegalArgumentException("Can't find description for id: " + id);
		}
    }

    public Integer getPrice(String description) {
    	Integer price = priceService.calculatePrice(description);
        return price;
    }

    public CoffeeOrder findOrder(String id) {
    	String description = findDescription(id);
        return new CoffeeOrder(id, findDescription(id), getPrice(description));
    }
    
}
