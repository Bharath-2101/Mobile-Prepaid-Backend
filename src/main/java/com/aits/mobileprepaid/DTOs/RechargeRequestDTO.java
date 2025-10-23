package com.aits.mobileprepaid.DTOs;

import com.aits.mobileprepaid.Entities.RechargeHistory.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RechargeRequestDTO {

    @NotNull(message = "User Id cannot be null.")
    private Long userId;

    @NotNull(message = "Plan Id cannot be null.")
    private Long planId;

    @NotNull(message = "Payment Method cannot be null.")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Payment details cannot be blank")
    @Size(max = 100, message = "Payment details must not exceed 100 characters")
    private String paymentDetails;
}
