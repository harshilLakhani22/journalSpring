package com.example.journalApp.service;

import com.example.journalApp.entity.JournalEntry;
import com.example.journalApp.entity.User;
import com.example.journalApp.repository.JournalEntryRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepo journalEntryRepo;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) throws Exception {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new Exception("User not found");
        }
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalEntryRepo.save(journalEntry);
        user.getJournalEntries().add(saved);
        userService.saveUser(user);
    }

    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepo.save(journalEntry);
    }

    public List<JournalEntry> getAllEntry(){
        return journalEntryRepo.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id){
        return journalEntryRepo.findById(id);
    }

    public boolean deleteById(ObjectId id, String username) {
        boolean removed = false;
        try {
            User user = userService.findByUsername(username);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveNewUser(user); // Save updated user after removal
                journalEntryRepo.deleteById(id); // Delete the journal entry from the database
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("An error occurred while deleting the entry.", e);
        }

        return removed;
    }
}
