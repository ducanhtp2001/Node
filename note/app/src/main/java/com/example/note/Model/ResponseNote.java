package com.example.note.Model;

import java.util.List;

public class ResponseNote {
    private boolean status;
    private List<Note> notes;

    public ResponseNote(boolean status, List<Note> notes) {
        this.status = status;
        this.notes = notes;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
