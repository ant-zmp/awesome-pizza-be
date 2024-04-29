package com.portfolio.awesomepizzabe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO{

    private String id;
    @NotBlank
    private String name;
    private String description;
    private List<String> ingredients = new ArrayList<>();
    @Valid
    @NotNull
    private ProductTypeDTO productType;
    private double price;
    private boolean available = true;
    private String imageId;

}
