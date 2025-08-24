package com.dat.backend.datshop.product.repository;

import com.dat.backend.datshop.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(
            value = "select distinct category from product p",
            nativeQuery = true
    )
    List<String> findDistinctCategories();

    @Query(
            value = "select * from product p order by id desc limit 4",
            nativeQuery = true
    )
    List<Product> findTop4ByOrderByIdDesc();
}
