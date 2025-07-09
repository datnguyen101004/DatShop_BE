package com.dat.backend.datshop.payment.repository;

import com.dat.backend.datshop.payment.entity.Bill_Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Bill_ItemsRepository extends JpaRepository<Bill_Items, Long> {
    List<Bill_Items> findByBillId(String billId);
}
