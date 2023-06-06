package com.kodev.totem.repositories;

import com.kodev.totem.models.Usuario;
import com.kodev.totem.services.UsuarioService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public Usuario findUsuarioByMedico_MedicoId(Long id);
}
