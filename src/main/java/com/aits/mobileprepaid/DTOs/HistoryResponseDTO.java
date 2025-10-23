package com.aits.mobileprepaid.DTOs;

import com.aits.mobileprepaid.Entities.RechargeHistory.*;
import com.aits.mobileprepaid.Entities.RechargePlan.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HistoryResponseDTO {

    private Long id;
    private LocalDateTime rechargeDate;
    private LocalDateTime expirationDate;
    private Long paidAmount;
    private PaymentMethod paymentMethod;
    private String paymentDetails;
    private Long userId;
    private String username;
    private Category category;

}
