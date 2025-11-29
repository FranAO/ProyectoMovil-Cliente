package com.example.proyectomovil.Models;

import java.util.Date;

public class Ticket {
    private String Id;
    private String StudentId;
    private String PackageId;
    private String Status;
    private Date PurchaseDate;

    public Ticket(String id, String studentId, String packageId, String status, Date purchaseDate) {
        Id = id;
        StudentId = studentId;
        PackageId = packageId;
        Status = status;
        PurchaseDate = purchaseDate;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getPackageId() {
        return PackageId;
    }

    public void setPackageId(String packageId) {
        PackageId = packageId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Date getPurchaseDate() {
        return PurchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        PurchaseDate = purchaseDate;
    }
}
