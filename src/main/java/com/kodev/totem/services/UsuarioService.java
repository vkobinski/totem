package com.kodev.totem.services;

import com.kodev.totem.enums.Roles;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
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
        System.out.println(email);
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

    public List<Usuario> getMedicUsers() {
        return usuarioRepository.findAll().stream().filter((u) -> getRole(u.getRole().name()) == Roles.MEDICO).toList();
    }

    public Usuario editUser(Long id, MultipartFile foto, String email, String senha, String nomeCompleto) {

        Usuario user = usuarioRepository.findUsuarioByMedico_MedicoId(id);

        if(email != null) user.setEmail(email);
        if(senha != null) user.setPassword(senha);
        if(nomeCompleto != null) user.getMedico().setNomeCompleto(nomeCompleto);

        if(foto != null) {
            try {
                user.getMedico().setFoto(foto.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return usuarioRepository.save(user);
    }
}
