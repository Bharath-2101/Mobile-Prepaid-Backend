package com.aits.mobileprepaid.DTOs;

import com.aits.mobileprepaid.Entities.RechargePlan.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanRequestDTO {

    @NotBlank(message = "Plan name is required.")
    @Size(max = 50, message = "Plan name must not exceed 50 characters.")
    private String name;

    @NotNull(message = "Price is required.")
    @Min(value = 1, message = "Price must be greater than 0.")
    private Long price;

    @NotNull(message = "Validity is required.")
    @Min(value = 1, message = "Validity must be at least 1 day.")
    private Long validity;

    @Size(max = 255, message = "Description must not exceed 255 characters.")
    private String description;

    @NotNull(message = "Category is required.")
    @Enumerated(EnumType.STRING)
    private Category category;



}
