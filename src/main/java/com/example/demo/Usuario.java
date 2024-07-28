package com.example.demo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NamedQueries({
        @NamedQuery(name="Usuario.findByCedula", query="SELECT u FROM Usuario u WHERE u.cedula = :cedula"),
        @NamedQuery(name="Usuario.findByEmail", query="SELECT u FROM Usuario u WHERE u.email = :email")
})
@Table(name = "USUARIO", schema = "JCHOEZ")
public class Usuario {
    public interface Registro {}
    public interface Actualization {}

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "USUARIO_SEQ", allocationSize = 1)
    private Long id;

    @NotEmpty(groups = {Registro.class, Default.class}, message = "El primer nombre no puede estar vacío.")
    @Column(name = "PRIMER_NOMBRE")
    private String primerNombre;

    @NotEmpty(groups = {Registro.class, Default.class}, message = "El segundo nombre no puede estar vacío.")
    @Column(name = "SEGUNDO_NOMBRE")
    private String segundoNombre;

    @NotEmpty(groups = {Registro.class, Default.class}, message = "El primer apellido no puede estar vacío.")
    @Column(name = "PRIMER_APELLIDO")
    private String primerApellido;

    @NotEmpty(groups = {Registro.class, Default.class}, message = "El segundo apellido no puede estar vacío.")
    @Column(name = "SEGUNDO_APELLIDO")
    private String segundoApellido;

    @NotEmpty(groups = {Registro.class, Default.class}, message = "La cédula no puede estar vacía.")
    @Column(name = "CEDULA", unique = true)
    private String cedula;

    @NotEmpty(groups = {Registro.class, Default.class}, message = "La dirección no puede estar vacía.")
    @Column(name = "DIRECCION")
    private String direction;

    @NotEmpty(groups = {Registro.class, Default.class}, message = "La parroquia no puede estar vacía.")
    @Column(name = "PARROQUIA")
    private String parroquia;

    @NotEmpty(groups = {Registro.class, Default.class}, message = "El correo electrónico no puede estar vacío.")
    @Email(groups = {Registro.class, Default.class}, message = "Correo electrónico no válido.")
    @Column(name = "EMAIL", unique = true)
    private String email;

    @NotEmpty(groups = {Registro.class, Default.class}, message = "El teléfono no puede estar vacío.")
    @Column(name = "TELEFONO")
    private String telefono;

    @NotEmpty(groups = {Registro.class}, message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.", groups = {Registro.class})
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "ROL")
    private String rol;

    @Column(name = "DEPARTAMENTO")
    private String departamento;

    @Column(name = "FECHA_REGISTRO")
    private LocalDate fechaRegistro;

    @Transient
    private String confirmPass;

    @NotNull(message = "Debe aceptar la política de privacidad.", groups = NuevoRegistro.class)
    @Column(nullable = true)
    private Boolean consent;

    public Usuario() {}

    public interface NuevoRegistro {}
}

