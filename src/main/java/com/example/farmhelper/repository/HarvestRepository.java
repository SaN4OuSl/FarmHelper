package com.example.farmhelper.repository;

import com.example.farmhelper.entity.Crop;
import com.example.farmhelper.entity.Harvest;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HarvestRepository extends JpaRepository<Harvest, Long> {

    List<Harvest> findAllByCropId(Long cropId);

    Boolean existsByCrop_Id(Long cropId);

    Optional<Harvest> findByCropAndMonthYearOfCollection(Crop crop,
                                                           String monthAndYearOfCollection);

    @Transactional
    @Modifying
    @Query("DELETE FROM Harvest h WHERE h.id = :id")
    void deleteHarvestById(@Param("id") Long id);
}
