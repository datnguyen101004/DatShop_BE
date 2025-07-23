package com.dat.backend.datshop.order.mapper;

import com.dat.backend.datshop.order.dto.BillResponse;
import com.dat.backend.datshop.order.entity.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BillMapper {
    @Mapping(target = "orderId", source = "id")
    BillResponse toBillResponse(Bill bill);
}
