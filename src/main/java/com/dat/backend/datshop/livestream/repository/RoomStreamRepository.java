package com.dat.backend.datshop.livestream.repository;

import com.dat.backend.datshop.livestream.entity.RoomStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomStreamRepository extends JpaRepository<RoomStream, Long> {
}
