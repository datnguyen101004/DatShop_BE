package com.dat.backend.datshop.order.entity;

public enum OrderStatus {
    PENDING,
    WAITING_FOR_PAYMENT,
    PREPARING,
    SHIPPING,
    SUCCESS,
    CANCEL,
}
