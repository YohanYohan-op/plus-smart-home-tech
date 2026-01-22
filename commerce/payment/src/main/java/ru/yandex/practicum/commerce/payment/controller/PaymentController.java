package ru.yandex.practicum.commerce.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.api.payment.client.PaymentClient;
import ru.yandex.practicum.commerce.api.payment.dto.PaymentDto;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentClient {
    private final PaymentService paymentService;

    public PaymentDto doPayment(OrderDto order) {
        log.info("Create payment for order {}", order);
        return paymentService.doPayment(order);
    }

    public BigDecimal getTotalCost(OrderDto order) {
        log.info("Get total cost for order {}", order);
        return paymentService.getTotalCost(order);
    }

    public void refundPayment(UUID paymentId) {
        log.info("Refund payment id={}", paymentId);
        paymentService.refundPayment(paymentId);
    }

    public BigDecimal getProductsCost(OrderDto order) {
        log.info("Get products cost for order {}", order);
        return paymentService.getProductsCost(order);
    }

    public void failedPayment(UUID paymentId) {
        log.info("Payment id={} failed.", paymentId);
        paymentService.failedPayment(paymentId);
    }

}