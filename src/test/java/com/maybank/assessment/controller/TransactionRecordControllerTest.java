package com.maybank.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maybank.assessment.dto.TransactionRecordDTO;
import com.maybank.assessment.dto.UpdateDescriptionRequest;
import com.maybank.assessment.exception.GlobalExceptionHandler;
import com.maybank.assessment.exception.ResourceNotFoundException;
import com.maybank.assessment.service.TransactionRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionRecordControllerTest {

    @Mock
    private TransactionRecordService service;

    @InjectMocks
    private TransactionRecordController controller;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testSearchEndpoint() throws Exception {
        TransactionRecordDTO dto = TransactionRecordDTO.builder()
                .id(1L)
                .accountNumber("12345")
                .trxAmount(new BigDecimal("100.00"))
                .description("FUND TRANSFER")
                .trxDate(LocalDate.of(2023, 1, 1))
                .trxTime(LocalTime.of(12, 0))
                .customerId("222")
                .build();

        Page<TransactionRecordDTO> page = new PageImpl<>(List.of(dto));
        when(service.search(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/transactions")
                        .param("description", "transfer")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("FUND TRANSFER"));
    }

    @Test
    void testUpdateEndpoint() throws Exception {
        TransactionRecordDTO dto = TransactionRecordDTO.builder()
                .id(1L)
                .accountNumber("12345")
                .trxAmount(new BigDecimal("100.00"))
                .description("NEW DESC")
                .trxDate(LocalDate.of(2023, 1, 1))
                .trxTime(LocalTime.of(12, 0))
                .customerId("222")
                .build();

        UpdateDescriptionRequest request = new UpdateDescriptionRequest();
        request.setDescription("NEW DESC");

        when(service.updateDescription(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("NEW DESC"));
    }

    @Test
    void testUpdate_NotFound() throws Exception {
        Long invalidId = 999L;
        UpdateDescriptionRequest request = new UpdateDescriptionRequest();
        request.setDescription("Anything");

        when(service.updateDescription(eq(invalidId), any()))
                .thenThrow(new ResourceNotFoundException("Transaction with ID " + invalidId + " not found"));

        mockMvc.perform(put("/api/transactions/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Transaction with ID " + invalidId + " not found"))
                .andExpect(jsonPath("$.path").value("/api/transactions/" + invalidId));
    }

    @Test
    void testUpdate_OptimisticLockingFailure() throws Exception {
        Long id = 1L;
        UpdateDescriptionRequest request = new UpdateDescriptionRequest();
        request.setDescription("Conflict");

        when(service.updateDescription(eq(id), any()))
                .thenThrow(new OptimisticLockingFailureException("Version mismatch"));

        mockMvc.perform(put("/api/transactions/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Version mismatch"))
                .andExpect(jsonPath("$.path").value("/api/transactions/" + id));
    }

    @Test
    void testUpdate_InvalidInput() throws Exception {
        UpdateDescriptionRequest request = new UpdateDescriptionRequest();
        request.setDescription(""); // invalid: blank

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/transactions/1"));
    }
}