package com.portfolio.awesomepizzabe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {

    @Id
    private String id;
    private String name;
    private String description;

}
