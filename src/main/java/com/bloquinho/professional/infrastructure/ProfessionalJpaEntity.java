package com.bloquinho.professional.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "professionals")
public class ProfessionalJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "public_id", nullable = false, unique = true, length = 21)
    private String publicId;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(name = "business_name", length = 150)
    private String businessName;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(length = 30)
    private String phone;
    @Column(length = 30)
    private String whatsapp;
    @Column(length = 255)
    private String email;
    @Column(length = 255)
    private String instagram;
    @Column(length = 120)
    private String city;
    @Column(length = 2)
    private String state;
    @Column(nullable = false)
    private boolean active;

    protected ProfessionalJpaEntity() {
    }

    public String getPublicId() { return publicId; }
    public String getName() { return name; }
    public String getBusinessName() { return businessName; }
    public String getDescription() { return description; }
    public String getPhone() { return phone; }
    public String getWhatsapp() { return whatsapp; }
    public String getEmail() { return email; }
    public String getInstagram() { return instagram; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public boolean isActive() { return active; }
}
