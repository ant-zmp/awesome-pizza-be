package com.portfolio.awesomepizzabe.dto;

import com.portfolio.awesomepizzabe.model.Product;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductDetailDTO{
    private String id;
    private String name;
    private double price;
    private String imageId;
    private int quantity;

    public ProductDetailDTO() {}
    public ProductDetailDTO(Product product, int quantity) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.imageId = product.getImageId();
        this.quantity = quantity;
    }
}
