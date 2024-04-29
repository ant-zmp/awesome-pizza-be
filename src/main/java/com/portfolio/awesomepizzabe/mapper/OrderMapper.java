package com.portfolio.awesomepizzabe.mapper;

import com.portfolio.awesomepizzabe.dto.OrderDTO;
import com.portfolio.awesomepizzabe.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class OrderMapper extends BaseMapper<Order, OrderDTO> {
}
