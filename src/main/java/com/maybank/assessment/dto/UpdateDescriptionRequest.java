package com.maybank.assessment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDescriptionRequest {

    @NotBlank(message = "Description must not be blank")
    private String description;
}
