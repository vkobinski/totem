package com.kodev.totem.controllers;

import com.kodev.totem.enums.Roles;
import com.kodev.totem.models.Medico;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.services.MedicoService;
import com.kodev.totem.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MedicoService medicoService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, MedicoService medicoService) {
        this.usuarioService = usuarioService;
        this.medicoService = medicoService;
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> loginUsuario(@RequestParam String email, @RequestParam String senha) {
        Usuario usuario = usuarioService.findUsuarioByEmail(email);

        if(usuario.getPassword().equals(senha)) return ResponseEntity.ok(usuario);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PostMapping("/form")
    public ResponseEntity<Usuario> criaUsuario(@RequestParam String email, @RequestParam String senha, @RequestParam String role, @RequestParam(required = false) Long idMedico) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(senha);
        usuario.setRole(usuarioService.getRole(role));

        Medico medico = medicoService.getMedicoById(idMedico);

        if(usuario.getRole() == Roles.MEDICO) usuario.setMedico(medico);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criaUsuario(usuario));

    }
}
