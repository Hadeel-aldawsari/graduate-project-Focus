package com.example.focus.Controller;


import com.example.focus.ApiResponse.ApiResponse;
import com.example.focus.DTO.ProfileDTO;
import com.example.focus.DTO.ProfileDTOin;
import com.example.focus.DTO.ToolDTOIn;
import com.example.focus.Model.MyUser;
import com.example.focus.Service.ProfilePhotographerService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/focus/photographer/profile")
@RequiredArgsConstructor
public class ProfilePhotographerController {
    private final ProfilePhotographerService profilePhotographerService;

    @GetMapping("/get-all")
    public  ResponseEntity getAllProfilePhotographer(){
       return ResponseEntity.status(200).body(profilePhotographerService.getAllProfiles()) ;
    }
    //photographer
    @GetMapping("/get-my-profile")
    public ResponseEntity getMyProfilePhotographer(@AuthenticationPrincipal MyUser myUser){
        return ResponseEntity.status(200).body(profilePhotographerService.getMyProfile(myUser.getId()));
    }
    //all
    @GetMapping("/get-specific-profile/{id}")
    public ResponseEntity getSpecificProfilePhotographer(@PathVariable Integer id,  @AuthenticationPrincipal MyUser myUser){
        return ResponseEntity.status(200).body(profilePhotographerService.getSpecificProfile(myUser.getId(),id));
    }
    //photographer
    @PostMapping("/upload-media")
    public ResponseEntity uploadMedia( @AuthenticationPrincipal MyUser myUser, @RequestParam("file") MultipartFile file) {
        try {
            profilePhotographerService.uploadMedia(myUser.getId(),file);

        }  catch (IOException e) {
            return ResponseEntity.status(500).body("Error occurred while uploading the file.");
        }
        return ResponseEntity.status(200).body(new ApiResponse("Upload successful."));
    }
    //photographer
    @PutMapping("/update/{id}")
    public ResponseEntity updateProfile( @AuthenticationPrincipal MyUser myUser, @ModelAttribute @Valid ProfileDTOin profileDTOin,
                                         @RequestParam("file") MultipartFile file){
        try {
            profilePhotographerService.updateProfile(myUser.getId(),profileDTOin,file);

        }  catch (IOException e) {
            return ResponseEntity.status(500).body("Error occurred while uploading the file.");
        }
        return ResponseEntity.status(200).body(new ApiResponse("updated successful."));
    }
}
