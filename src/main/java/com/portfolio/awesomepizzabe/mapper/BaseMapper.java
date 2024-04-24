package com.portfolio.awesomepizzabe.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public abstract class BaseMapper<M,D> {
    public abstract D toDTO(M model);
    public abstract List<D> toDTO(List<M> models);
    public Page<D> toDTO(Page<M> models) { return models.map(this::toDTO);}

    public abstract M toModel(D dto);
    public abstract List<M> toModel(List<D> dtos);
    public Page<M> toModel(Page<D> dtos) { return dtos.map(this::toModel);}
}
