package com.example.Driview.domain.sample.repository;

import com.example.Driview.domain.sample.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<SampleEntity, Long> {
}