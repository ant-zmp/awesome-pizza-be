package com.portfolio.awesomepizzabe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeDTO {

    @NotBlank
    private String id;
    @NotBlank(message = "Product Type name must not be empty.")
    private String name;
    private String description;
    private long version;

}
