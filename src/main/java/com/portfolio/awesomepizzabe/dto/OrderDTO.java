package com.portfolio.awesomepizzabe.dto;

import com.portfolio.awesomepizzabe.model.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private String id;
    private String orderCode;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private double totalPrice;
    @NotEmpty
    private Map<String, Integer> productQuantity = new HashMap<>();
    private String notes;
    private String reason;
    @NotBlank
    private String address;
    private long version;

}
