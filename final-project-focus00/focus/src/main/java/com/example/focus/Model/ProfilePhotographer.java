package com.example.focus.Model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile_photographer")
public class ProfilePhotographer {
    @Id
    private Long id;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private Integer numberOfPosts;

    @Column(nullable = false, length = 255)
    private String image;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Photographer photographer;
}
