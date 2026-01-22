package ru.yandex.practicum.commerce.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.api.delivery.client.DeliveryClient;
import ru.yandex.practicum.commerce.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.commerce.api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@RestController
public class DeliveryController implements DeliveryClient {

    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto addDelivery(DeliveryDto newDelivery) {
        log.info("Request to create a delivery {}", newDelivery);
        return deliveryService.addDelivery(newDelivery);
    }

    @Override
    public void successfulDelivery(UUID deliveryId) {
        log.info("Successful delivery {}", deliveryId);
        deliveryService.successfulDelivery(deliveryId);
    }

    @Override
    public void pickedDelivery(UUID deliveryId) {
        log.info("Accept products for delivery {}", deliveryId);
        deliveryService.pickedDelivery(deliveryId);
    }

    @Override
    public void failedDelivery(UUID deliveryId) {
        log.info("Delivery error {}", deliveryId);
        deliveryService.failedDelivery(deliveryId);
    }

    @Override
    public BigDecimal getDeliveryCost(OrderDto order) {
        log.info("Request for order delivery cost {}", order);
        return deliveryService.getDeliveryCost(order);
    }

}