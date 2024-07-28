package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/Estadisticas")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    @Autowired
    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @GetMapping
    public String mostrarFormulario(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "Estadisticas";
    }

    @GetMapping("/datos")
    @ResponseBody
    public Map<String, Object> obtenerEstadisticas(
            @RequestParam("filtro") String filtro,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return estadisticasService.obtenerEstadisticas(filtro, startDate, endDate);
    }
}
