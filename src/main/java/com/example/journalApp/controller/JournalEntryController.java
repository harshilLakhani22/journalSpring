package com.example.journalApp.controller;

import com.example.journalApp.entity.JournalEntry;
import com.example.journalApp.entity.User;
import com.example.journalApp.service.JournalEntryService;
import com.example.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user != null) {
            List<JournalEntry> all = user.getJournalEntries();
            if(all != null && !all.isEmpty()){
                return new ResponseEntity<>(all, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No journal entries found", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            journalEntryService.saveEntry(myEntry, username);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED );
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating journal entry: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{searchId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId searchId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(searchId)).toList();
        if(!collect.isEmpty()){
            Optional<JournalEntry> journalEntry = journalEntryService.findById(searchId);
            if(journalEntry.isPresent()){
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{deleteId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId deleteId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean removed = journalEntryService.deleteById(deleteId, username);
        if (removed)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PutMapping("/id/{putId}")
    public ResponseEntity<?> updateJournalEntryById(
            @PathVariable ObjectId putId,
            @RequestBody JournalEntry newEntry
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Authenticated username: " + username);

        User user = userService.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
        }

        List<JournalEntry> matchingEntries = user.getJournalEntries().stream()
                .filter(x -> x.getId().equals(putId))
                .toList();

        if (!matchingEntries.isEmpty()) {
            Optional<JournalEntry> journalEntryOptional = journalEntryService.findById(putId);
            if (journalEntryOptional.isPresent()) {
                JournalEntry old = journalEntryOptional.get();

                old.setTitle(!newEntry.getTitle().isEmpty() ? newEntry.getTitle() : old.getTitle());
                old.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : old.getContent());

                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Journal entry not found or unauthorized", HttpStatus.NOT_FOUND);
    }

}

