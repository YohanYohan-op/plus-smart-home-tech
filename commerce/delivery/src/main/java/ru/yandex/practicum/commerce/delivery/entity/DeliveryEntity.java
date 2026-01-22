package ru.yandex.practicum.commerce.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.commerce.api.delivery.dto.enums.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "delivery")
public class DeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deliveryId;

    @ManyToOne
    @JoinColumn(name = "from_address_id")
    private DeliveryAddress fromAddress;

    @ManyToOne
    @JoinColumn(name = "to_address_id")
    private DeliveryAddress toAddress;

    private UUID orderId;

    private DeliveryState deliveryState;

}
