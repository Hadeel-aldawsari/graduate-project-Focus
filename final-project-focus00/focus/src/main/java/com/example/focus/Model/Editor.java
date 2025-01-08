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
public class Editor {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String name;

    @NotEmpty(message = "Please enter your city")
    @Column(columnDefinition = "varchar(40) not null")
    private String city;

    @NotEmpty(message = "Please enter your phone number")
    @Pattern(regexp = "^05\\d{8}$", message = "Phone number must start with 05 and have 10 digits")
    @Column(columnDefinition = "varchar(10) not null unique")
    private String phoneNumber;

    @OneToOne
    @MapsId
    @JsonIgnore
    private MyUser myUser;

    @OneToMany(mappedBy = "editor", cascade = CascadeType.ALL)
    private Set<RequestEditing> requests;

    @OneToMany(mappedBy = "editor", cascade = CascadeType.ALL)
    private Set<OfferEditing> offerEditings;

    @OneToOne(mappedBy = "editor", cascade = CascadeType.ALL)
    private ProfileEditor profileEditor;
}
