package com.example.focus.Model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "studio")
public class Studio {
    @Id
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String name;

    @Column(nullable = false, length = 30)
    private String city;

    @Column(nullable = false, length = 10, unique = true)
    private String phoneNumber;

    @Column(nullable = false, length = 10, unique = true)
    private String commercialRecord;

    @Column(nullable = false, length = 60)
    private String address;


    @Column(nullable = false, length = 60)
    private String status;

    @Column(nullable = true, length = 255)
    private String imageURL;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", referencedColumnName = "id")
    private MyUser myUser;
}
