package com.dat.backend.datshop.user.repository;

import com.dat.backend.datshop.user.entity.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Long> {

    void deleteByProductIdAndUserId(Long productId, Long userId);
}
