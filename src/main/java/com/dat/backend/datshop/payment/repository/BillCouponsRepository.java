package com.dat.backend.datshop.payment.repository;

import com.dat.backend.datshop.payment.entity.BillCoupons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillCouponsRepository extends JpaRepository<BillCoupons, Long> {
    List<BillCoupons> findByBillId(String billId);
}
