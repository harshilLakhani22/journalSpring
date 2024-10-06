package com.example.journalApp.controller;

import com.example.journalApp.entity.JournalEntry;
import com.example.journalApp.service.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @GetMapping
    public List<JournalEntry> getAll(){
        return journalEntryService.getAllEntry();
    }

    @PostMapping
    public JournalEntry createEntry(@RequestBody JournalEntry myEntry){
        myEntry.setDate(LocalDateTime.now());
        journalEntryService.saveEntry(myEntry);
        return myEntry;
    }

    @GetMapping("/id/{searchId}")
    public JournalEntry getJournalEntryById(@PathVariable ObjectId searchId){
        return journalEntryService.findById(searchId).orElse(null);
    }

    @DeleteMapping("/id/{deleteId}")
    public boolean deleteJournalEntryById(@PathVariable ObjectId deleteId){
        journalEntryService.deleteById(deleteId);
        return true;
    }

    @PutMapping("/id/{putId}")
    public JournalEntry updateJournalEntryById(@PathVariable ObjectId putId, @RequestBody JournalEntry newEntry){
        JournalEntry old = journalEntryService.findById(putId).orElse(null);
        if(old != null){
            old.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("") ? newEntry.getTitle() : old.getTitle());
            old.setContent(newEntry.getContent() != null && !newEntry.getContent().equals("") ? newEntry.getContent() : old.getContent());
        }
        journalEntryService.saveEntry(old);
        return old;
    }
}
