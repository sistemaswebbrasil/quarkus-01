package br.com.siswbrasil.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ExtendedProps extends PanacheEntity {
    @Transient
    public List<String> guests = new ArrayList<>();

    @OneToMany
    public List<Guest> guestList = new ArrayList<>();
    public String location;
    public String description;
    public String calendar;

    // Getters and Setters
    public List<String> getGuests() {
        return guests;
    }

    public void setGuests(List<String> guestNames) {
        this.guests = guestNames;
    }

    public List<Guest> getGuestList() {
        return guestList;
    }

    public void setGuestList(List<Guest> guests) {
        this.guestList = guests;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }
}