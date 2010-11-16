package com.udnaes.jerseymockito.rest;

public class PriceService {

	public Integer calculatePrice(String description) {
		if (description.equals("Latte")) {
			return Integer.valueOf(88);
		} else if (description.equals("Cappuccino")) {
			return Integer.valueOf(99);
		} else {
			throw new IllegalArgumentException("Can't calculate price for description: " + description);
		}
	}
}
