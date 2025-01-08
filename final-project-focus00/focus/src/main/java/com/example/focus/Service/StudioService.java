package com.example.focus.Service;

import com.example.focus.ApiResponse.ApiException;
import com.example.focus.DTO.*;
import com.example.focus.Model.*;
import com.example.focus.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudioService {
    private final MyUserRepository myUserRepository;
    private final StudioRepository studioRepository;
    private final PhotographerRepository photographerRepository;
    private final SpaceRepository spaceRepository;
    private final ProfileStudioRepository profileStudioRepository;
    private final EmailService emailService;

    public List<StudioDTO> getAllStudios() {
        List<Studio> studios = studioRepository.findAll();
        List<StudioDTO> studioDTOS = new ArrayList<>();
        for (Studio studio : studios) {
            StudioDTO studioDTO = new StudioDTO();
            studioDTO.setName(studio.getName());
            studioDTO.setUsername(studio.getMyUser().getUsername());
            studioDTO.setEmail(studio.getMyUser().getEmail());
            studioDTO.setPhoneNumber(studio.getPhoneNumber());
            studioDTO.setCity(studio.getCity());
            studioDTO.setAddress(studio.getAddress());
            studioDTO.setCommercialRecord(studio.getCommercialRecord());
            studioDTO.setStatus(studio.getStatus());
            studio.setImageURL(studio.getImageURL());
            studioDTOS.add(studioDTO);
        }
        return studioDTOS;
    }

    public void registerStudio(StudioDTOIn studioDTOIn) {
        MyUser checkUsername = myUserRepository.findMyUserByUsername(studioDTOIn.getUsername());
        MyUser checkEmail = myUserRepository.findMyUserByEmail(studioDTOIn.getEmail());
        if (checkUsername != null) {
            throw new ApiException("Username already exists");
        }
        if (checkEmail != null) {
            throw new ApiException("Email already exists");
        }
        String hashPass = new BCryptPasswordEncoder().encode(studioDTOIn.getPassword());

        MyUser user = new MyUser();
        user.setUsername(studioDTOIn.getUsername());
        user.setEmail(studioDTOIn.getEmail());
        user.setPassword(hashPass);
        user.setRole("STUDIO");

        Studio studio = new Studio();
        studio.setMyUser(user);
        studio.setCommercialRecord(studioDTOIn.getCommercialRecord());
        studio.setName(studioDTOIn.getName());
        studio.setCity(studioDTOIn.getCity());
        studio.setPhoneNumber(studioDTOIn.getPhoneNumber());
        studio.setAddress(studioDTOIn.getAddress());
        studio.setStatus("Inactive");

        user.setStudio(studio); // Ensure bidirectional relationship
        myUserRepository.save(user);
        studioRepository.save(studio);
    }

    public void activateStudio(Integer admin_id, Integer studio_id) {
        MyUser user = myUserRepository.findMyUserById(admin_id);
        if (user == null) {
            throw new ApiException("Admin not found");
        }
        Studio studio = studioRepository.findStudioById(studio_id);
        if (studio == null) {
            throw new ApiException("Studio not found");
        }
        studio.setStatus("active");

        studioRepository.save(studio);

        emailService.sendEmail(studio.getMyUser().getEmail(),
                "Welcome to Focus!",
                "Dear " + studio.getName() + ",\n\n" +
                        "Congratulations! Your studio has been successfully activated.\n" +
                        "We are thrilled to have you on board and excited to see your work. " +
                        "You can now log in and start your journey with us.\n\n" +
                        "Best regards,\n" +
                        "Focus Team");
    }

    public void rejectStudio(Integer admin_id, Integer studio_id) {
        MyUser user = myUserRepository.findMyUserById(admin_id);
        if (user == null) {
            throw new ApiException("Admin not found");
        }
        Studio studio = studioRepository.findStudioById(studio_id);
        if (studio == null) {
            throw new ApiException("Studio not found");
        }
        studio.setStatus("rejected");

        studioRepository.save(studio);

        emailService.sendEmail(studio.getMyUser().getEmail(),
                "Sorry, your studio was rejected",
                "Dear " + studio.getName() + ",\n\n" +
                        "We regret to inform you that your studio has been rejected.\n" +
                        "If you have any questions, please feel free to contact us.\n\n" +
                        "Best regards,\n" +
                        "Focus Team");
    }

    private final String UPLOAD_PROFILE_DIR = "C:/Users/doly/Desktop/Upload/Profile/";

    public void uploadImage(Integer id, MultipartFile file) throws IOException {
        MyUser user = myUserRepository.findMyUserById(id);
        if (user == null) {
            throw new ApiException("Profile not found");
        }
        Studio studio = studioRepository.findStudioById(user.getId());
        if (studio == null) {
            throw new ApiException("Studio not found");
        }

        if (!isValidImageFile(file)) {
            throw new ApiException("Invalid image file. Only JPG, PNG, and JPEG files are allowed");
        }
        Path filePath = Paths.get(UPLOAD_PROFILE_DIR.concat(saveImageFile(file)));
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        String filePathString = filePath.toString();
        studio.setImageURL(filePathString);

        studioRepository.save(studio);
    }

    private boolean isValidImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg");
    }

    private String saveImageFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_PROFILE_DIR + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public List<StudioDTOPhotographer> getStudiosByCity(String city) {
        List<Studio> studios = studioRepository.findStudioByCity(city);
        if (studios.isEmpty()) {
            throw new ApiException("No studios available");
        }
        List<StudioDTOPhotographer> studioDTOS = new ArrayList<>();
        for (Studio studio : studios) {
            StudioDTOPhotographer studioDTO = new StudioDTOPhotographer();
            studioDTO.setName(studio.getName());
            studioDTO.setUsername(studio.getMyUser().getUsername());
            studioDTO.setPhoneNumber(studio.getPhoneNumber());
            studioDTO.setEmail(studio.getMyUser().getEmail());
            studioDTO.setAddress(studio.getAddress());
            studioDTO.setCommercialRecord(studio.getCommercialRecord());
            studioDTO.setCity(studio.getCity());
            studioDTOS.add(studioDTO);
        }
        return studioDTOS;
    }

    public StudioDTOPhotographer getSpecificStudio(Integer studio_id) {
        Studio studio = studioRepository.findStudioById(studio_id);
        if (studio == null) {
            throw new ApiException("Studio not found");
        }
        StudioDTOPhotographer studioDTO = new StudioDTOPhotographer();
        studioDTO.setName(studio.getName());
        studioDTO.setUsername(studio.getMyUser().getUsername());
        studioDTO.setPhoneNumber(studio.getPhoneNumber());
        studioDTO.setEmail(studio.getMyUser().getEmail());
        studioDTO.setAddress(studio.getAddress());
        studioDTO.setCommercialRecord(studio.getCommercialRecord());
        studioDTO.setCity(studio.getCity());
        return studioDTO;
    }
}
