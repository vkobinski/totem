package com.kodev.totem.services;

import com.kodev.totem.enums.Roles;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsuarioService {

    public final UsuarioRepository usuarioRepository;

    public ConcurrentHashMap<Long, WebSocketSession> userSockets = new ConcurrentHashMap<>();


    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void setSocketForUser(Long id, WebSocketSession session) {
        userSockets.put(id, session);
        userSockets.entrySet().removeIf(entry -> !entry.getValue().isOpen());
    }

    public Usuario criaUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario findUsuarioByEmail(String email) {
        return usuarioRepository.findUsuarioByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    public WebSocketSession getSocketForUser(Usuario usuario) {
        return userSockets.get(usuario.getIdUsuario());
    }

    public Usuario getUsuarioByMedicoId(Long id) {
        return usuarioRepository.findUsuarioByMedico_MedicoId(id);
    }

    public Roles getRole(String role) {
        if (role.equals("MEDICO")) {
            return Roles.MEDICO;
        } else if (role.equals("SECRETARIA")) {
            return Roles.SECRETARIA;
        } else {
            throw new jakarta.persistence.NoResultException("Não existe Role com nome: " + role);
        }
    }
}
