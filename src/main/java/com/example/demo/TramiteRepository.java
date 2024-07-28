package com.example.demo;

import io.micrometer.common.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TramiteRepository extends JpaRepository<Tramite, Long> {
    List<Tramite> findByUsuarioEmail(String email);
    Page<Tramite> findByEstado(String estado, Pageable pageable);

    @Query("SELECT t FROM Tramite t WHERE t.usuario.cedula = :cedula")
    Page<Tramite> findByUsuarioCedula(@Param("cedula") String cedula, Pageable pageable);

    @NonNull
    @Override
    List<Tramite> findAll();

    @Query("SELECT t FROM Tramite t WHERE t.estado = :estado")
    List<Tramite> findByEstado(@Param("estado") String estado);

    @Query("SELECT t FROM Tramite t WHERE t.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<Tramite> findByFechaCreacionBetween(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT t FROM Tramite t WHERE LOWER(t.departamento) = LOWER(:departamento)")
    Page<Tramite> findByDepartamento(@Param("departamento") String departamento, Pageable pageable);

    @Query("SELECT t FROM Tramite t LEFT JOIN FETCH t.eventos WHERE t.id = :id")
    Optional<Tramite> findByIdWithEventos(@Param("id") Long id);

    @Query("SELECT COUNT(t) FROM Tramite t WHERE t.estado = :estado AND t.fechaCreacion BETWEEN :startDate AND :endDate")
    Long countByEstadoAndDateRange(@Param("estado") String estado,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(t) FROM Tramite t WHERE t.fechaFinalizacion <= :fechaLimite")
    Long countByFechaFinalizacionLessThanEqual(@Param("fechaLimite") LocalDate fechaLimite);

    @Query("SELECT COUNT(t) FROM Tramite t WHERE t.fechaFinalizacion > :fechaLimite")
    Long countByFechaFinalizacionGreaterThan(@Param("fechaLimite") LocalDate fechaLimite);

    @Query("SELECT t.departamento, AVG(t.duracion) FROM Tramite t GROUP BY t.departamento")
    List<Object[]> findAverageDurationByDepartment();
}

