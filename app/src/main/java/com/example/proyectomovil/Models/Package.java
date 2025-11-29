package com.example.proyectomovil.Models;

public class Package {
    private String Id;
    private String Name;
    private String Description;
    private String TicketCount;
    private Double Price;
    private int DurationsDays;
    private boolean Active;

    public Package(String id, String name, String description, String ticketCount, Double price, int durationsDays, boolean active) {
        Id = id;
        Name = name;
        Description = description;
        TicketCount = ticketCount;
        Price = price;
        DurationsDays = durationsDays;
        Active = active;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTicketCount() {
        return TicketCount;
    }

    public void setTicketCount(String ticketCount) {
        TicketCount = ticketCount;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public int getDurationsDays() {
        return DurationsDays;
    }

    public void setDurationsDays(int durationsDays) {
        DurationsDays = durationsDays;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }
}
