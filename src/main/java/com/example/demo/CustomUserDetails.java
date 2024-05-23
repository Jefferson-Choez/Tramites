package com.example.demo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;
import lombok.Getter;

@Getter
public class CustomUserDetails extends User {
    private final String primerNombre;
    private final String segundoNombre;
    private final String primerApellido;
    private final String segundoApellido;
    private final String cedula;
    private final String direction;
    private final String parroquia;
    private final String email;
    private final String telefono;

    public CustomUserDetails(String userName, String password, String primerNombre, String segundoNombre, String primerApellido,
                             String segundoApellido, String cedula, String direction, String parroquia, String email, String telefono,
                             Collection<? extends GrantedAuthority> authorities) {
        super(userName, password, authorities);
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.cedula = cedula;
        this.direction = direction;
        this.parroquia = parroquia;
        this.email = email;
        this.telefono = telefono;
    }
}