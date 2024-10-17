package br.com.siswbrasil.model;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "extended_props")
public class ExtendedProps extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;
    
    @Transient
    public List<String> guests = new ArrayList<>();

    @OneToMany
    @JoinTable(
        name = "extended_props_guests",
        joinColumns = @JoinColumn(name = "extended_props_id"),
        inverseJoinColumns = @JoinColumn(name = "guest_id")
    )
    public List<Guest> guestList = new ArrayList<>();
    
    @Column(name = "location")
    public String location;
    
    @Column(name = "description")
    public String description;
    
    @Column(name = "calendar")
    public String calendar;
}