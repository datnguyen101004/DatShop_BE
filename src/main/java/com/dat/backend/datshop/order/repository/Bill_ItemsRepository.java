package com.dat.backend.datshop.payment.repository;

import com.dat.backend.datshop.payment.entity.BillItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Bill_ItemsRepository extends JpaRepository<BillItems, Long> {
    List<BillItems> findByBillId(String billId);
}
