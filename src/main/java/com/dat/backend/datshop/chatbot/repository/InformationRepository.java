package com.dat.backend.datshop.chatbot.repository;

import com.dat.backend.datshop.chatbot.entity.Information;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationRepository extends JpaRepository<Information, String> {
}
