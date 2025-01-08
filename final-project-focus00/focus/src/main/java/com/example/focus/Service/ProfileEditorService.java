package com.example.focus.Service;

import com.example.focus.ApiResponse.ApiException;
import com.example.focus.DTO.ProfileDTOin;
import com.example.focus.Model.*;
import com.example.focus.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileEditorService {

    private final ProfileEditorRepository profileEditorRepository ;
    private final MediaRepository mediaRepository;
    private final ProfilePhotographerRepository profilePhotographerRepository;
    private final MyUserRepository myUserRepository;
    private final PhotographerRepository photographerRepository;

    public List<ProfileEditor> getAllProfiles() {
        List<ProfileEditor> profiles = profileEditorRepository.findAll();

        return profiles;
    }




    public ProfileEditor getMyProfile(Integer editorrid ) {
        ProfileEditor profile = profileEditorRepository.findProfileEditorById(editorrid);
        if(profile == null) {
            throw new ApiException("Editor not found");
        }


        return profile;
    }


    public ProfileEditor getSpecificProfile(Integer editorid ) {
        ProfileEditor profile = profileEditorRepository.findProfileEditorById(editorid);
        if(profile == null) {
            throw new ApiException("Editor not found");
        }


        return profile;
    }


    private  final String UPLOAD_PROFILE_DIR = "C:/Users/doly/Desktop/Upload/Profile/";
    public void updateProfile(Integer id, ProfileDTOin profileDTOin, MultipartFile file) throws IOException{
        Photographer photographer=photographerRepository.findPhotographersById(id);
        if (photographer != null) {

            if (!isValidImageFile(file)) {
                throw new ApiException("Invalid image file. Only JPG, PNG, and JPEG files are allowed");
            }

            Path filePath = Paths.get(UPLOAD_PROFILE_DIR.concat(saveImageFile(file)));

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String filePathString = filePath.toString();

            photographer.getProfilePhotographer().setDescription(profileDTOin.getDescription());
            photographer.getProfilePhotographer().setImage(filePathString);
        } else {
            throw new ApiException("Profile Not Found");
        }
        photographerRepository.save(photographer);
    }



    public void deleteProfile(Integer id) {
        ProfileEditor existingProfile = profileEditorRepository.findProfileEditorById(id);
        if (existingProfile != null) {
            profileEditorRepository.delete(existingProfile);
        } else {
            throw new ApiException("Profile Not Found");
        }
    }




    private  final String UPLOAD_MEDIA_PROFILE_DIR = "C:/Users/doly/Desktop/Upload/Media/";

    public void uploadMediaProfile(Integer profileid, MultipartFile file) throws IOException {
        ProfileEditor profileEditor=profileEditorRepository.findProfileEditorById (profileid);
        if(profileEditor==null){
            throw new ApiException("Profile editor not found");
        }
        String fileName = file.getOriginalFilename();
        String fileType = getFileType(fileName);

        if ("unknown".equals(fileType)) {
            throw new ApiException("Unsupported file type. Only images and videos are allowed");
        }

        Path filePath = Paths.get(UPLOAD_MEDIA_PROFILE_DIR.concat(fileName));

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String filePathString = filePath.toString();

        profileEditor.setNumberOfPosts(profileEditor.getNumberOfPosts() + 1);
        Media media = new Media();
        media.setProfileEditor(profileEditor);
        media.setMediaType(fileType);
        media.setUploadDate(LocalDateTime.now());
        media.setMediaURL(filePathString);
        media.setVisibility(true);

        mediaRepository.save(media);

    }


    // ميثود لتحديد نوع الملف بناءً على امتداده
    private String getFileType(String fileName) {
        String fileType = "unknown";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            fileType = "image";
        } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mov")) {
            fileType = "video";
        }
        return fileType;
    }


    // التحقق من نوع الملف
    private boolean isValidImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg");
    }

    // حفظ الصورة في المسار المحدد
    private String saveImageFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_PROFILE_DIR + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }


}
