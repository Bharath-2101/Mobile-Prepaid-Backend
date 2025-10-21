package com.aits.mobileprepaid.Repositories;

import com.aits.mobileprepaid.Entities.RechargePlan;
import com.aits.mobileprepaid.Entities.RechargePlan.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechargePlanRepository extends JpaRepository<RechargePlan, Long> {

    List<RechargePlan> findByCategory(Category category);

    List<RechargePlan> findByNameContainingIgnoreCase(String name);


}
