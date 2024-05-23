package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Usuario> optionalUsuario  = usuarioRepository.findByEmail(email);
        Usuario usuario = optionalUsuario.orElseThrow(() -> new UsernameNotFoundException
                ("Usuario no encontrado con email: " + email));

        return new CustomUserDetails(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getPrimerNombre(),
                usuario.getSegundoNombre(),
                usuario.getPrimerApellido(),
                usuario.getSegundoApellido(),
                usuario.getCedula(),
                usuario.getDirection(),
                usuario.getParroquia(),
                usuario.getEmail(),
                usuario.getTelefono(),
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
    }
}