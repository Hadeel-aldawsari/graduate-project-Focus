package com.example.focus.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Media type is required")
    @Pattern(regexp = "^(image|video)$", message = "type must be 'video' or 'image'")
    @Column(columnDefinition = "varchar(5) not null")
    private String mediaType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @AssertTrue(message = "visibility ")
    @Column(columnDefinition = "boolean not null")
    private Boolean visibility;

    @NotEmpty(message = "Media URL is required")
    @Column(columnDefinition = "varchar(255) not null")
    private String mediaURL;


    @ManyToOne
    @JoinColumn(name = "profile_photographer_id", referencedColumnName = "id")
    @JsonIgnore
    private ProfilePhotographer ProfilePhotographer;

    @ManyToOne
    @JoinColumn(name = "requestEditing_id")
    private RequestEditing requestEditing;

    @ManyToOne
    @JoinColumn(name = "profile_Editor_id", referencedColumnName = "id")
    @JsonIgnore
    private ProfileEditor ProfileEditor;
}