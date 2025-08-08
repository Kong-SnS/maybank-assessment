package com.maybank.assessment.service;

import com.maybank.assessment.dto.TransactionRecordDTO;
import com.maybank.assessment.dto.UpdateDescriptionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionRecordService {
    Page<TransactionRecordDTO> search(String accountNumber, String customerId, String description, Pageable pageable);
    TransactionRecordDTO updateDescription(Long id, UpdateDescriptionRequest request);
}
