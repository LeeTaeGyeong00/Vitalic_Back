package com.example.vitalic_back.repository;

import com.example.vitalic_back.entity.EnterPassbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnterPassbookRepository extends JpaRepository<EnterPassbook, Long> {
}
