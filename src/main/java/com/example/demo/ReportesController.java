package com.example.demo;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ReportesController {

    private final ReporteService reporteService;

    @Autowired
    public ReportesController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/Reportes")
    public String mostrarReportes(Model model, Principal principal) {
        model.addAttribute("tramite", new Tramite());
        model.addAttribute("username", principal.getName());
        return "Reportes";
    }

    @PostMapping("/Reportes/usuarios")
    public ResponseEntity<InputStreamResource> generarReporteUsuarios(@RequestParam(value = "tipoReporteUsuarios") String tipoReporteUsuarios,
                                                                      @RequestParam(value = "fechaInicioUsuarios", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicioUsuarios,
                                                                      @RequestParam(value = "fechaFinUsuarios", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFinUsuarios,
                                                                      @RequestParam("formato") String formato) {
        List<Usuario> usuarios;
        if ("Por Rango de Fechas".equals(tipoReporteUsuarios) && fechaInicioUsuarios != null && fechaFinUsuarios != null) {
            usuarios = reporteService.getUsuariosByFechaRegistro(fechaInicioUsuarios, fechaFinUsuarios);
        } else {
            usuarios = reporteService.getUsuarios();
        }
        ByteArrayInputStream bais = reporteService.generateUsuariosReport(usuarios, formato);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=usuarios_report." + formato);
        MediaType mediaType = getMediaTypeForFormat(formato);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(mediaType)
                .body(new InputStreamResource(bais));
    }

    @PostMapping("/Reportes/tramites")
    public ResponseEntity<byte[]> generarReporteTramites(@RequestParam(value = "tipoReporteTramites") String tipoReporteTramites,
                                                         @RequestParam(value = "estado", required = false) String estado,
                                                         @RequestParam(value = "fechaInicioTramites", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicioTramites,
                                                         @RequestParam(value = "fechaFinTramites", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFinTramites,
                                                         @RequestParam("formato") String formato) throws DocumentException, IOException {
        List<Tramite> tramites;
        if ("Por Estado".equals(tipoReporteTramites) && estado != null && !estado.isEmpty()) {
            tramites = reporteService.getTramitesByEstado(estado);
        } else if ("Por Rango de Fechas".equals(tipoReporteTramites) && fechaInicioTramites != null && fechaFinTramites != null) {
            tramites = reporteService.getTramitesByFechaCreacion(fechaInicioTramites, fechaFinTramites);
        } else {
            tramites = reporteService.getAllTramites();
        }

        byte[] reportData;
        String contentDisposition;
        if ("PDF".equalsIgnoreCase(formato)) {
            reportData = reporteService.generatePdfReport(tramites);
            contentDisposition = "attachment; filename=reporte_tramites.pdf";
        } else {
            reportData = reporteService.generateTextReport(tramites);
            contentDisposition = "attachment; filename=reporte_tramites.txt";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType("PDF".equalsIgnoreCase(formato) ? MediaType.APPLICATION_PDF : MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", contentDisposition);

        return ResponseEntity.ok().headers(headers).body(reportData);
    }

    private MediaType getMediaTypeForFormat(String formato) {
        return switch (formato.toLowerCase()) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "txt" -> MediaType.TEXT_PLAIN;
            default -> throw new IllegalArgumentException("Formato no soportado: " + formato);
        };
    }
}
