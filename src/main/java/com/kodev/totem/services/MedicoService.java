package com.kodev.totem.services;

import com.kodev.totem.models.Medico;
import com.kodev.totem.repositories.MedicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public Medico criaMedico(Medico medico) {
        return medicoRepository.save(medico);
    }

    public List<Medico> getMedicos() {
        return medicoRepository.findAll();
    }

    public Medico getMedicoById(Long id) {
        return medicoRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Optional<Medico> getMedicoByNomeCompleto(String nome) {
        return medicoRepository.findMedicoByNomeCompletoIgnoreCase(nome);
    }

    public Medico deletaMedico(Long id) {
        return medicoRepository.findById(id).map((medico) -> {
            medico.setAtivo(false);
            medicoRepository.save(medico);
            return medico;
        }).orElseThrow(EntityNotFoundException::new);
    }
}
