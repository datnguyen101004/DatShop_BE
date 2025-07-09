package com.dat.backend.datshop.payment.repository;

import com.dat.backend.datshop.payment.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, String> {
    List<Bill> findByUserId(Long id);
}
