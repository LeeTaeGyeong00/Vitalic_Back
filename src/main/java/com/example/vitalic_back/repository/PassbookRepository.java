package com.example.vitalic_back.repository;

import com.example.vitalic_back.entity.EnterPassbook;
import com.example.vitalic_back.entity.Passbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassbookRepository extends JpaRepository<Passbook, Long> {
}
