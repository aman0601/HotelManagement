package com.hotel.booking.Model.Entity;

import javax.persistence.*;

@Entity
@Table(name = "hotels", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "location"}))
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    public Hotel(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Hotel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}

