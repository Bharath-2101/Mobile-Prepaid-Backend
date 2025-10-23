package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.HistoryResponseDTO;
import com.aits.mobileprepaid.DTOs.RechargeRequestDTO;
import com.aits.mobileprepaid.Entities.RechargeHistory;
import com.aits.mobileprepaid.Entities.RechargePlan;
import com.aits.mobileprepaid.Entities.User;
import com.aits.mobileprepaid.Exceptions.ResourceNotFoundException;
import com.aits.mobileprepaid.Repositories.RechargeHistoryRepository;
import com.aits.mobileprepaid.Repositories.RechargePlanRepository;
import com.aits.mobileprepaid.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RechargeService {

    private final UserRepository userRepository;
    private final RechargePlanRepository rechargePlanRepository;
    private final RechargeHistoryRepository rechargeHistoryRepository;
    private final RechargeHistoryService rechargeHistoryService;
    private final JavaMailSender javaMailSender;

    public HistoryResponseDTO recharge(RechargeRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + dto.getUserId()));

        RechargePlan plan = rechargePlanRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan not found with ID: " + dto.getPlanId()));

        LocalDateTime now = LocalDateTime.now();

        RechargeHistory history = RechargeHistory.builder()
                .user(user)
                .rechargePlan(plan)
                .rechargeDate(now)
                .expirationDate(now.plusDays(plan.getValidity()))
                .paidAmount(plan.getPrice())
                .paymentMethod(dto.getPaymentMethod())
                .paymentDetails(dto.getPaymentDetails())
                .build();

        rechargeHistoryRepository.save(history);

        sendRechargeConfirmation(user.getEmail(),user.getName(),plan.getName(),plan.getPrice(),plan.getValidity());

        return rechargeHistoryService.historyToDTO(history);
    }

    @Async
    public void sendRechargeConfirmation(String toEmail, String userName, String planName, Long amount, Long validity) {
        String subject = "Recharge Successful – MobilePrepaid";
        String body = String.format("""
                Hi %s,

                Your recharge for plan '%s' was successful!
                Amount Paid: ₹%d
                Validity: %s days

                Thank you for using MobilePrepaid!

                Regards,
                MobilePrepaid Team
                """, userName, planName, amount, validity);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("⚠️ Email send failed: " + e.getMessage());
        }
    }



}

