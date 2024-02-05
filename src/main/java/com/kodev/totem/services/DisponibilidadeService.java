package com.kodev.totem.services;

import com.kodev.totem.controllers.DisponibilidadeController;
import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Disponibilidade;
import com.kodev.totem.repositories.AtendimentoRepository;
import com.kodev.totem.repositories.DisponibilidadeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DisponibilidadeService {

    private final DisponibilidadeRepository disponibilidadeRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final MedicoService medicoService;

    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository, AtendimentoRepository atendimentoRepository, MedicoService medicoService) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.medicoService = medicoService;
    }

    private java.sql.Date convertDate(String date)  {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date parse = null;
        try {
            parse = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new java.sql.Date(parse.getTime());
    }


    public List<Disponibilidade> getDisponibilidadeByDia(String dia) throws ParseException {

        return disponibilidadeRepository.getAllByDia(convertDate(dia));
    }

    public boolean checkIfTimeIsInDisponibilidade(Disponibilidade dis, String time) {

        int hours = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);

        boolean isStart = false;
        boolean isEnd = false;

        if(dis.getHoraInicio() <= hours) {
            if(dis.getMinutoInicio() <= minutes) {
                isStart = true;
            }
        }

        if(dis.getHoraFim() >= hours) {
            if(dis.getMinutoFim() >= minutes) {
                isEnd = true;
            }
        }

        return isStart && isEnd;
    }

    public List<Disponibilidade> getByDayList(String medicoId, List<String> dayList) {

        List<Disponibilidade> disList = new ArrayList<>();

        dayList.forEach((day) -> {

                java.sql.Date date = convertDate(day);
                List<Disponibilidade> all = disponibilidadeRepository.getAllByDiaAndMedico_MedicoId(date, Long.parseLong(medicoId));

                all.stream()
                                .forEach((d) -> d.setMedico(null));

                disList.addAll(all);

        });

        dayList.forEach((day) -> {
            LocalDateTime time;
                Date date = convertDate(day);
                time = date.toLocalDate().atStartOfDay();
            List<Atendimento> all = atendimentoRepository.getAtendimentosByMedico_MedicoId_OnDate(Long.parseLong(medicoId), time);

            all.forEach((a) -> {
                Disponibilidade dis = new Disponibilidade();

                dis.setHoraInicio(a.getDataAtendimento().getHour());
                dis.setHoraFim(a.getDataAtendimento().getHour());
                dis.setMinutoFim(a.getDataAtendimento().getMinute());
                dis.setMinutoInicio(a.getDataAtendimento().getMinute());
                dis.setDia(convertDate(day));
                dis.setAtendimento(true);

                disList.add(dis);
            });

        });


        return disList;

    }

    public boolean checkIfTimeOccupied(int hour, int minute, java.sql.Date date, long medicoId) {

        List<Disponibilidade> all = disponibilidadeRepository.getAllByDiaAndMedico_MedicoId(date, medicoId);

        for (Disponibilidade d : all) {
            boolean r = checkIfTimeIsInDisponibilidade(d, "%d:%d".formatted(hour, minute));
            if(r) return true;
        };

        return false;
    }

    public boolean checkIfDisponibilidadeExists(Disponibilidade newDis) {

        List<Disponibilidade> allByDia = disponibilidadeRepository.getAllByDiaAndMedico_MedicoId(newDis.getDia(), newDis.getMedico().getMedicoId());

        for (Disponibilidade dia : allByDia) {

            if (newDis.getHoraInicio() == dia.getHoraInicio() && newDis.getHoraFim() == dia.getHoraFim() && dia.getMinutoInicio() == newDis.getMinutoInicio() && dia.getMinutoFim() == newDis.getMinutoFim()) {
                return true;
            }
        }

        return false;
    }

    public void deleteAllInDay(String medicoId, java.sql.Date date) {

        List<Disponibilidade> all = disponibilidadeRepository.getAllByDiaAndMedico_MedicoId(date, Long.parseLong(medicoId));
        disponibilidadeRepository.deleteAll(all);
    }

    public List<Disponibilidade> createDisponibilidadeList(String medicoId, List<DisponibilidadeController.ReceiveDisponibilidade> disponibilidades) throws ParseException {

        List<Disponibilidade> list = new ArrayList<>();

        for (DisponibilidadeController.ReceiveDisponibilidade rec : disponibilidades) {

            deleteAllInDay(medicoId, convertDate(rec.dia));

            for (int i = 0; i < rec.horarios.size(); i += 2) {

                Disponibilidade dis = new Disponibilidade();
                dis.setMedico(medicoService.getMedicoById(Long.parseLong(medicoId)));
                dis.setDia(convertDate(rec.dia));


                String horaInicio = rec.horarios.get(i).split(":")[0];
                String minutoInicio = rec.horarios.get(i).split(":")[1];

                String horaFim;
                String minutoFim;

                if(rec.horarios.size()-1 == i) {
                    horaFim = horaInicio;
                    minutoFim = minutoInicio;
                } else {
                    horaFim = rec.horarios.get(i + 1).split(":")[0];
                    minutoFim = rec.horarios.get(i + 1).split(":")[1];
                }

                    dis.setHoraInicio(Integer.parseInt(horaInicio));
                dis.setHoraFim(Integer.parseInt(horaFim));
                dis.setMinutoInicio(Integer.parseInt(minutoInicio));
                dis.setMinutoFim(Integer.parseInt(minutoFim));

                if (!checkIfDisponibilidadeExists(dis)) list.add(disponibilidadeRepository.save(dis));
            }

        }

        list.stream().
                forEach((d) -> {
                    d.getMedico().setFoto(null);
                });

        return list;
    }

    public List<Disponibilidade> getAll() {
        List<Disponibilidade> all = disponibilidadeRepository.findAll();

        all.stream()
                .forEach(dis -> dis.getMedico().setFoto(null));

        return all;
    }
}
