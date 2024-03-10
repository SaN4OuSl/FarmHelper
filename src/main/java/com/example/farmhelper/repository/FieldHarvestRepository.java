package com.example.farmhelper.repository;

import com.example.farmhelper.entity.FieldHarvest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldHarvestRepository extends JpaRepository<FieldHarvest, Long> {

    List<FieldHarvest> findAllByHarvestId(Long harvestId);
}
