package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.PlanRequestDTO;
import com.aits.mobileprepaid.DTOs.PlanResponseDTO;
import com.aits.mobileprepaid.Entities.RechargePlan;
import com.aits.mobileprepaid.Entities.RechargePlan.*;
import com.aits.mobileprepaid.Exceptions.ResourceNotFoundException;
import com.aits.mobileprepaid.Repositories.RechargePlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RechargePlanServiceTest {

    @Mock
    RechargePlanRepository rechargePlanRepository;

    @InjectMocks
    private RechargePlanService rechargePlanService;

    RechargePlan testPlan1,testPlan2;
    PlanRequestDTO testPlanRequestDTO;

    @BeforeEach
    void setUp() {
        testPlan1 = new RechargePlan(1L,"Plan1", 100L,28L,"Description of Plan1",Category.UNLIMITED);
        testPlan2 = new RechargePlan(2L,"Plan2",200L,54L,"Description of Plan2",Category.POPULAR);

        testPlanRequestDTO=new PlanRequestDTO(
                testPlan1.getName(),
                testPlan1.getPrice(),
                testPlan1.getValidity(),
                testPlan1.getDescription(),
                testPlan1.getCategory()
        );
    }

    //Positive Test Case: Adding the plan into Database.
    @Test
    void itShouldTakePlanRequestDTOAndGivePlanResponseDTO() {
        //Mock
        when(rechargePlanRepository.save(any(RechargePlan.class))).thenReturn(testPlan1);
        //Act
        PlanResponseDTO expectedResult=rechargePlanService.addPlan(testPlanRequestDTO);
        //Assert
        assertNotNull(expectedResult);
        assertEquals(expectedResult.getId(),testPlan1.getId());
        assertEquals(expectedResult.getName(),testPlan1.getName());
        assertEquals(expectedResult.getPrice(),testPlan1.getPrice());
        assertEquals(expectedResult.getValidity(),testPlan1.getValidity());
        assertEquals(expectedResult.getDescription(),testPlan1.getDescription());
        assertEquals(expectedResult.getCategory(),testPlan1.getCategory());
    }

    //Positive Test Case: It has get the all plans.
    @Test
    void itShouldReturnAllPlans() {
        // Mock
        List<RechargePlan> planList = Arrays.asList(testPlan1, testPlan2);
        when(rechargePlanRepository.findAll()).thenReturn(planList);

        // Act
        List<PlanResponseDTO> actualList = rechargePlanService.getAllPlans();

        // Assert
        assertNotNull(actualList);
        assertEquals(2, actualList.size());

        List<PlanResponseDTO> expectedList = planList.stream()
                .map(rechargePlanService::rechargePlanToPlanResponseDTO)
                .toList();

        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i).getId(), actualList.get(i).getId());
            assertEquals(expectedList.get(i).getName(), actualList.get(i).getName());
            assertEquals(expectedList.get(i).getPrice(), actualList.get(i).getPrice());
            assertEquals(expectedList.get(i).getValidity(), actualList.get(i).getValidity());
            assertEquals(expectedList.get(i).getDescription(), actualList.get(i).getDescription());
            assertEquals(expectedList.get(i).getCategory(), actualList.get(i).getCategory());
        }
    }

    //Positive Test Case: It has get all plans with given category.
    @Test
    void itShouldReturnAllPlansWithCategory() {
        //Given
        Category category = Category.UNLIMITED;
        //Mock
        List<RechargePlan> planList = Collections.singletonList(testPlan1);
        when(rechargePlanRepository.findByCategory(category)).thenReturn(planList);
        //Act
        List<PlanResponseDTO> actualList= rechargePlanService.getPlansByCategory(category);
        //Assert
        assertNotNull(actualList);
        assertEquals(1, actualList.size());

        List<PlanResponseDTO> expectedList = planList.stream()
                .map(rechargePlanService::rechargePlanToPlanResponseDTO)
                .toList();

        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i).getId(), actualList.get(i).getId());
            assertEquals(expectedList.get(i).getName(), actualList.get(i).getName());
            assertEquals(expectedList.get(i).getPrice(), actualList.get(i).getPrice());
            assertEquals(expectedList.get(i).getValidity(), actualList.get(i).getValidity());
            assertEquals(expectedList.get(i).getDescription(), actualList.get(i).getDescription());
            assertEquals(expectedList.get(i).getCategory(), actualList.get(i).getCategory());
        }

    }

    //Positive Test Case: It has to give  plan By id.
    @Test
    void itShouldHaveGiveRechargePlanWithGivenId()
    {
        //Given
        long id = 1L;
        //Mock
        when(rechargePlanRepository.findById(id)).thenReturn(Optional.of(testPlan1));
        //Act
        PlanResponseDTO actualResult=rechargePlanService.getPlanById(id);
        //Assert
        assertNotNull(actualResult);
        assertEquals(testPlan1.getId(), actualResult.getId());
        assertEquals(testPlan1.getName(), actualResult.getName());
        assertEquals(testPlan1.getPrice(), actualResult.getPrice());
        assertEquals(testPlan1.getValidity(), actualResult.getValidity());
        assertEquals(testPlan1.getDescription(), actualResult.getDescription());
        assertEquals(testPlan1.getCategory(), actualResult.getCategory());
    }

    //Negative Test Case:It has to throw exception if plan does not exist with given id.
    @Test
    void itShouldThrowExceptionIfPlanWithGivenIdDoesNotExist()
    {
        //Given
        long id = 1L;
        //Mock
        when(rechargePlanRepository.findById(id)).thenReturn(Optional.empty());
        //Assert
        assertThrows(ResourceNotFoundException.class, () -> rechargePlanService.getPlanById(id));
    }

    //Positive Test Case: It has to give the recharge plans with given name containing.
    @Test
    void itShouldGivePlansWithGivenNameContainingInIt()
    {
        //Given
        String name="lan";
        List<RechargePlan> plansList = Arrays.asList(testPlan1, testPlan2);
        //Mock
        when(rechargePlanRepository.findByNameContainingIgnoreCase(name)).thenReturn(plansList);
        //Act
        List<PlanResponseDTO> actualList=rechargePlanService.getPlanByName(name);
        //Assert
        assertNotNull(actualList);
        assertEquals(2, actualList.size());
        List<PlanResponseDTO> expectedList = plansList.stream()
                .map(rechargePlanService::rechargePlanToPlanResponseDTO)
                .toList();

        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i).getId(), actualList.get(i).getId());
            assertEquals(expectedList.get(i).getName(), actualList.get(i).getName());
            assertEquals(expectedList.get(i).getPrice(), actualList.get(i).getPrice());
            assertEquals(expectedList.get(i).getValidity(), actualList.get(i).getValidity());
            assertEquals(expectedList.get(i).getDescription(), actualList.get(i).getDescription());
            assertEquals(expectedList.get(i).getCategory(), actualList.get(i).getCategory());
        }
    }

    //Positive Test Case:Update the Recharge Plan with PlanRequestDTO ignoring null if given id exist.
    @Test
    void itShouldHaveUpdatePlanWithGivenUpdatedPlanIfIdExists()
    {
        //Given
        long id = 2L;
        //Mock
        when(rechargePlanRepository.findById(id)).thenReturn(Optional.of(testPlan2));
        when(rechargePlanRepository.save(any(RechargePlan.class))).thenAnswer(i -> i.getArguments()[0]);
        //Act
        PlanResponseDTO actualResult=rechargePlanService.updatePlan(id,testPlanRequestDTO);
        PlanResponseDTO expectedResult=new PlanResponseDTO(id,
                                                            testPlan1.getName(),
                                                            testPlan1.getDescription(),
                                                            testPlan1.getPrice(),
                                                            testPlan1.getValidity(),
                                                            testPlan1.getCategory());
        //Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult.getId(),actualResult.getId());
        assertEquals(expectedResult.getName(),actualResult.getName());
        assertEquals(expectedResult.getPrice(),actualResult.getPrice());
        assertEquals(expectedResult.getValidity(),actualResult.getValidity());
        assertEquals(expectedResult.getDescription(),actualResult.getDescription());
        assertEquals(expectedResult.getCategory(),actualResult.getCategory());
    }

    //Negative Test Case: It has to throw exception if Plan does not exist with given id.
    @Test
    void itShouldThrowExceptionIfUpdatePlanWithGivenIdDoesNotExist()
    {
        //Given
        long id = 2L;
        //Mock
        when(rechargePlanRepository.findById(id)).thenReturn(Optional.empty());
        //Assert
        assertThrows(ResourceNotFoundException.class, () -> rechargePlanService.updatePlan(id,testPlanRequestDTO));
    }

    //Positive Test Case: It has to delete the plan if plan exist with given id.
    @Test
    void itShouldDeletePlanWithGivenId() {
        long id = 1L;
        //Mock
        doNothing().when(rechargePlanRepository).deleteById(id);
        // Act
        String actualResult = rechargePlanService.deletePlan(id);
        // Assert
        assertEquals("Recharge Plan with id " + id + " is deleted successfully", actualResult);
    }

    // Negative Test Case: Should throw exception if plan with given id does not exist
    @Test
    void itShouldThrowExceptionWhenPlanIdDoesNotExist() {
        long id = 100L;
        //Mock
        doThrow(new EmptyResultDataAccessException(1)).when(rechargePlanRepository).deleteById(id);
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> rechargePlanService.deletePlan(id)
        );

        assertEquals("Plan with given id" + id + " does not exists.", exception.getMessage());
    }



}