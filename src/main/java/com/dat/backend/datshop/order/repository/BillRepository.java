package com.dat.backend.datshop.order.repository;

import com.dat.backend.datshop.order.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {
    Optional<Bill> findByOrderId(Long id);
}
