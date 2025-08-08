package com.maybank.assessment.controller;

import com.maybank.assessment.dto.TransactionRecordDTO;
import com.maybank.assessment.dto.UpdateDescriptionRequest;
import com.maybank.assessment.service.TransactionRecordService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRecordController {

    private final TransactionRecordService service;

    public TransactionRecordController(TransactionRecordService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> search(
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String trxDate,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TransactionRecordDTO> resultPage =
                service.search(accountNumber, trxDate, description, PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("content", resultPage.getContent());
        return response;
    }

    @PutMapping("/{id}")
    public TransactionRecordDTO update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDescriptionRequest request
    ) {
        return service.updateDescription(id, request);
    }
}