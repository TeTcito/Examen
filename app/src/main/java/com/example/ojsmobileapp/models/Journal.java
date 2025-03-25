package com.example.ojsmobileapp.models;

public class Journal {
    private String journal_id;
    private String name;
    private String description;
    private String abbreviation;
    private String logo;

    // Getters con manejo de nulos
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

    public String getLogo() {
        return logo != null ? logo : "";
    }
}