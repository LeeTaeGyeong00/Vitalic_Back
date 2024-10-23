package com.example.vitalic_back.service;

import org.springframework.stereotype.Service;
import com.example.vitalic_back.entity.EnterPassbook;
import com.example.vitalic_back.repository.EnterPassbookRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EnterPassbookService {
    @Autowired
    private EnterPassbookRepository enterPassbookRepository;

    public EnterPassbook save(EnterPassbook enterPassbook) {
        return enterPassbookRepository.save(enterPassbook);
    }
}
