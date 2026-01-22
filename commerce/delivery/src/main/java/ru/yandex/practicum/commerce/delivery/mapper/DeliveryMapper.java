package ru.yandex.practicum.commerce.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.commerce.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.commerce.delivery.entity.DeliveryEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeliveryMapper {
    DeliveryDto toDto(DeliveryEntity delivery);

    DeliveryEntity toEntity(DeliveryDto deliveryDto);
}
