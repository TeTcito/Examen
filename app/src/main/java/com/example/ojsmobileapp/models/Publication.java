package com.example.ojsmobileapp.models;

import java.util.List;

public class Publication {
    private String publication_id;
    private String title;
    private String section;
    private String doi;
    private String abstractText;
    private String date_published;
    private List<Galley> galeys;
    private List<Author> authors;
    private List<Keyword> keywords;

    // Getters y Setters
    public String getPublication_id() {
        return publication_id;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getDoi() {
        return doi;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getDate_published() {
        return date_published;
    }

    public List<Galley> getGaleys() {
        return galeys;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public static class Galley {
        private String galley_id;
        private String label;
        private String file_id;
        private String UrlViewGalley;

        // Getters
        public String getGalley_id() {
            return galley_id;
        }

        public String getLabel() {
            return label;
        }

        public String getFile_id() {
            return file_id;
        }

        public String getUrlViewGalley() {
            return UrlViewGalley;
        }
    }

    public static class Author {
        private String nombres;
        private String filiacion;
        private String email;

        // Getters
        public String getNombres() {
            return nombres;
        }

        public String getFiliacion() {
            return filiacion;
        }

        public String getEmail() {
            return email;
        }
    }

    public static class Keyword {
        private String keyword;

        // Getter
        public String getKeyword() {
            return keyword;
        }
    }
}