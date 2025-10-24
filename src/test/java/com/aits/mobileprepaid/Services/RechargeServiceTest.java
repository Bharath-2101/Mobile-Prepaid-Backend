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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RechargeServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RechargePlanRepository rechargePlanRepository;
    @Mock
    private RechargeHistoryRepository rechargeHistoryRepository;
    @Mock
    private RechargeHistoryService rechargeHistoryService;
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private RechargeService rechargeService;

    private User user;
    private RechargePlan plan;
    private RechargeRequestDTO requestDTO;
    private RechargeHistory history;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        plan = new RechargePlan();
        plan.setId(100L);
        plan.setName("Unlimited Pack");
        plan.setPrice(199L);
        plan.setValidity(28L);

        requestDTO = new RechargeRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setPlanId(100L);
        requestDTO.setPaymentMethod(RechargeHistory.PaymentMethod.CREDIT);
        requestDTO.setPaymentDetails("4111 1111 1111 1111");

        history = new RechargeHistory();
        history.setUser(user);
        history.setRechargePlan(plan);
        history.setPaidAmount(plan.getPrice());
        history.setPaymentMethod(requestDTO.getPaymentMethod());
        history.setPaymentDetails("XXXX XXXX XXXX 1111");
    }

    //Positive Test Case: Recharge Successful.
    @Test
    void testRecharge_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rechargePlanRepository.findById(100L)).thenReturn(Optional.of(plan));
        when(rechargeHistoryRepository.save(any(RechargeHistory.class))).thenReturn(history);
        when(rechargeHistoryService.historyToDTO(any(RechargeHistory.class)))
                .thenReturn(new HistoryResponseDTO());

        HistoryResponseDTO response = rechargeService.recharge(requestDTO);

        assertNotNull(response);
        verify(userRepository, times(1)).findById(1L);
        verify(rechargePlanRepository, times(1)).findById(100L);
        verify(rechargeHistoryRepository, times(1)).save(any(RechargeHistory.class));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

   //Negative Test Case: Throws Exception as User with given is not found.
    @Test
    void testRecharge_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> rechargeService.recharge(requestDTO));

        assertTrue(ex.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findById(1L);
        verify(rechargePlanRepository, never()).findById(any());
    }

    //Negative Test Case:Throws Exception as Plan with given is not found.
    @Test
    void testRecharge_PlanNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rechargePlanRepository.findById(100L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> rechargeService.recharge(requestDTO));

        assertTrue(ex.getMessage().contains("Plan not found"));
        verify(rechargeHistoryRepository, never()).save(any());
    }

    //Positive Test Case: It has to send the Email to User successful.
    @Test
    void testSendRechargeConfirmation_Success() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        rechargeService.sendRechargeConfirmation("john@example.com", "John Doe",
                "Unlimited Pack", 199L, 28L);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

   //Negative Test Case: It has throw Exception as the Mail sender is down or any other exception.
    @Test
    void testSendRechargeConfirmation_FailureHandledGracefully() {
        doThrow(new RuntimeException("Mail server down"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> rechargeService.sendRechargeConfirmation(
                "john@example.com", "John Doe", "Unlimited Pack", 199L, 28L));

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    //Possibility Test Cases for Method.
    //Positive Test Case: It has to detect the upi suffix.
    @Test
    void testMaskPaymentDetails_UPI() {
        String masked = rechargeService.maskPaymentDetails("bharath@okicici");
        assertTrue(masked.startsWith("b"));
        assertTrue(masked.contains("@okicici"));
    }

    //Neutral Test Case: It has to given same input as output if email is given as input.
    @Test
    void testMaskPaymentDetails_Email() {
        String email = "test@example.com";
        assertEquals(email, rechargeService.maskPaymentDetails(email));
    }

    //Positive Test Case: It has to mask the card number with X except last 4 chars.
    @Test
    void testMaskPaymentDetails_CardNumber() {
        String card = "4111 1111 1111 1111";
        String masked = rechargeService.maskPaymentDetails(card);
        assertTrue(masked.endsWith("1111"));
        assertTrue(masked.contains("X"));
    }

    //Neutral Test Case: It has to give the same input as output if mobile number is given input.
    @Test
    void testMaskPaymentDetails_Mobile() {
        String mobile = "9876543210";
        assertEquals(mobile, rechargeService.maskPaymentDetails(mobile)); // unchanged
    }

    //Neutral Test Case: It has to give nothing when it takes nothing as input.
    @Test
    void testMaskPaymentDetails_NullAndEmpty() {
        assertNull(rechargeService.maskPaymentDetails(null));
        assertEquals("", rechargeService.maskPaymentDetails(""));
    }
}
