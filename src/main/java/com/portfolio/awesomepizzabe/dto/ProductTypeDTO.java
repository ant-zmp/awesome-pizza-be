package com.portfolio.awesomepizzabe.dto;

import jakarta.validation.constraints.NotBlank;

public class ProductTypeDTO {
    private String id;

    @NotBlank(message = "Product Type name must not be empty.")
    private String name;

    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductTypeDTO() {}

    public ProductTypeDTO(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
