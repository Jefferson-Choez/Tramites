package com.example.demo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCedula(String cedula);
    @Query("SELECT u FROM Usuario u WHERE u.email = :search OR u.cedula = :search")
    Optional<Usuario> findByEmailOrCedula(@Param("search") String search);

    @Query("SELECT u FROM Usuario u WHERE u.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    List<Usuario> findByFechaRegistroBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
}

