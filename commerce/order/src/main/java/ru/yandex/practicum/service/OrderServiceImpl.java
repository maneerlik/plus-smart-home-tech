package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.contract.DeliveryClient;
import ru.yandex.practicum.contract.PaymentClient;
import ru.yandex.practicum.contract.WarehouseClient;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.dto.in.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.in.CreateNewDeliveryRequest;
import ru.yandex.practicum.dto.in.CreateNewOrderRequest;
import ru.yandex.practicum.dto.in.ProductReturnRequest;
import ru.yandex.practicum.enums.OrderState;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.DeliveryDetails;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderProduct;
import ru.yandex.practicum.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    public List<OrderDto> getUserOrders(String username) {
        checkUsername(username);
        return OrderMapper.toOrderDtoList(orderRepository.findByUsername(username));
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(String username, CreateNewOrderRequest request) {
        checkUsername(username);
        Order order = initializeOrder(request, username);
        reserveProductsAndSetDeliveryDetails(order, request);
        fetchAndSetPrices(order);
        setOnDelivery(order);

        return saveAndMap(order);
    }

    @Override
    @Transactional
    public OrderDto returnProducts(ProductReturnRequest request) {
        Order order = getOrderById(request.orderId());

        Map<UUID, Integer> products = request.products();
        returnBookedProducts(products);
        updateProductQuantities(order, products);

        order.setOrderState(OrderState.PRODUCT_RETURNED);

        return saveAndMap(order);
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        PaymentDto payment = createPayment(order);
        order.setPaymentId(payment.getPaymentId());

        order.setOrderState(OrderState.ON_PAYMENT);

        return saveAndMap(order);
    }

    @Override
    public OrderDto successfulPayment(UUID orderId) {
        return changeOrderState(orderId, OrderState.PAID);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        return changeOrderState(orderId, OrderState.PAYMENT_FAILED);
    }

    @Override
    public OrderDto successfulDelivery(UUID orderId) {
        return changeOrderState(orderId, OrderState.DELIVERED);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        return changeOrderState(orderId, OrderState.DELIVERY_FAILED);
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        return changeOrderState(orderId, OrderState.COMPLETED);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        Order order = getOrderById(orderId);
        BigDecimal totalCost = getTotalPrice(order);
        order.setTotalPrice(totalCost);

        return saveAndMap(order);
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        Order order = getOrderById(orderId);
        BigDecimal deliveryPrice = getDeliveryPrice(order);
        DeliveryDetails details = order.getDeliveryDetails();
        details.setDeliveryPrice(deliveryPrice);

        return saveAndMap(order);
    }

    @Override
    public OrderDto assembleOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        assembleProducts(order);
        order.setOrderState(OrderState.ASSEMBLED);

        return saveAndMap(order);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        return changeOrderState(orderId, OrderState.ASSEMBLY_FAILED);
    }

    private Order initializeOrder(CreateNewOrderRequest request, String username) {
        Order order = OrderMapper.toOrder(request, username);
        order.setOrderId(UUID.randomUUID());
        order.setOrderState(OrderState.NEW);

        return order;
    }

    private void reserveProductsAndSetDeliveryDetails(Order order, CreateNewOrderRequest request) {
        BookedProductsDto booked = warehouseClient.checkProductsQuantity(request.shoppingCart());
        createDeliveryDetails(booked, order);
    }

    private void fetchAndSetPrices(Order order) {
        BigDecimal productPrice = paymentClient.getProductCost(OrderMapper.toOrderDto(order));
        order.setProductPrice(productPrice);
    }

    private OrderDto saveAndMap(Order order) {
        return OrderMapper.toOrderDto(orderRepository.save(order));
    }

    private OrderDto changeOrderState(UUID orderId, OrderState state) {
        Order order = getOrderById(orderId);
        order.setOrderState(state);

        order = orderRepository.save(order);

        return OrderMapper.toOrderDto(order);
    }

    private void checkUsername(String username) {
        if (Objects.isNull(username) || username.isBlank()) {
            throw new NotAuthorizedUserException("User not authorized");
        }
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NoOrderFoundException("Order not found"));
    }

    private void updateProductQuantities(Order order, Map<UUID, Integer> returnedProducts) {
        List<OrderProduct> products = order.getProducts();

        for (OrderProduct product : products) {
            Integer returnQuantity = returnedProducts.get(product.getProductId());

            if (Objects.nonNull(returnQuantity)) {
                int newQuantity = Math.max(0, product.getQuantity() - returnQuantity);
                product.setQuantity(newQuantity);
            }
        }
    }

    private void setOnDelivery(Order order) {
        DeliveryDto deliveryDto = createDelivery(order);
        DeliveryDetails deliveryDetails = order.getDeliveryDetails();
        deliveryDetails.setDeliveryId(deliveryDto.getDeliveryId());
        order.setOrderState(OrderState.ON_DELIVERY);
    }

    private DeliveryDto createDelivery(Order order) {
        OrderDto orderDto = OrderMapper.toOrderDto(order);
        AddressDto fromAddress = warehouseClient.getAddress();
        AddressDto toAddress = OrderMapper.toAddressDto(order.getDeliveryAddress());

        CreateNewDeliveryRequest request = new CreateNewDeliveryRequest(orderDto, fromAddress, toAddress);

        return deliveryClient.createDelivery(request);
    }

    private void returnBookedProducts(Map<UUID, Integer> products) {
        warehouseClient.returnBookedProducts(products);
    }

    private PaymentDto createPayment(Order order) {
        return paymentClient.createPayment(OrderMapper.toOrderDto(order));
    }

    private BigDecimal getProductPrice(Order order) {
        return paymentClient.getProductCost(OrderMapper.toOrderDto(order));
    }

    private BigDecimal getDeliveryPrice(Order order) {
        return deliveryClient.calculateDeliveryCost(order.getDeliveryDetails().getDeliveryId());
    }

    private BigDecimal getTotalPrice(Order order) {
        return paymentClient.getTotalCost(OrderMapper.toOrderDto(order));
    }

    private void createDeliveryDetails(BookedProductsDto bookedProducts, Order order) {
        DeliveryDetails deliveryDetails = new DeliveryDetails();
        deliveryDetails.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        deliveryDetails.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        deliveryDetails.setFragile(bookedProducts.isFragile());
        order.setDeliveryDetails(deliveryDetails);
    }

    private void assembleProducts(Order order) {
        AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest(
                OrderMapper.toOrderProductDtoMap(order.getProducts()),
                order.getOrderId()
        );

        warehouseClient.assemblyProducts(request);
    }

    private BookedProductsDto getBookedProduct(CreateNewOrderRequest request) {
        return warehouseClient.checkProductsQuantity(request.shoppingCart());
    }
}
