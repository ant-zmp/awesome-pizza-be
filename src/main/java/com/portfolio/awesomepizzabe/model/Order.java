package com.portfolio.awesomepizzabe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String id;
    private String orderCode;
    private LocalDateTime orderDate = LocalDateTime.now();
    private OrderStatus status = OrderStatus.PLACED;
    private double totalPrice;
    private Map<String, Integer> productQuantity = new HashMap<>();
    private String notes;
    private String reason;

    @Version
    private long version;

}
