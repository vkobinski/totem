package com.kodev.totem.controllers;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.services.AtendimentoService;
import lombok.Getter;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/atendimento")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    @Autowired
    public AtendimentoController(AtendimentoService atendimentoService) {
        this.atendimentoService = atendimentoService;
    }

    @PutMapping("/chegou/{id}")
    public ResponseEntity<String> markPatientHasArrived(@PathVariable Long id) {
        return ResponseEntity.ok(atendimentoService.markPatientArrived(id));
    }

    @GetMapping
    public ResponseEntity<List<Atendimento>> getAtendimentos() {
        return ResponseEntity.ok(atendimentoService.getAtendimentos());
    }

    @GetMapping("/today")
    public ResponseEntity<List<Atendimento>> getAtendimentosToday() {
        return ResponseEntity.ok(atendimentoService.getTodayAtendimentos());
    }
}
