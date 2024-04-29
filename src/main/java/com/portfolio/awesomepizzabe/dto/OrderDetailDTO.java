package com.portfolio.awesomepizzabe.dto;

import com.portfolio.awesomepizzabe.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO implements Serializable {

    private String id;
    private String orderCode;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private double totalPrice;
    private Set<ProductDetailDTO> productQuantity = new HashSet<>();
    private String notes;
    private String reason;
    private int inLineBefore;

}
