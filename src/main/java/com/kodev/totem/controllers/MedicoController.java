package com.kodev.totem.controllers;

import com.kodev.totem.models.Medico;
import com.kodev.totem.services.MedicoService;
import com.kodev.totem.services.PacienteService;
import jakarta.persistence.GeneratedValue;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medico")
public class MedicoController {

    public final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @PostMapping("/form")
    public ResponseEntity<Medico> criaMedico(@RequestParam MultipartFile foto, @RequestParam String nomeCompleto) throws IOException {

        Medico medico = new Medico();
        medico.setFoto(foto.getBytes());
        medico.setNomeCompleto(nomeCompleto);

        medico = medicoService.criaMedico(medico);

        return ResponseEntity.status(HttpStatus.CREATED).body(medico);
    }

    @GetMapping
    public ResponseEntity<List<Medico>> getMedicos() {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.getMedicos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Medico> deleteMedico(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(medicoService.deletaMedico(id));
    }

}
