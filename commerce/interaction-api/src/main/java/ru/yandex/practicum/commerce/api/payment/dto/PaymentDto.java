package ru.yandex.practicum.commerce.api.payment.dto;

import ru.yandex.practicum.commerce.api.payment.dto.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDto(UUID paymentId, BigDecimal totalPayment, BigDecimal deliveryTotal, BigDecimal feeTotal,
                         PaymentStatus status) {
}