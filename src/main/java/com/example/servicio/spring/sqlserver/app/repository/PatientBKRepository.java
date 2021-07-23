package com.example.servicio.spring.sqlserver.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.servicio.spring.sqlserver.app.entity.Patients;

@Repository
public interface PatientBKRepository extends JpaRepository<Patients, Long>{

}
