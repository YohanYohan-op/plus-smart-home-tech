package ru.yandex.practicum.commerce.api.payment.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.api.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {

    @PostMapping
    PaymentDto doPayment(@Valid @RequestBody OrderDto order);

    @PostMapping("/totalCost")
    BigDecimal getTotalCost(@Valid OrderDto order);

    @PostMapping("/refund")
    void refundPayment(@Valid @RequestBody UUID paymentId);

    @PostMapping("/productCost")
    BigDecimal getProductsCost(OrderDto order);

    @PostMapping("/failed")
    void failedPayment(UUID paymentId);

}
