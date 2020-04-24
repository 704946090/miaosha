package com.ceshi.service.model;

import java.math.BigDecimal;

public class PayMsgModel {

    private BigDecimal orderPrices;

    private BigDecimal price;

    private Integer amount;

    private String title;

    private String imgUrl;

    private String description;

    private String id;

    public BigDecimal getOrderPrices() {
        return orderPrices;
    }

    public void setOrderPrices(BigDecimal orderPrice) {
        this.orderPrices = orderPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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
}
