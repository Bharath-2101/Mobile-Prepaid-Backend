package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.PlanRequestDTO;
import com.aits.mobileprepaid.DTOs.PlanResponseDTO;
import com.aits.mobileprepaid.Entities.RechargePlan;
import com.aits.mobileprepaid.Entities.RechargePlan.*;
import com.aits.mobileprepaid.Exceptions.ResourceNotFoundException;
import com.aits.mobileprepaid.Repositories.RechargePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RechargePlanService {

    private final RechargePlanRepository rechargePlanRepository;

    // Adding Plan
    public PlanResponseDTO addPlan(PlanRequestDTO planRequestDTO) {

        RechargePlan rechargePlan = new RechargePlan();
        rechargePlan.setName(planRequestDTO.getName());
        rechargePlan.setDescription(planRequestDTO.getDescription());
        rechargePlan.setPrice(planRequestDTO.getPrice());
        rechargePlan.setValidity(planRequestDTO.getValidity());
        rechargePlan.setCategory(planRequestDTO.getCategory());
        RechargePlan savedPlan = rechargePlanRepository.save(rechargePlan);

        return rechargePlanToPlanResponseDTO(savedPlan);
    }


    // Get all plans
    public List<PlanResponseDTO> getAllPlans(){

        return rechargePlanRepository.findAll().stream()
                                                .map(this::rechargePlanToPlanResponseDTO)
                                                .toList();
    }

    //Get plans by Category
    public List<PlanResponseDTO> getPlansByCategory(Category category){
        return rechargePlanRepository.findByCategory(category).stream()
                                                                .map(this::rechargePlanToPlanResponseDTO)
                                                                .toList();
    }

    //GetPlan by Id
    public PlanResponseDTO getPlanById(Long id){
        return rechargePlanToPlanResponseDTO(
                rechargePlanRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Plan with given id "+id+" does not exists.")));
    }

    //Get Plans Containing the name
    public List<PlanResponseDTO> getPlanByName(String name){
        return rechargePlanRepository.findByNameContainingIgnoreCase(name).stream()
                                                                        .map(this::rechargePlanToPlanResponseDTO)
                                                                        .toList();
    }

    //Update Plan
    public PlanResponseDTO updatePlan(Long id,PlanRequestDTO planRequestDTO){

        RechargePlan unUpdatedPlan=rechargePlanRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Plan with given id "+id+" does not exists."));

        if (planRequestDTO.getName()!=null) unUpdatedPlan.setName(planRequestDTO.getName());
        if (planRequestDTO.getDescription()!=null) unUpdatedPlan.setDescription(planRequestDTO.getDescription());
        if (planRequestDTO.getPrice()!=null) unUpdatedPlan.setPrice(planRequestDTO.getPrice());
        if(planRequestDTO.getValidity()!=null) unUpdatedPlan.setValidity(planRequestDTO.getValidity());
        if (planRequestDTO.getCategory()!=null) unUpdatedPlan.setCategory(planRequestDTO.getCategory());

        return rechargePlanToPlanResponseDTO(rechargePlanRepository.save(unUpdatedPlan));
    }

    //Delete Plan by id
    public String deletePlan(Long id){
        try{
            rechargePlanRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Plan with given id"+id+" does not exists.");
        }
        return "Recharge Plan with id "+id+" is deleted successfully";
    }


    //Util - > from RechargePlan to PlanResponseDTO
    public PlanResponseDTO rechargePlanToPlanResponseDTO(RechargePlan plan){
        return new PlanResponseDTO(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getValidity(),
                plan.getCategory());
    }

}
