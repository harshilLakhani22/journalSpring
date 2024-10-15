package com.example.journalApp.service;

import com.example.journalApp.entity.JournalEntry;
import com.example.journalApp.entity.User;
import com.example.journalApp.repository.JournalEntryRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepo journalEntryRepo;

    @Autowired
    private UserService userService;

    public void saveEntry(JournalEntry journalEntry, String username) throws Exception {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new Exception("User not found");
        }
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalEntryRepo.save(journalEntry);
        user.getJournalEntries().add(saved);
        userService.saveEntry(user);
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

    public void deleteById(ObjectId id, String username) throws Exception {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new Exception("User not found");
        }
        boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
        if (removed) {
            userService.saveEntry(user); // Save updated user after removal
            journalEntryRepo.deleteById(id); // Delete the journal entry from the database
        } else {
            throw new Exception("Journal entry not found for deletion");
        }
    }
}
