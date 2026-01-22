package ru.yandex.practicum.commerce.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.api.payment.dto.PaymentDto;
import ru.yandex.practicum.commerce.api.payment.dto.enums.PaymentStatus;
import ru.yandex.practicum.commerce.api.store.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.api.store.dto.ProductDto;
import ru.yandex.practicum.commerce.api.exception.order.NoOrderFoundException;
import ru.yandex.practicum.commerce.payment.entity.PaymentEntity;
import ru.yandex.practicum.commerce.api.exception.payment.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStore;
    private final PaymentMapper paymentMapper;

    private final static BigDecimal TAX_RATE = BigDecimal.valueOf(1.1);

    @Transactional
    public PaymentDto doPayment(OrderDto order) {
        checkOrder(order);
        PaymentEntity payment = new PaymentEntity();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setFeeTotal(getTotalCost(order));
        payment.setTotalPayment(getProductsCost(order));
        payment.setDeliveryTotal(order.deliveryPrice());
        paymentRepository.save(payment);
        log.info("Payment processed successfully, payment id: {}", payment.getPaymentId());
        return paymentMapper.toDto(payment);
    }

    public BigDecimal getTotalCost(OrderDto order) {
        checkOrder(order);
        BigDecimal cost = getProductsCostFromStore(order.products()).multiply(TAX_RATE).add(order.deliveryPrice());
        log.info("Total cost calculated, order id: {}", order.orderId());
        return cost.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void refundPayment(UUID paymentId) {
        PaymentEntity payment = checkPayment(paymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        log.info("Change payment state to success, payment id: {}", paymentId);
    }

    public BigDecimal getProductsCost(OrderDto order) {
        return getProductsCostFromStore(order.products()).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void failedPayment(UUID paymentId) {
        PaymentEntity payment = checkPayment(paymentId);
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        log.info("Change payment state to failed, payment id: {}", paymentId);
    }

    private BigDecimal getProductsCostFromStore(Map<UUID, Long> products) {
        final BigDecimal[] cost = {BigDecimal.valueOf(0.0)};
        products.forEach((productId, quantity) -> {
            ProductDto product = shoppingStore.getProduct(productId);
            cost[0] = cost[0].add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        });
        return cost[0];
    }

    private void checkOrder(OrderDto order) {
        if (
                order.totalPrice() == null
                || order.deliveryPrice() == null
                || order.orderId() == null
                || order.productPrice() == null
        ) {
            throw new NotEnoughInfoInOrderToCalculateException("Not enough info for processing payment, order id: %s"
                    .formatted(order.orderId()));
        }
    }

    private PaymentEntity checkPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Payment with id: %s not found".formatted(paymentId)));
    }
}