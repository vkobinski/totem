package com.kodev.totem.services;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Paciente;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.repositories.AtendimentoRepository;
import com.kodev.totem.repositories.MedicoRepository;
import com.kodev.totem.repositories.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Transient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    private final UsuarioService usuarioService;


    @Autowired
    public AtendimentoService(AtendimentoRepository atendimentoRepository, PacienteRepository pacienteRepository, MedicoRepository medicoRepository, UsuarioService usuarioService) {
        this.atendimentoRepository = atendimentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.usuarioService = usuarioService;
    }

    public Atendimento criaAtendimento(Atendimento atendimento) {
        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> getAtendimentosByMedicoId(Long id) {
        return atendimentoRepository.getAtendimentosByMedico_MedicoId(id);
    }

    @Transient
    public String markPatientArrived(Long idPaciente) {

        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.atTime(LocalTime.MAX));

        Optional<Paciente> byId = pacienteRepository.findById(idPaciente);

        if(byId.isEmpty()) throw new EntityNotFoundException();

        List<Atendimento> atendimentos = atendimentoRepository.findByPacienteAndDataAtendimentoBetween(byId.get(), startOfDay, endOfDay);

        for (Atendimento aT : atendimentos) {
            aT.setChegou(true);
            atendimentoRepository.save(aT);

            Usuario userMedico = usuarioService.getUsuarioByMedicoId(aT.getMedico().getMedicoId());
            WebSocketSession socketForUser = usuarioService.getSocketForUser(userMedico);
            try {
                socketForUser.sendMessage(new TextMessage("S"));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return "Appointment marked as patient arrived.";
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentoRepository.findAll();
    }

    public List<Atendimento> getTodayAtendimentos() {
        return atendimentoRepository.findAllByDataAtendimentoToday();
    }
}
