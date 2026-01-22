package ru.yandex.practicum.commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.api.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.api.delivery.client.DeliveryClient;
import ru.yandex.practicum.commerce.api.order.dto.OrderDto;
import ru.yandex.practicum.commerce.api.order.dto.ProductReturnRequest;
import ru.yandex.practicum.commerce.api.order.dto.enums.OrderState;
import ru.yandex.practicum.commerce.api.payment.client.PaymentClient;
import ru.yandex.practicum.commerce.api.payment.dto.PaymentDto;
import ru.yandex.practicum.commerce.api.warehouse.client.WarehouseClient;
import ru.yandex.practicum.commerce.api.warehouse.dto.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.commerce.api.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.commerce.order.entity.OrderEntity;
import ru.yandex.practicum.commerce.api.exception.order.NoOrderFoundException;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    public List<OrderDto> getOrders(String username) {
        log.info("Get user {} orders", username);
        return orderRepository.findAllByUsername(username).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Transactional
    public OrderDto createOrder(String username, OrderDto newOrder) {
        final ShoppingCartDto shoppingCartDto = new ShoppingCartDto(
                newOrder.shoppingCartId(),
                newOrder.products()
        );
        BookedProductsDto bookedProductsDto = warehouseClient.checkProductCount(shoppingCartDto);
        log.info("Booking products for order: {}", bookedProductsDto);
        OrderEntity order = orderMapper.toEntity(newOrder);
        order.setOrderId(null);
        order.setUsername(username);
        order.setState(OrderState.NEW);
        log.info("Order created: {}", order);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto returnProducts(ProductReturnRequest request) {
        OrderEntity order = findById(request.orderId());
        request.products().forEach(
                (productId, quantity) -> {
                    long newQuantity = order.getProducts().get(productId) - quantity;
                    if (newQuantity <= 0) {
                        order.getProducts().remove(productId);
                    } else {
                        order.getProducts().put(productId, newQuantity);
                    }
                }
        );
        orderRepository.save(order);
        warehouseClient.returnToWarehouse(request.products());
        log.info("Order {} is returned.", order);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        OrderEntity order = findById(orderId);
        order.setState(OrderState.ON_PAYMENT);
        OrderDto orderDto = orderMapper.toDto(order);
        PaymentDto paymentDto = paymentClient.doPayment(orderDto);
        order.setPaymentId(paymentDto.paymentId());
        order.setDeliveryPrice(paymentDto.deliveryTotal());
        order.setProductPrice(paymentDto.totalPayment());
        order.setTotalPrice(paymentDto.feeTotal());
        log.info("Order with id={} on payment.", orderId);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        OrderEntity order = findById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);
        log.info("Order with id={} payment failed.", orderId);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        OrderEntity order = findById(orderId);
        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);
        log.info("Order with id={} delivered", orderId);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        OrderEntity order = findById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);
        log.info("Order with id={} delivery failed.", orderId);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto completed(UUID orderId) {
        OrderEntity order = findById(orderId);
        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);
        log.info("Order with id={} completed.", orderId);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto calculateTotal(UUID orderId) {
        OrderEntity order = findById(orderId);
        BigDecimal productsCost = paymentClient.getProductsCost(orderMapper.toDto(order));
        order.setProductPrice(productsCost);
        orderRepository.save(order);
        log.info("Total cost for order {} is {}", orderId, productsCost);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto calculateDelivery(UUID orderId) {
        OrderEntity order = findById(orderId);
        BigDecimal deliveryCost = deliveryClient.getDeliveryCost(orderMapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);
        orderRepository.save(order);
        log.info("Delivery cost for order {} is {}", orderId, deliveryCost);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        OrderEntity order = findById(orderId);
        AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest(order.getProducts(), orderId);
        BookedProductsDto bookedProducts = warehouseClient.assemblyOrder(request);
        order.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        order.setFragile(bookedProducts.isFragile());
        order.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        order.setState(OrderState.ASSEMBLED);
        orderRepository.save(order);
        log.info("Order is assembled: {}", order);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        OrderEntity order = findById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);
        log.warn("Order is not assembled; {} ", order);
        return orderMapper.toDto(order);
    }

    public Map<UUID, Long> getProducts(UUID orderId) {
        OrderEntity order = findById(orderId);
        return order.getProducts();
    }

    private OrderEntity findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Order with id=" + orderId + " not found."));
    }

}