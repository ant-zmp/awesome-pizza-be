package com.portfolio.awesomepizzabe.mapper;

import com.portfolio.awesomepizzabe.dto.ProductTypeDTO;
import com.portfolio.awesomepizzabe.model.ProductType;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public abstract class ProductTypeMapper extends BaseMapper<ProductType, ProductTypeDTO> {
}
