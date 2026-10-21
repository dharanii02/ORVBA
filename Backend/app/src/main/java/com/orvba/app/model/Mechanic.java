package com.orvba.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mechanics")
public class Mechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long mechanicId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;

    private String phone;

    private String email;

    private String address;

    private String specialization;

    private String experience;

    private String idProof;

    private String idProofNumber;

    private boolean available = false;

    private boolean approved = false;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
private boolean rejected = false;

    // Getters and Setters

    public Long getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(Long mechanicId) {
        this.mechanicId = mechanicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public String getIdProofNumber() {
        return idProofNumber;
    }

    public void setIdProofNumber(String idProofNumber) {
        this.idProofNumber = idProofNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public User getUser() {
    return user;
}

public void setUser(User user) {
    this.user = user;
}

public boolean isRejected() {
    return rejected;
}

public void setRejected(boolean rejected) {
    this.rejected = rejected;
}
}