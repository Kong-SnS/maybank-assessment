package com.maybank.assessment.service;

import com.maybank.assessment.dto.TransactionRecordDTO;
import com.maybank.assessment.dto.UpdateDescriptionRequest;
import com.maybank.assessment.entity.TransactionRecordEntity;
import com.maybank.assessment.exception.ResourceNotFoundException;
import com.maybank.assessment.repository.TransactionRecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionRecordServiceImpl implements TransactionRecordService {

    private final TransactionRecordRepository transactionRecordRepository;

    public Page<TransactionRecordDTO> search(String accountNumber, String customerId, String description, Pageable pageable) {
        Specification<TransactionRecordEntity> spec = (root, query, cb) -> cb.conjunction();
        if (accountNumber != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("accountNumber"), accountNumber));
        }
        if (customerId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("customerId"), customerId));
        }
        if (description != null) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
        }
        return transactionRecordRepository.findAll(spec, pageable)
                .map(this::toDto);
    }

    @Transactional
    public TransactionRecordDTO updateDescription(Long id, UpdateDescriptionRequest request) {
        TransactionRecordEntity record = transactionRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with ID " + id + " not found"));
        record.setDescription(request.getDescription());
        return toDto(transactionRecordRepository.save(record));
    }

    private TransactionRecordDTO toDto(TransactionRecordEntity record) {
        return TransactionRecordDTO.builder()
                .id(record.getId())
                .accountNumber(record.getAccountNumber())
                .trxAmount(record.getTrxAmount())
                .description(record.getDescription())
                .trxDate(record.getTrxDate())
                .trxTime(record.getTrxTime())
                .customerId(record.getCustomerId())
                .build();
    }
}
