package com.dat.backend.datshop.vote.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "vote")
public class Vote {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // ID của người dùng đã bình chọn
    @Column(name = "target_id")
    private Long targetId; // ID của đối tượng được bình chọn (có thể là sản phẩm, bài viết, v.v.)

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type")
    private VoteType voteType; // Chọn kiểu bình chọn (ví dụ : sản phẩm, bài viết, v.v.)

    @Column(name = "vote_value")
    private int voteValue; // Giá trị bình chọn (1 -> 5 sao)

    @Column(name = "comment")
    private String comment; // Bình luận kèm theo bình chọn (nếu có)

    private boolean isDeleted = false; // Cờ dùng để đánh dấu bình chọn đã bị xóa hay chưa
}
