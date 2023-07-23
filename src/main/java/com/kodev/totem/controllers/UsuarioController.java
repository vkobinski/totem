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
import org.springframework.web.multipart.MultipartFile;

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
        if(usuario.getPassword().equals(senha) && usuario.getMedico().isAtivo()) return ResponseEntity.ok(usuario);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PostMapping("/form")
    public ResponseEntity<Usuario> criaUsuario(@RequestParam String email, @RequestParam String senha) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(senha);
        usuario.setRole(Roles.SECRETARIA);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criaUsuario(usuario));

    }

    @PostMapping("/form-medico")
    public ResponseEntity<Usuario> criaUsuario(@RequestParam String email, @RequestParam String senha, @RequestParam MultipartFile foto, @RequestParam String nomeCompleto) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(senha);
        usuario.setRole(Roles.MEDICO);

        Medico medico = new Medico();
        medico.setNomeCompleto(nomeCompleto);

        try {
            medico.setFoto(foto.getBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        medico = medicoService.criaMedico(medico);

        usuario.setMedico(medico);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criaUsuario(usuario));

    }
}
