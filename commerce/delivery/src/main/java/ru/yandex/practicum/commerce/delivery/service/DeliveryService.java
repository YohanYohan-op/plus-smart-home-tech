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
        DeliveryEntity delivery = deliveryRepository.findById(order.deliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException("Not found delivery with id=" + order.deliveryId()));

        double cost = 5.0;
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        if (warehouseAddress.getCity().contains("ADDRESS_1")) {
            cost *= 2;
        }
        if (warehouseAddress.getCity().contains("ADDRESS_2")) {
            cost *= 3;
        }
        if (order.fragile()) {
            cost *= 1.2;
        }

        cost += order.deliveryWeight() * 0.3;
        cost += order.deliveryVolume() * 0.2;

        if (!warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())) {
            cost *= 1.2;
        }

        return BigDecimal.valueOf(cost).setScale(2, RoundingMode.UP);
    }

    private DeliveryEntity checkAndGetDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Delivery with id %s not found".formatted(deliveryId)));
    }

}
