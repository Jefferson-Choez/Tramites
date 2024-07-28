package com.example.demo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReporteService {

    private final TramiteRepository tramiteRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ReporteService(TramiteRepository tramiteRepository, UsuarioRepository usuarioRepository) {
        this.tramiteRepository = tramiteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> getUsuariosByFechaRegistro(LocalDate fechaInicio, LocalDate fechaFin) {
        return usuarioRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);
    }

    public List<Tramite> getAllTramites() {
        List<Tramite> tramites = tramiteRepository.findAll();
        logTramites(tramites);
        return tramites;
    }

    public List<Tramite> getTramitesByEstado(String estado) {
        List<Tramite> tramites = tramiteRepository.findByEstado(estado);
        logTramites(tramites);
        return tramites;
    }

    public List<Tramite> getTramitesByFechaCreacion(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Tramite> tramites = tramiteRepository.findByFechaCreacionBetween(fechaInicio, fechaFin);
        logTramites(tramites);
        return tramites;
    }

    private void logTramites(List<Tramite> tramites) {
        if (tramites.isEmpty()) {
            System.out.println("No se encontraron trámites.");
        } else {
            for (Tramite tramite : tramites) {
                System.out.println("Tramite ID: " + tramite.getId() + ", Estado: " + tramite.getEstado());
            }
        }
    }

    public ByteArrayInputStream generateUsuariosReport(List<Usuario> usuarios, String formato) {
        return switch (formato.toLowerCase()) {
            case "pdf" -> generatePdfUsuarios(usuarios);
            case "texto" -> generateTextUsuarios(usuarios);
            default -> throw new IllegalArgumentException("Formato no soportado: " + formato);
        };
    }

    private ByteArrayInputStream generatePdfUsuarios(List<Usuario> usuarios) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph header = new Paragraph("Reporte de Usuarios");
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph("\n"));

            PdfPTable table = createUsuariosTable(usuarios);
            document.add(table);

            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error al generar el reporte de usuarios en formato PDF", e);
        }
    }

    private PdfPTable createUsuariosTable(List<Usuario> usuarios) {
        PdfPTable table = new PdfPTable(7);
        table.addCell("ID");
        table.addCell("Cedula");
        table.addCell("Nombres y apellidos");
        table.addCell("Email");
        table.addCell("Direccion");
        table.addCell("Parroquia");
        table.addCell("Telefono");

        for (Usuario usuario : usuarios) {
            table.addCell(String.valueOf(usuario.getId()));
            table.addCell(usuario.getCedula());
            table.addCell(usuario.getPrimerNombre() + " " + usuario.getSegundoNombre() + " " + usuario.getPrimerApellido() + " " + usuario.getSegundoApellido());
            table.addCell(usuario.getEmail());
            table.addCell(usuario.getDirection());
            table.addCell(usuario.getParroquia());
            table.addCell(usuario.getTelefono());
        }
        return table;
    }

    private ByteArrayInputStream generateTextUsuarios(List<Usuario> usuarios) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID\tCedula\tNombres y apellidos\tEmail\tDireccion\tParroquia\tTelefono\n");
        for (Usuario usuario : usuarios) {
            sb.append(usuario.getId()).append("\t")
                    .append(usuario.getCedula()).append("\t")
                    .append(usuario.getPrimerNombre()).append(" ").append(usuario.getSegundoNombre()).append(" ")
                    .append(usuario.getPrimerApellido()).append(" ").append(usuario.getSegundoApellido()).append("\t")
                    .append(usuario.getEmail()).append("\t")
                    .append(usuario.getDirection()).append("\t")
                    .append(usuario.getParroquia()).append("\t")
                    .append(usuario.getTelefono()).append("\n");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el reporte de usuarios en formato texto", e);
        }
    }

    public byte[] generatePdfReport(List<Tramite> tramites) throws DocumentException {
        if (tramites.isEmpty()) {
            throw new DocumentException("No hay trámites para generar el reporte.");
        }

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();
        PdfPTable table = new PdfPTable(8);
        addTableHeader(table);
        addRows(table, tramites);

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("ID", "Estado", "Fecha de Creación", "Tipo de Trámite", "Subtipo de Trámite",
                        "Nombres y Apellidos", "Cédula", "Teléfono")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<Tramite> tramites) {
        for (Tramite tramite : tramites) {
            table.addCell(String.valueOf(tramite.getId()));
            table.addCell(tramite.getEstado());
            table.addCell(tramite.getFechaCreacion().toString());
            table.addCell(tramite.getTipo());
            table.addCell(tramite.getSubtipo());
            table.addCell(tramite.getUsuario().getPrimerNombre() + " " + tramite.getUsuario().getSegundoNombre()
                    + " " + tramite.getUsuario().getPrimerApellido() + " " + tramite.getUsuario().getSegundoApellido());
            table.addCell(tramite.getUsuario().getCedula());
            table.addCell(tramite.getUsuario().getTelefono());
        }
    }

    public byte[] generateTextReport(List<Tramite> tramites) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        writer.write("ID,Estado,Fecha de Creación,Tipo de Trámite,Subtipo de Trámite,Nombres y Apellidos,Cédula,Teléfono\n");

        for (Tramite tramite : tramites) {
            writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                    tramite.getId(),
                    tramite.getEstado(),
                    tramite.getFechaCreacion().toString(),
                    tramite.getTipo(),
                    tramite.getSubtipo(),
                    tramite.getUsuario().getPrimerNombre() + " " + tramite.getUsuario().getSegundoNombre()
                            + " " + tramite.getUsuario().getPrimerApellido() + " " + tramite.getUsuario().getSegundoApellido(),
                    tramite.getUsuario().getCedula(),
                    tramite.getUsuario().getTelefono()));
        }

        writer.flush();
        writer.close();
        return out.toByteArray();
    }
}
