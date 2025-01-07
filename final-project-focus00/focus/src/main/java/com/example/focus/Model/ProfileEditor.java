package com.example.focus.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProfileEditor {
    @Id
    private Integer id;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @PositiveOrZero(message = "Number of posts cannot be negative")
    private Integer numberOfPosts;

    @Column(nullable = true)
    private String imageURL;

    @OneToOne
    @MapsId
    @JsonIgnore
    @JoinColumn(name = "id", referencedColumnName = "id")
    private MyUser myUser;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ProfileEditor")
    private Set<Media> medias;


}