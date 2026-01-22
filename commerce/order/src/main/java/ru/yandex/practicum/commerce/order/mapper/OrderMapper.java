package ru.yandex.practicum.commerce.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.commerce.api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.order.entity.OrderEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderDto toDto(OrderEntity order);

    @Mapping(target = "username", ignore = true)
    OrderEntity toEntity(OrderDto orderDto);
}
