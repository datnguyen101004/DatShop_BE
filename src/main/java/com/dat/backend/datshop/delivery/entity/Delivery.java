package com.dat.backend.datshop.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    private Long userId; // ID của người dùng nhận hàng
    private Long shopId; // ID của cửa hàng giao hàng
    private String address; // Địa chỉ giao hàng
    private String phoneNumber; // Số điện thoại liên hệ
    private String deliveryStatus; // Trạng thái giao hàng (ví dụ: "Đang giao", "Đã giao", "Hủy")
    private String deliveryTime; // Thời gian giao hàng dự kiến
    private String deliveryNote; // Ghi chú giao hàng (nếu có)
    private boolean isDeleted = false; // Cờ dùng để đánh dấu giao hàng đã bị xóa hay chưa
    private String deliveryType; // Loại giao hàng (ví dụ: "Giao nhanh", "Giao tiêu chuẩn")
    private String deliveryCost; // Chi phí giao hàng
    private String trackingNumber; // Số theo dõi giao hàng (nếu có)
}
