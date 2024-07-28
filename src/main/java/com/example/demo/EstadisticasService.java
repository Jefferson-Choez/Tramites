package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class EstadisticasService {

    private final TramiteRepository tramiteRepository;

    @Autowired
    public EstadisticasService(TramiteRepository tramiteRepository) {
        this.tramiteRepository = tramiteRepository;
    }

    public Map<String, Object> obtenerEstadisticas(String filtro, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> estadisticas = new LinkedHashMap<>();

        switch (filtro) {
            case "estado":
                Long totalTramites = tramiteRepository.count();
                Long ingresados = tramiteRepository.countByEstadoAndDateRange("ingresado", startDate, endDate);
                Long enTransicion = tramiteRepository.countByEstadoAndDateRange("en transicion", startDate, endDate);
                Long cerrados = tramiteRepository.countByEstadoAndDateRange("cerrado", startDate, endDate);
                estadisticas.put("Total Trámites", totalTramites);
                estadisticas.put("Ingresados", ingresados);
                estadisticas.put("En Transición", enTransicion);
                estadisticas.put("Cerrados", cerrados);
                break;
            case "tiempo":
                LocalDate fechaLimite = endDate.minusDays(8);
                Long finalizadosATiempo = tramiteRepository.countByFechaFinalizacionLessThanEqual(fechaLimite);
                Long finalizadosFueraDeTiempo = tramiteRepository.countByFechaFinalizacionGreaterThan(fechaLimite);
                estadisticas.put("Finalizados a Tiempo", finalizadosATiempo);
                estadisticas.put("Finalizados Fuera de Tiempo", finalizadosFueraDeTiempo);
                break;
            case "area":
                List<Object[]> duracionPromedioPorArea = tramiteRepository.findAverageDurationByDepartment();
                duracionPromedioPorArea.forEach(result -> estadisticas.put((String) result[0], ((Number) result[1]).doubleValue()));
                break;
        }

        return estadisticas;
    }
}
