package com.dat.backend.datshop.order.repository;

import com.dat.backend.datshop.order.entity.Order;
import com.dat.backend.datshop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long id);

    @Query(
            value = "select * from orders o where o.shop_id = ?1 order by o.id desc",
            nativeQuery = true
    )
    List<Order> findAllByShopId(Long id);
}
