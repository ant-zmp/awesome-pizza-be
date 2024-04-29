package com.portfolio.awesomepizzabe.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@NoArgsConstructor
public class ProductType {

    @Id
    private String id;
    private String name;
    private String description;

    public ProductType(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Version
    private long version;
}
