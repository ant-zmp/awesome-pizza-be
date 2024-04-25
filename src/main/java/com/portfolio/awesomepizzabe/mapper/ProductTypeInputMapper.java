package com.portfolio.awesomepizzabe.mapper;

import com.portfolio.awesomepizzabe.dto.ProductTypeInputDTO;
import com.portfolio.awesomepizzabe.model.ProductType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ProductTypeInputMapper extends BaseMapper<ProductType, ProductTypeInputDTO> {
}
