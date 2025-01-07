package com.example.focus.Service;

import com.example.focus.ApiResponse.ApiException;
import com.example.focus.DTO.PhotographerDTO;
import com.example.focus.DTO.ProfileDTO;
import com.example.focus.DTO.ProfileDTOin;
import com.example.focus.Model.Media;
import com.example.focus.Model.MyUser;
import com.example.focus.Model.ProfileEditor;
import com.example.focus.Model.ProfilePhotographer;
import com.example.focus.Repository.MediaRepository;
import com.example.focus.Repository.MyUserRepository;
import com.example.focus.Repository.ProfileEditorRepository;
import com.example.focus.Repository.ProfilePhotographerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfilePhotographerService {
    private final ProfilePhotographerRepository profilePhotographerRepository ;
    private final ProfileEditorRepository profileEditorRepository ;
    private final MediaRepository mediaRepository;
    private final MyUserRepository myUserRepository;

    public List<ProfilePhotographer> getAllProfiles() {
        List<ProfilePhotographer> profiles = profilePhotographerRepository.findAll();
        return profiles;
    }




    public ProfilePhotographer getMyProfile(Integer photographerid ) {
        ProfilePhotographer profile = profilePhotographerRepository.findProfilePhotographerById(photographerid);
        if(profile == null) {
            throw new ApiException("Photographer not found");
        }

        return profile;
    }


    public ProfilePhotographer getSpecificProfile(Integer userid1, Integer userid2 ) {
        MyUser user1 = myUserRepository.findMyUserById(userid1);
        if(user1 == null) {
            throw new ApiException("User not not found");
        }

       MyUser user2= myUserRepository.findMyUserById(userid2);
        if(user2 == null) {
            throw new ApiException("User you search about not found");
        }
            ProfilePhotographer profilePhotographer = profilePhotographerRepository.findProfilePhotographerById(userid2);

        return profilePhotographer;

    }


    public void addProfile(ProfilePhotographer profile) {
        profilePhotographerRepository.save(profile);
    }


    //update profile
    private  final String UPLOAD_PROFILE_DIR = "C:/Users/doly/Desktop/Upload/Profile/";
    public void updateProfile(Integer id, ProfileDTOin profileDTOin, MultipartFile file) throws IOException{
        MyUser user=myUserRepository.findMyUserById(id);
        if (user != null) {

            if (!isValidImageFile(file)) {
                throw new ApiException("Invalid image file. Only JPG, PNG, and JPEG files are allowed");
            }

            Path filePath = Paths.get(UPLOAD_PROFILE_DIR.concat(saveImageFile(file)));

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String filePathString = filePath.toString();

           // user.getProfilePhotographer().setDescription(profileDTOin.getDescription());
           // user.getProfilePhotographer().setImage(filePathString);
        } else {
            throw new ApiException("Profile Not Found");
        }
myUserRepository.save(user);
}







    public void deleteProfile(Integer id) {
        ProfilePhotographer existingProfile = profilePhotographerRepository.findProfilePhotographerById(id);
        if (existingProfile != null) {
            profilePhotographerRepository.delete(existingProfile);
        } else {
            throw new ApiException("Profile Not Found");
        }
    }





    private  final String UPLOAD_MEDIA_PROFILE_DIR = "C:/Users/doly/Desktop/Upload/Media/";

    public void uploadMedia(Integer profileid, MultipartFile file) throws IOException {
        ProfilePhotographer profilePhotographer=profilePhotographerRepository.findProfilePhotographerById (profileid);
        if(profilePhotographer==null){
            throw new ApiException("ProfilePhotographer not found");
        }
        String fileName = file.getOriginalFilename();
        String fileType = getFileType(fileName);

        if ("unknown".equals(fileType)) {
            throw new ApiException("Unsupported file type. Only images and videos are allowed");
        }


        Path filePath = Paths.get(UPLOAD_MEDIA_PROFILE_DIR.concat(fileName));

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String filePathString = filePath.toString();

        profilePhotographer.setNumberOfPosts(profilePhotographer.getNumberOfPosts()+1);
        Media media = new Media();
        media.setProfilePhotographer(profilePhotographer);
        media.setMediaType(fileType);  //
        media.setUploadDate(LocalDateTime.now());
        media.setMediaURL(filePathString);
        media.setVisibility(true);

        mediaRepository.save(media);

    }


//get my photo

//    public Media GetMyPhoto(Integer id){
//        Media media = mediaRepository.findMediaById(id);
//    }

//get my video


//get photo number


 //get video number





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























    private String getFileType(String fileName) {
        String fileType = "unknown";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
            fileType = "image";
        } else if (fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mov")) {
            fileType = "video";
        }
        return fileType;
    }




}