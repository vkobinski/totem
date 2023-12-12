package com.kodev.totem.controllers;

import com.kodev.totem.models.Paciente;
import com.kodev.totem.services.PacienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/api/v1/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping("/form")
    public ResponseEntity<Paciente> criaPacienteForm(@RequestParam String nomeCompleto, @RequestParam String dataNascimento) throws ParseException {

        if(nomeCompleto.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Paciente pacienteCriado = new Paciente();
        pacienteCriado.setNomeCompleto(nomeCompleto);

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date parse = dateTimeFormatter.parse(dataNascimento);

        pacienteCriado.setDataNascimento(new Date(parse.getTime()));
        pacienteCriado = pacienteService.criaPaciente(pacienteCriado);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCriado);
    }

    @PostMapping
    public ResponseEntity<Paciente> criaPaciente(@RequestBody Paciente paciente) {
        Paciente pacienteCriado = pacienteService.criaPaciente(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCriado);
    }
    @GetMapping
    public ResponseEntity<List<Paciente>> getPacientes() {
        return ResponseEntity.status(HttpStatus.OK).body(pacienteService.getPacientes());
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> changeStatus(@PathVariable Long id) {
        boolean change = pacienteService.changeStatus(id);
        if(change) return ResponseEntity.status(HttpStatus.OK).body("Status do paciente " + id + " mudou");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possível encontrar paciente " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Paciente> deletePaciente(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(pacienteService.deletePaciente(id));
    }

    @PostMapping("/busca")
    public ResponseEntity<List<Paciente>> getPacientesByNomeContaining(@RequestParam String nome) {
        return ResponseEntity.ok(pacienteService.getPacientesByNomeCompletoContaining(nome));
    }

    @PostMapping("/busca-nome-completo")
    public ResponseEntity<Paciente> getPacientesByNomeCompleto(@RequestParam String nome) {
        return ResponseEntity.ok(pacienteService.getPacientesByNomeCompleto(nome));
    }

    @PostMapping("/buscaPrimeira")
    public ResponseEntity<List<Paciente>> getPacientesByPrimeiraLetra(@RequestParam String letra) {
        return ResponseEntity.ok(pacienteService.getPacientesByPrimeiraLetra(letra));
    }
}
