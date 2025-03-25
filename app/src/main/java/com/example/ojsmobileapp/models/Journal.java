package com.example.ojsmobileapp.models;

public class Journal {
    private String journal_id;
    private String name;
    private String description;
    private String abbreviation;
    private String portada;
    private String journalThumbnail;

    public String getJournal_id() {
        return journal_id != null ? journal_id : "";
    }

    public String getName() {
        return name != null ? name : "Sin nombre";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public String getAbbreviation() {
        return abbreviation != null ? abbreviation : "";
    }

    public String getPortada() {
        return portada != null ? portada : "";
    }

    public String getJournalThumbnail() {
        return journalThumbnail != null ? journalThumbnail : "";
    }

    public void setJournal_id(String journal_id) {
        this.journal_id = journal_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public void setJournalThumbnail(String journalThumbnail) {
        this.journalThumbnail = journalThumbnail;
    }
}