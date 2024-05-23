package com.example.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCedula(String cedula);
}
