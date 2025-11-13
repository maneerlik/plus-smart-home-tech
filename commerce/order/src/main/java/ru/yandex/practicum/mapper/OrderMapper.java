package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.OrderDto;
import ru.yandex.practicum.dto.ShoppingCartDto;
import ru.yandex.practicum.dto.in.CreateNewOrderRequest;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.DeliveryDetails;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderProduct;

import java.util.*;

public final class OrderMapper {
    /**
     * Don't let anyone instantiate this class.
     */
    private OrderMapper() {

    }

    public static Order toOrder(CreateNewOrderRequest createNewOrderRequest, String username) {
        Order order = new Order();
        ShoppingCartDto cart = createNewOrderRequest.shoppingCart();

        order.setProducts(toOrderProductList(cart.getProducts()));
        order.setShoppingCartId(cart.getCartId());
        order.setUsername(username);

        Address address = toAddres(createNewOrderRequest.deliveryAddress());
        order.setDeliveryAddress(address);

        return order;
    }

    public static List<OrderProduct> toOrderProductList(Map<UUID, Integer> products) {
        return products.entrySet().stream().map(entry -> {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProductId(entry.getKey());
            orderProduct.setQuantity(entry.getValue());
            return orderProduct;
        }).toList();
    }

    public static OrderDto toOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();

        orderDto.setProducts(toOrderProductDtoMap(order.getProducts()));

        boolean isDeliveryDetails = order.getDeliveryDetails() != null;
        DeliveryDetails deliveryDetails = isDeliveryDetails ? order.getDeliveryDetails() : null;

        if (isDeliveryDetails) {
            orderDto.setDeliveryWeight(deliveryDetails.getDeliveryWeight());
            orderDto.setDeliveryVolume(deliveryDetails.getDeliveryVolume());
            orderDto.setDeliveryPrice(deliveryDetails.getDeliveryPrice());
            orderDto.setDeliveryId(deliveryDetails.getDeliveryId());
            orderDto.setFragile(deliveryDetails.isFragile());
        }

        return orderDto;
    }

    public static List<OrderDto> toOrderDtoList(List<Order> orders) {
        return orders.stream().map(OrderMapper::toOrderDto).toList();
    }

    public static Map<UUID, Integer> toOrderProductDtoMap(List<OrderProduct> products) {
        Map<UUID, Integer> productsMap = new HashMap<>();

        if (Objects.nonNull(products)) {
            for (OrderProduct product : products) {
                productsMap.put(product.getProductId(), product.getQuantity());
            }
        }

        return productsMap;
    }

    public static AddressDto toAddressDto(Address address) {
        if (Objects.isNull(address)) return null;

        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }

    public static Address toAddres(AddressDto addressDto) {
        if (Objects.isNull(addressDto)) return null;

        Address address = new Address();

        address.setCountry(addressDto.getCountry());
        address.setCity(addressDto.getCity());
        address.setStreet(addressDto.getStreet());
        address.setHouse(addressDto.getHouse());
        address.setFlat(addressDto.getFlat());

        return address;
    }
}
