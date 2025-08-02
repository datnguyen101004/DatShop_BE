package com.dat.backend.datshop.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeliveryRequest {
    private String from_name;
    private String from_phone;
    private String from_address;
    private String from_ward_name;
    private String from_district_name;
    private String from_province_name;
    private String to_name;
    private String to_phone;
    private String to_address;
    private String to_ward_name;
    private String to_district_name;
    private String to_province_name;
    //    private String return_address;
//    private String return_ward_name;
//    private String return_district_name;
//    private String return_province_name;
//    @Schema(
//            description = "Mã đơn hàng của người bán. Mã này sẽ được sử dụng để tra cứu đơn hàng trên hệ thống của người bán. Tạo ra mã này bằng: shop + id đơn hàng",
//            example = "shop_1"
//    )
    private String client_order_code;
    private String content;
//    @Schema(description = "Khối lượng (gram)", example = "30")
    private int weight;
//    @Schema(description = "Chiều dài (cm)", example = "30")
    private int length;
//    @Schema(description = "Chiều rộng (cm)", example = "30")
    private int width;
//    @Schema(description = "Chiều cao (cm)", example = "30")
    private int height;
    private String coupon;
//    @Schema(
//            description = "Mã loại dịch vụ cố định. Trong đó:  2: Hàng nhẹ, 5: Hàng nặng. Hàng nhẹ sử dụng length, width, height và weight. Hàng nặng sử dụng items[].length, items[].width, items[].height và items[].weight",
//            example = "2"
//    )
    private int service_type_id;
//    @Schema(
//            description = "Mã người thanh toán phí dịch vụ. Trong đó: 1: Người bán/Người gửi, 2: Người mua/Người nhận.",
//            example = "2"
//    )
    private int payment_type_id;
    private String note;
//    @Schema(
//            description = "Ghi chú bắt buộc, Bao gồm: CHOTHUHANG, CHOXEMHANGKHONGTHU, KHONGCHOXEMHANG\n" +
//                    "\n" +
//                    "CHOTHUHANG nghĩa là Người mua có thể yêu cầu xem và dùng thử hàng hóa\n" +
//                    "\n" +
//                    "CHOXEMHANGKHONGTHU nghĩa là Người mua được xem hàng nhưng không được dùng thử hàng\n" +
//                    "\n" +
//                    "KHONGCHOXEMHANG nghĩa là Người mua không được phép xem hàng",
//            example = "CHOXEMHANGKHONGTHU"
//    )
    private String required_note;
    private List<GhnItem> items;
}
