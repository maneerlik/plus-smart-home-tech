package ru.yandex.practicum.mapper;

import ru.yandex.practicum.dto.AddressDto;
import ru.yandex.practicum.dto.DeliveryDto;
import ru.yandex.practicum.dto.in.CreateNewDeliveryRequest;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;

public final class DeliveryMapper {
    /**
     * Don't let anyone instantiate this class.
     */
    private DeliveryMapper() {

    }

    public static Delivery toDelivery(CreateNewDeliveryRequest request) {
        Delivery delivery = new Delivery();

        delivery.setFromAddress(toAddress(request.fromAddress()));
        delivery.setToAddress(toAddress(request.toAddress()));
        delivery.setDeliveryWeight(request.orderDto().getDeliveryWeight());
        delivery.setDeliveryVolume(request.orderDto().getDeliveryVolume());
        delivery.setFragile(request.orderDto().isFragile());

        return delivery;
    }

    public static DeliveryDto toDeliveryDto(Delivery delivery) {
        return DeliveryDto.builder()
                .fromAddress(toAddressDto(delivery.getFromAddress()))
                .toAddress(toAddressDto(delivery.getToAddress()))
                .orderId(delivery.getOrderId())
                .deliveryState(delivery.getDeliveryState())
                .build();
    }

    public static AddressDto toAddressDto(Address address) {
        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .build();
    }

    public static Address toAddress(AddressDto addressDto) {
        Address address = new Address();

        address.setCountry(addressDto.getCountry());
        address.setCity(addressDto.getCity());
        address.setStreet(addressDto.getStreet());
        address.setHouse(addressDto.getHouse());
        address.setFlat(addressDto.getFlat());

        return address;
    }
}
