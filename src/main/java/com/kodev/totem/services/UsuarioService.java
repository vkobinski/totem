package com.kodev.totem.services;

import com.kodev.totem.repositories.UsuarioRepository;
import org.aspectj.weaver.BoundedReferenceType;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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

}
