package com.example.farmhelper.repository;

import com.example.farmhelper.entity.Crop;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findAllByNameContainingIgnoreCase(String search);

    Optional<Crop> findByName(String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM Crop c WHERE c.id = :id")
    void deleteCropById(@Param("id") Long id);
}
