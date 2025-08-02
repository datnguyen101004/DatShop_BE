package com.dat.backend.datshop.delivery.mapper;


import com.dat.backend.datshop.delivery.dto.CreateDeliveryResponse;
import com.dat.backend.datshop.delivery.entity.Delivery;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    Delivery createDeliveryRequestToDeliveryEntity(CreateDeliveryResponse createDeliveryResponse);
}
