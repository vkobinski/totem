package com.kodev.totem.services;

import com.kodev.totem.models.Medico;
import com.kodev.totem.models.Paciente;
import com.kodev.totem.repositories.MedicoRepository;
import com.kodev.totem.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    @Autowired
    public SearchService(MedicoRepository medicoRepository, PacienteRepository pacienteRepository) {
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public Response getAllPacientesAndMedico() {
        List<Medico> medicos = medicoRepository.findAll();
        medicos.removeIf((medico) -> !medico.isAtivo());

        Response response = new Response();
        response.medicos = medicos;
        response.pacientes = pacienteRepository.findAll();

        return response;
    }

    public class Response {
        public List<Medico> medicos;
        public List<Paciente> pacientes;
    }
}
