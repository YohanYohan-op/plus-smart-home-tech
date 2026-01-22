package ru.yandex.practicum.commerce.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.commerce.api.payment.dto.PaymentDto;
import ru.yandex.practicum.commerce.payment.entity.PaymentEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    PaymentEntity toEntity(PaymentDto payment);

    PaymentDto toDto(PaymentEntity payment);

}