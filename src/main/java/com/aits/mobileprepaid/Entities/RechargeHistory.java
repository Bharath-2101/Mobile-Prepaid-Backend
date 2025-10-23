package com.aits.mobileprepaid.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RechargeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Recharge date cannot be null")
    private LocalDateTime rechargeDate;

    @NotNull(message = "Expiration date cannot be null")
    private LocalDateTime expirationDate;

    @NotNull(message = "Paid amount cannot be null")
    @Positive(message = "Paid amount must be a positive value")
    private Long paidAmount;

    @NotNull(message = "Payment method cannot be null")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Payment details cannot be blank")
    @Size(max = 100, message = "Payment details must not exceed 100 characters")
    private String paymentDetails;

    public RechargeHistory(Long id, LocalDateTime rechargeDate, LocalDateTime expirationDate,
                           Long paidAmount, PaymentMethod paymentMethod, String paymentDetails) {
        this.id = id;
        this.rechargeDate = rechargeDate;
        this.expirationDate = expirationDate;
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.paymentDetails = paymentDetails;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    @NotNull(message = "Recharge plan cannot be null")
    private RechargePlan rechargePlan;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;

    public enum PaymentMethod {
        UPI, CREDIT, DEBIT
    }
}
