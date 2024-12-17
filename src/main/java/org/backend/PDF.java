package org.backend;

public class PDF {
    private int id;
    private int userId;
    private String pdfLocation;

    public PDF(int id, int userId, String pdfLocation) {
        this.id = id;
        this.userId = userId;
        this.pdfLocation = pdfLocation;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPdfLocation() {
        return pdfLocation;
    }

    public void setPdfLocation(String pdfLocation) {
        this.pdfLocation = pdfLocation;
    }
}
