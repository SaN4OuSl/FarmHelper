package com.example.farmhelper.repository;

import com.example.farmhelper.entity.Field;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    List<Field> findAllByIsActiveAndFieldNameIsContainingIgnoreCase(Boolean isActive,
                                                                    String fieldName);

    Optional<Field> findByFieldName(String fieldName);
}
