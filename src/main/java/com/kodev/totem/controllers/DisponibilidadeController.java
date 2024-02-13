package com.kodev.totem.controllers;

import com.kodev.totem.models.Disponibilidade;
import com.kodev.totem.models.Medico;
import com.kodev.totem.services.DisponibilidadeService;
import com.kodev.totem.services.SearchService;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/api/v1/disponibilidade")
public class DisponibilidadeController {

    private final DisponibilidadeService disponibilidadeService;

    public DisponibilidadeController(DisponibilidadeService disponibilidadeService) {
        this.disponibilidadeService = disponibilidadeService;
    }

    @GetMapping("/day")
    public ResponseEntity<List<Disponibilidade>> getDisponibilidadeDia(@RequestParam String dia) throws ParseException {

        return ResponseEntity.ok(disponibilidadeService.getDisponibilidadeByDia(dia));
    }

    @GetMapping
    public ResponseEntity<List<Disponibilidade>> getAll() {
        return ResponseEntity.ok(disponibilidadeService.getAll());
    }

    @PostMapping("/{id}")
    public ResponseEntity<List<Disponibilidade>> createDisponibilidades(@PathVariable String id, @RequestBody List<ReceiveDisponibilidade> disponibilidades) throws ParseException {

        return ResponseEntity.ok(disponibilidadeService.createDisponibilidadeList(id, disponibilidades));
    }

    @PostMapping("/getList/{id}")
    public ResponseEntity<List<Disponibilidade>> getList(@PathVariable String id, @RequestBody List<String> days) throws ParseException {

        List<Disponibilidade> byDayList = disponibilidadeService.getByDayList(id, days);

        return ResponseEntity.ok(byDayList);
    }

    @PostMapping("/week/{id}")
    public ResponseEntity<List<Disponibilidade>> getMedicoWeek(@PathVariable String id, @RequestParam int week) {

        List<Disponibilidade> rWeek = disponibilidadeService.getMedicoWeek(id, week);
        return ResponseEntity.ok(rWeek);
    }


    @NoArgsConstructor
    public static class ReceiveDisponibilidade {
        public String dia;
        public List<String> horarios;
    }

}
