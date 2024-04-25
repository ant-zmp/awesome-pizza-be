package com.portfolio.awesomepizzabe.mapper;

import com.portfolio.awesomepizzabe.dto.ProductDTO;
import com.portfolio.awesomepizzabe.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductTypeMapper.class})
public abstract class ProductMapper extends BaseMapper<Product, ProductDTO> {
}
