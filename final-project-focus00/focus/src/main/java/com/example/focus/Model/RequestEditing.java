package com.example.focus.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RequestEditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime estimatedCompletionDate;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private String fullCameraName;

    @Column(nullable = false)
    private String sensorSize;

    @Column(nullable = false)
    private String kitLens;

    @Column(nullable = false)
    private String viewFinder;

    @Column(nullable = false)
    private String nativeISO;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "editor_id", nullable = false)
    private Editor editor;

    @OneToMany(mappedBy = "requestEditing") // Fix: Use `mappedBy` for bidirectional mapping
    private Set<OfferEditing> offerEditings;

    @ManyToOne
    @JoinColumn(name = "photographer_id")
    private Photographer photographer;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "requestEditing")
    private Set<Media> medias;

}
