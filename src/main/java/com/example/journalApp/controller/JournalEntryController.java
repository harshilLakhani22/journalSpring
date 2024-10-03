package com.example.journalApp.controller;

import com.example.journalApp.entity.JournalEntry;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    private Map<Long, JournalEntry> journalEntries = new HashMap<>();

    @GetMapping
    public List<JournalEntry> getAll(){
        return new ArrayList<>(journalEntries.values());
    }

    @PostMapping
    public boolean createEntry(@RequestBody JournalEntry myEntry){
        journalEntries.put(myEntry.getId(), myEntry);
        return true;
    }

    @GetMapping("/id/{searchId}")
    public JournalEntry getJournalEntryById(@PathVariable Long searchId){
        return journalEntries.get(searchId);
    }

    @DeleteMapping("/id/{deleteId}")
    public boolean deleteJournalEntryById(@PathVariable Long deleteId){
        journalEntries.remove(deleteId);
        return true;
    }

    @PutMapping("/id/{putId}")
    public JournalEntry updateJournalEntryById(@PathVariable Long putId, @RequestBody JournalEntry putEntry){
        return journalEntries.put(putId, putEntry);
    }
}
