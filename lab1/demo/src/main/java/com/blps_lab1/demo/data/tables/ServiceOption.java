package com.blps_lab1.demo.data.tables;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "service_options")
public class ServiceOption {

    public ServiceOption() {}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, unique = true, length = 100)
    private ServiceType type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price_per_day", nullable = false)
    @ColumnDefault("0")
    private double pricePerDay;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ServiceType getType() { return type; }
    public void setType(ServiceType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }
}