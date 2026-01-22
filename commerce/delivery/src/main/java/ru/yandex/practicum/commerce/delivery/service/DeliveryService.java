package ru.yandex.practicum.commerce.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.api.delivery.dto.DeliveryDto;
import ru.yandex.practicum.commerce.api.delivery.dto.enums.DeliveryState;
import ru.yandex.practicum.commerce.api.order.client.OrderClient;
import ru.yandex.practicum.commerce.api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.api.warehouse.client.WarehouseClient;
import ru.yandex.practicum.commerce.api.warehouse.dto.AddressDto;
import ru.yandex.practicum.commerce.delivery.entity.DeliveryEntity;
import ru.yandex.practicum.commerce.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Transactional
    public DeliveryDto addDelivery(DeliveryDto newDelivery) {
        DeliveryEntity delivery = deliveryMapper.toEntity(newDelivery);
        delivery.setDeliveryState(DeliveryState.CREATED);
        log.info("Delivery planned: {}.", delivery);
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Transactional
    public void successfulDelivery(UUID deliveryId) {
        DeliveryEntity delivery = checkAndGetDelivery(deliveryId);
        log.info("Delivery with id={} is successful.", deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderClient.completed(delivery.getOrderId());
    }

    @Transactional
    public void pickedDelivery(UUID deliveryId) {
        DeliveryEntity delivery = checkAndGetDelivery(deliveryId);
        log.info("Delivery with id={} picked.", deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
        orderClient.assembly(delivery.getOrderId());
    }

    @Transactional
    public void failedDelivery(UUID deliveryId) {
        DeliveryEntity delivery = checkAndGetDelivery(deliveryId);
        log.info("Delivery with id={} failed", deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        orderClient.deliveryFailed(delivery.getOrderId());
    }

    public BigDecimal getDeliveryCost(OrderDto order) {
        log.info("Starting delivery cost calculation for orderId: {}, deliveryId: {}",
                order.orderId(), order.deliveryId());

        DeliveryEntity delivery = deliveryRepository.findById(order.deliveryId())
                .orElseThrow(() -> {
                    log.error("Delivery not found for order. OrderId: {}, DeliveryId: {}",
                            order.orderId(), order.deliveryId());
                    return new NoDeliveryFoundException("Not found delivery with id=" + order.deliveryId());
                });

        BigDecimal cost = BigDecimal.valueOf(5.0);
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        log.debug("Warehouse city: {}, street: {}, orderId: {}",
                warehouseAddress.getCity(), warehouseAddress.getStreet(), order.orderId());
        log.debug("Delivery address: {}", delivery.getToAddress());

        if (warehouseAddress.getCity().contains("ADDRESS_1")) {
            cost = cost.multiply(BigDecimal.valueOf(2));
            log.debug("If contains ADDRESS_1, cost: {}, coefficient: {}, orderId: {}", cost, 2, order.orderId());
        }
        if (warehouseAddress.getCity().contains("ADDRESS_2")) {
            cost = cost.multiply(BigDecimal.valueOf(3));
            log.debug("If contains ADDRESS_2, cost: {}, coefficient: {}, orderId: {}", cost, 3, order.orderId());
        }
        if (order.fragile()) {
            cost = cost.multiply(BigDecimal.valueOf(1.2));
            log.debug("Order is fragile, cost: {}, coefficient: {}, orderId: {}", cost, 1.2, order.orderId());
        }

        cost = cost.add(BigDecimal.valueOf(order.deliveryWeight() * 0.3));
        log.debug("Calculated with weight: {} , cost = {}, coefficient: {}, orderId: {}",
                order.deliveryWeight(), cost, 0.3, order.orderId());

        cost = cost.add(BigDecimal.valueOf(order.deliveryVolume() * 0.2));
        log.debug("Calculated with volume: {} , cost = {}, coefficient: {}, orderId: {}", order.deliveryVolume(), cost, 0.2, order.orderId());

        if (!warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())) {
            cost = cost.multiply(BigDecimal.valueOf(1.2));
            log.debug("Cost where delivery near warehouse: {}, coefficient: {}, orderId: {}", cost, 1.2, order.orderId());
        }
        log.info("Delivery final cost calculated: {} for orderId: {}, deliveryId: {}", cost, order.orderId(), order.deliveryId());

        return cost.setScale(2, RoundingMode.UP);
    }

    private DeliveryEntity checkAndGetDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery with id %s not found".formatted(deliveryId)));
    }

}
