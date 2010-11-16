package com.udnaes.jerseymockito.rest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CoffeeOrder {
    private String description;
    private String id;
    private Integer price;

    public CoffeeOrder() {

    }

    public CoffeeOrder(String orderId, String description, Integer price) {
        this.description = description;
        this.id = orderId;
        this.price = price;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

}
