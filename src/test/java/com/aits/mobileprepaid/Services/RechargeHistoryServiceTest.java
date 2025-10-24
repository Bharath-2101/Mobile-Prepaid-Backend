package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.HistoryResponseDTO;
import com.aits.mobileprepaid.Entities.RechargeHistory;
import com.aits.mobileprepaid.Entities.RechargePlan;
import com.aits.mobileprepaid.Entities.User;
import com.aits.mobileprepaid.Repositories.RechargeHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RechargeHistoryServiceTest {

    @Mock
    private RechargeHistoryRepository rechargeHistoryRepository;

    @InjectMocks
    private RechargeHistoryService rechargeHistoryService;

    private RechargeHistory history;
    private User user;
    private RechargePlan plan;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");

        plan = new RechargePlan();
        plan.setCategory(RechargePlan.Category.UNLIMITED);

        history = new RechargeHistory();
        history.setId(1001L);
        history.setRechargeDate(LocalDateTime.now().minusDays(1));
        history.setExpirationDate(LocalDateTime.now().plusDays(5));
        history.setPaidAmount(199L);
        history.setPaymentMethod(RechargeHistory.PaymentMethod.CREDIT);
        history.setPaymentDetails("4111 1111 1111 1111");
        history.setUser(user);
        history.setRechargePlan(plan);
    }

    //Positive Test Case: Get all recharge histories.
    @Test
    void testGetAllRechargeHistories() {
        when(rechargeHistoryRepository.findAll()).thenReturn(List.of(history));

        List<HistoryResponseDTO> result = rechargeHistoryService.getAllRechargeHistories();

        assertEquals(1, result.size());
        HistoryResponseDTO dto = result.get(0);
        assertEquals(history.getId(), dto.getId());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals("John Doe", dto.getUsername());
        assertEquals(RechargePlan.Category.UNLIMITED, dto.getCategory());
        assertEquals(RechargeHistory.PaymentMethod.CREDIT, dto.getPaymentMethod());
        verify(rechargeHistoryRepository, times(1)).findAll();
    }

    //Positive Test Case : Get all recharge histories by using User id.
    @Test
    void testGetAllRechargeHistoriesByUserId() {
        when(rechargeHistoryRepository.findByUserId(1L)).thenReturn(List.of(history));

        List<HistoryResponseDTO> result = rechargeHistoryService.getAllRechargeHistoriesByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(199L, result.get(0).getPaidAmount());
        verify(rechargeHistoryRepository, times(1)).findByUserId(1L);
    }

    //Positive Test Case : Get all recharge histories which are going to expire in three days.
    @Test
    void testGetAllRechargeHistoriesExpiresInThreeDays() {
        LocalDateTime now = LocalDateTime.now();
        when(rechargeHistoryRepository.findByExpirationDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(history));

        List<HistoryResponseDTO> result = rechargeHistoryService.getAllRechargeHistoriesExpiresInThreeDays();

        assertFalse(result.isEmpty());
        assertEquals(history.getId(), result.get(0).getId());
        verify(rechargeHistoryRepository, times(1))
                .findByExpirationDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    //Positive Test Case : It has to give HistoryResponseDTO from RechargeHistory.
    @Test
    void testHistoryToDTOConversion() {
        HistoryResponseDTO dto = rechargeHistoryService.historyToDTO(history);

        assertNotNull(dto);
        assertEquals(history.getId(), dto.getId());
        assertEquals(history.getUser().getName(), dto.getUsername());
        assertEquals(history.getPaymentMethod(), dto.getPaymentMethod());
        assertEquals(history.getRechargePlan().getCategory(), dto.getCategory());
        assertEquals(history.getPaidAmount(), dto.getPaidAmount());
    }

    //Neutral Test Case: Gives Empty List if no recharge histories exist.
    @Test
    void testEmptyListHandling() {
        when(rechargeHistoryRepository.findAll()).thenReturn(List.of());

        List<HistoryResponseDTO> result = rechargeHistoryService.getAllRechargeHistories();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rechargeHistoryRepository, times(1)).findAll();
    }
}
