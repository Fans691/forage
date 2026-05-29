package com.fans691.forage.service;

import com.fans691.forage.domain.Note;
import com.fans691.forage.repository.NoteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> findAll() {
        return noteRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
