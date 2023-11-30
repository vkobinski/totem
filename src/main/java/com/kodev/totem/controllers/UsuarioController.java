package com.kodev.totem.controllers;

import com.kodev.totem.enums.Roles;
import com.kodev.totem.models.Medico;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.services.MedicoService;
import com.kodev.totem.services.UsuarioService;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<Usuario> loginUsuario(@RequestParam String email, @RequestParam String senha, @RequestParam String token) {
        Usuario usuario = usuarioService.findUsuarioByEmail(email);

        usuario.getMedico().setToken(token);

        System.out.println(token);

        if (usuario.getPassword().equals(senha) && usuario.getMedico().isAtivo()) return ResponseEntity.ok(usuario);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/list-user")
    public ResponseEntity<List<Usuario>> getUsersMedicos() {


        List<Usuario> medicUsers = usuarioService.getMedicUsers();
        return ResponseEntity.ok(medicUsers);
    }

    @PostMapping("/form")
    public ResponseEntity<Usuario> criaUsuario(@RequestParam String email, @RequestParam String senha) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(senha);
        usuario.setRole(Roles.SECRETARIA);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criaUsuario(usuario));

    }

    @PutMapping("/medico/{id}")
    public ResponseEntity<Usuario> updateMedico(@PathVariable Long id,
                                               @RequestParam(required = false)MultipartFile foto,
                                               @RequestParam(required = false) String email,
                                               @RequestParam(required = false) String senha,
                                               @RequestParam(required = false) String nomeCompleto) {

        Usuario usuario = usuarioService.editUser(id, foto, email, senha, nomeCompleto);
        return ResponseEntity.ok(usuario);
    }


    @PostMapping("/form-medico")
    public ResponseEntity<Usuario> criaUsuario(@RequestParam String email, @RequestParam String senha, @RequestParam(required = false) MultipartFile foto, @RequestParam String nomeCompleto) {
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
