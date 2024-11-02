package com.virtual.app.sicbo.module.services.impl;

import com.virtual.app.sicbo.module.data.Journal;
import com.virtual.app.sicbo.module.repository.JournalRepository;
import com.virtual.app.sicbo.module.services.JournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalServiceImpl implements JournalService {

    private final JournalRepository journalRepository;

    @Autowired
    public JournalServiceImpl(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    @Override
    public Journal saveJournal(Journal journal) {
        return journalRepository.save(journal);
    }


    @Override
    public Journal getJournalById(Integer journalId) {
        Optional<Journal> optionalJournal = journalRepository.findById(journalId);
        return optionalJournal.orElse(null); // Return null if not found, or you can throw an exception
    }

    @Override
    public List<Journal> getAllJournals() {
        return journalRepository.findAll();
    }

    @Override
    public void deleteJournal(Integer journalId) {
        journalRepository.deleteById(journalId);
    }

    @Override
    public List<Journal> getJournalsByUserUuidAndDateCreated(String userUuid, LocalDate dateCreated) {
        return journalRepository.findByUserUuidAndDateCreated(userUuid, dateCreated);
    }

    @Override
    public List<Journal> getJournalsByUserUuidAndDateLastModified(String userUuid, LocalDateTime dateLastModified) {
        return journalRepository.findByUserUuidAndDateLastModified(userUuid, dateLastModified);
    }

    @Override
    public List<Object[]> getTotalProfitByDate(String userUuid) {
        return journalRepository.getTotalProfitByDate(userUuid);
    }

    @Override
    public List<Object[]> getTotalProfitByWeek(String userUuid) {
        return journalRepository.getTotalProfitByWeek(userUuid);
    }
}
