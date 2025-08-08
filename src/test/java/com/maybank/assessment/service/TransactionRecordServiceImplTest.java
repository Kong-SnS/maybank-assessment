package com.maybank.assessment.service;

import com.maybank.assessment.dto.TransactionRecordDTO;
import com.maybank.assessment.dto.UpdateDescriptionRequest;
import com.maybank.assessment.entity.TransactionRecordEntity;
import com.maybank.assessment.repository.TransactionRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionRecordServiceImplTest {

    @Mock
    private TransactionRecordRepository repository;

    @InjectMocks
    private TransactionRecordServiceImpl service;

    @Test
    void testSearchWithDescriptionOnly() {
        Pageable pageable = PageRequest.of(0, 10);

        TransactionRecordEntity record = TransactionRecordEntity.builder()
                .id(1L)
                .accountNumber("12345")
                .trxAmount(new BigDecimal("100.00"))
                .description("FUND TRANSFER")
                .trxDate(LocalDate.of(2023, 1, 1))
                .trxTime(LocalTime.of(12, 0))
                .customerId("222")
                .build();

        Page<TransactionRecordEntity> page = new PageImpl<>(List.of(record));

        // <-- stub the Specification overload, not the Example overload
        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        Page<TransactionRecordDTO> result = service.search(null, null, "transfer", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("FUND TRANSFER", result.getContent().get(0).getDescription());
    }

    @Test
    void testUpdateDescription() {
        TransactionRecordEntity record = TransactionRecordEntity.builder()
                .id(1L)
                .accountNumber("12345")
                .trxAmount(new BigDecimal("100.00"))
                .description("OLD DESC")
                .trxDate(LocalDate.of(2023, 1, 1))
                .trxTime(LocalTime.of(12, 0))
                .customerId("222")
                .version(0)
                .build();

        UpdateDescriptionRequest request = new UpdateDescriptionRequest();
        request.setDescription("NEW DESC");

        when(repository.findById(1L)).thenReturn(Optional.of(record));
        when(repository.save(any(TransactionRecordEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TransactionRecordDTO updated = service.updateDescription(1L, request);

        assertEquals("NEW DESC", updated.getDescription());
    }
}