package com.kodev.totem.controllers;

import com.kodev.totem.models.Paciente;
import com.kodev.totem.repositories.PacienteRepository;
import com.kodev.totem.services.PacienteService;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
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
}
