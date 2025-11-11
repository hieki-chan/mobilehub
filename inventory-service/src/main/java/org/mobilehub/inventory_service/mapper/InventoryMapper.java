package org.mobilehub.inventory_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mobilehub.inventory_service.dto.response.InventoryReservationItemResponse;
import org.mobilehub.inventory_service.dto.response.InventoryReservationResponse;
import org.mobilehub.inventory_service.dto.response.InventoryStockResponse;
import org.mobilehub.inventory_service.entity.InventoryReservation;
import org.mobilehub.inventory_service.entity.InventoryReservationItem;
import org.mobilehub.inventory_service.entity.InventoryStock;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {
    // === STOCK ===
    @Mapping(target = "available", expression = "java(stock.getAvailable())")
    InventoryStockResponse toStockResponse(InventoryStock stock);


    // === RESERVATION ===
    @Mapping(target = "items", expression = "java(toItemResponses(reservation.getItems()))")
    InventoryReservationResponse toReservationResponse(InventoryReservation reservation);


    // === ITEM ===
    InventoryReservationItemResponse toItemResponse(InventoryReservationItem item);


    // === HELPER ===
    default List<InventoryReservationItemResponse> toItemResponses(List<InventoryReservationItem> items) {
        if (items == null) return List.of();
        return items.stream().map(this::toItemResponse).collect(Collectors.toList());
    }
}
