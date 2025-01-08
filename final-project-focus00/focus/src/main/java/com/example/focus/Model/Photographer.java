package com.example.focus.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Photographer {

    @Id
    private Integer id;

    @Column(columnDefinition = "varchar(30) not null")
    private String name;

    @NotEmpty(message = "City cannot be empty")
    @Column(columnDefinition = "varchar(30) not null")
    private String city;

    @Pattern(regexp = "^05[0-9]{8}$", message = "Phone number must start with 05 and be followed by 8 digits")
    @Column(columnDefinition = "varchar(20) not null")
    private String phoneNumber;

    @OneToOne
    @MapsId
    @JsonIgnore
    private MyUser myUser;

    @OneToOne(mappedBy = "photographer", cascade = CascadeType.ALL)
    private ProfilePhotographer profilePhotographer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "photographer")
    @JsonIgnore
    private Set<Tool> tools;

    @OneToMany(mappedBy = "renter")
    private Set<RentTools> myOrders; // Orders for tools this photographer rents from others

    @OneToMany(mappedBy = "owner")
    private Set<RentTools> rentalTools; // Rental transactions for tools this photographer owns

    @OneToMany(mappedBy = "photographer")
    private Set<RequestEditing> requests;
}
