package com.example.focus.Service;
import com.example.focus.ApiResponse.ApiException;
import com.example.focus.DTO.*;
//import com.example.focus.Model.BookSpace;
//import com.example.focus.Model.Space;
import com.example.focus.Model.*;
//import com.example.focus.Repository.BookSpaceRepository;
//import com.example.focus.Repository.SpaceRepository;
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
    //  private final BookSpaceRepository bookSpaceRepository;

    public List<StudioDTO> getAllStudios(){
        List<Studio> studios = studioRepository.findAll();
        List<StudioDTO> studioDTOS = new ArrayList<>();
        for (Studio studio : studios){
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

    public void registerStudio(StudioDTOIn studioDTOIn){
        MyUser checkUsername =myUserRepository.findMyUserByUsername(studioDTOIn.getUsername());
        MyUser checkEmail =myUserRepository.findMyUserByEmail(studioDTOIn.getEmail());
        if(checkUsername != null){
            throw new ApiException("Username already exists");
        }
        if(checkEmail != null){
            throw new ApiException("email already exists");
        }
        String hashPass=new BCryptPasswordEncoder().encode(studioDTOIn.getPassword());


        MyUser user = new MyUser();
        user.setUsername(studioDTOIn.getUsername());
        user.setEmail(studioDTOIn.getEmail());
        user.setPassword(hashPass);
        user.setRole("STUDIO");


        Studio studio=new Studio();
        studio.setMyUser(user);
        studio.setCommercialRecord(studioDTOIn.getCommercialRecord());
        studio.setName(studioDTOIn.getName());
        studio.setCity(studioDTOIn.getCity());
        studio.setPhoneNumber(studioDTOIn.getPhoneNumber());
        studio.setAddress(studioDTOIn.getAddress());
        studio.setStatus("Inactive");

        if(studio!=null && user != null) {
            studioRepository.save(studio);
            myUserRepository.save(user);
        }




    }

    public void activateStudio(Integer admin_id,Integer studio_id){
        MyUser user = myUserRepository.findMyUserById(admin_id);
        if(user == null){
            throw new ApiException("admin not found");
        }
        Studio studio = studioRepository.findStudioById(studio_id);
        if(studio == null){
            throw new ApiException("studio not found");
        }
        studio.setStatus("active");
        studioRepository.save(studio);
        //String to, String subject, String body
        emailService.sendEmail(studio.getMyUser().getEmail(),
                "Welcome to Focus !",
                "Dear " + studio.getName() + ",\n\n" +
                        "Congratulations! Your studio has been successfully activated.\n" +
                        "We are thrilled to have you on board and excited to see your work. " +
                        "You can now log in and start your journey with us.\n\n" +
                        "Best regards,\n" +
                        "focus Team");


    }

    public void rejectStudio(Integer admin_id,Integer studio_id){
        MyUser user = myUserRepository.findMyUserById(admin_id);
        if(user == null){
            throw new ApiException("admin not found");
        }
        Studio studio = studioRepository.findStudioById(studio_id);
        if(studio == null){
            throw new ApiException("studio not found");
        }
        studio.setStatus("rejected");
        studioRepository.save(studio);
//to,subject,body
        emailService.sendEmail(studio.getMyUser().getEmail(),
                "Sorry your studio was rejected",
                "Dear " + studio.getName() + ",\n\n" +
                        "We regret to inform you that your studio has been rejected.\n" +
                        "If you have any questions, please feel free to contact us.\n\n" +
                        "Best regards,\n" +
                        "focus Team");

    }



    // studio add new space
//    public void addSpace (Integer studio_id,SpaceDTOIn spaceDTOIn){
//        Studio studio = studioRepository.findStudioById(studio_id);
//        if(studio == null){
//            throw new ApiException("studio not found");
//        }
//        if (!studio.getStatus().equalsIgnoreCase("active") ){
//            throw new ApiException("studio is not activated");
//        }
//        Space space = spaceRepository.findSpaceByName(spaceDTOIn.getName());
//        if(space != null){
//            throw new ApiException("space name already exists");
//        }
//        Space newSpace = new Space();
//        newSpace.setName(spaceDTOIn.getName());
//        newSpace.setType(spaceDTOIn.getType());
//        newSpace.setArea(spaceDTOIn.getArea());
//        newSpace.setDescription(spaceDTOIn.getDescription());
//        newSpace.setPrice(spaceDTOIn.getPrice());
//        newSpace.setStatus("active");
//        newSpace.setImage(spaceDTOIn.getImage());
//        newSpace.setStudio(studio);
//        spaceRepository.save(newSpace);
//
//    }

//    // studio delete space
//    public void deleteSpace (Integer studio_id,String space_name){
//        Studio studio = studioRepository.findStudioById(studio_id);
//        if(studio == null){
//            throw new ApiException("studio not found");
//        }
//        if (!studio.getIsActivated()){
//            throw new ApiException("studio is not activated");
//        }
//        Space space = spaceRepository.findSpaceByName(space_name);
//        if(space == null){
//            throw new ApiException("space not found");
//        }
//        spaceRepository.delete(space);
//
//    }
//
//    // update space
//    public void updateSpace (Integer studio_id,SpaceDTOIn spaceDTOIn){
//        Studio studio = studioRepository.findStudioById(studio_id);
//        if(studio == null){
//            throw new ApiException("studio not found");
//        }
//        if (!studio.getIsActivated()){
//            throw new ApiException("studio is not activated");
//        }
//        Space space = spaceRepository.findSpaceByName(spaceDTOIn.getName());
//        if(space == null){
//            throw new ApiException("space not found");
//        }
//        space.setName(spaceDTOIn.getName());
//        space.setType(spaceDTOIn.getType());
//        space.setArea(spaceDTOIn.getArea());
//        space.setDescription(spaceDTOIn.getDescription());
//        space.setPrice(spaceDTOIn.getPrice());
//        space.setStatus(spaceDTOIn.getStatus());
//        space.setImage(spaceDTOIn.getImage());
//        spaceRepository.save(space);
//
//    }
//
//    public List<RentalStudioRequestDTO> getSpaces(){
//        List<BookSpace> rsr = bookSpaceRepository.findAll();
//        List<RentalStudioRequestDTO> rsrDTO = new ArrayList<>();
//        for (BookSpace rs : rsr){
//            RentalStudioRequestDTO rsDTO = new RentalStudioRequestDTO(rs.getStartDate(),rs.getEndDate(),rs.getStatus(),rs.getNote());
//            rsrDTO.add(rsDTO);
//
//        }
//        return rsrDTO;
//    }
//
//    // studio can accept or reject the request from photographer
//    public void acceptOrRejectRequest(Integer studio_id,Integer request_id,String response){
//        Studio studio = studioRepository.findStudioById(studio_id);
//        if(studio == null){
//            throw new ApiException("studio not found");
//        }
//        if(!studio.getIsActivated()){
//            throw new ApiException("studio is not activated");
//        }
//        BookSpace sr = bookSpaceRepository.findRentalStudioRequestById(request_id);
//        if(sr == null){
//            throw new ApiException("request not found");
//        }
//
//        sr.setStatus(response);
//
//    }



    private  final String UPLOAD_PROFILE_DIR = "C:/Users/doly/Desktop/Upload/Profile/";
    public void UploadImage(Integer id, MultipartFile file) throws IOException {
        MyUser user=myUserRepository.findMyUserById(id);
        if (user != null) {

            if (!isValidImageFile(file)) {
                throw new ApiException("Invalid image file. Only JPG, PNG, and JPEG files are allowed");
            }
            Path filePath = Paths.get(UPLOAD_PROFILE_DIR.concat(saveImageFile(file)));
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String filePathString = filePath.toString();
            user.getStudio().setImageURL(filePathString);
        } else {
            throw new ApiException("Profile Not Found");
        }
        myUserRepository.save(user);
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


    public List<StudioDTOPhotographer> getStudiosByCity(String city) {


        List<Studio> studios = studioRepository.findStudioByCity(city);
        if(studios.isEmpty()) {
            throw new ApiException("Not have any studio");
        }

        List<StudioDTOPhotographer> studioDTOS = new ArrayList<>();
        for (Studio studio1 : studios) {
            StudioDTOPhotographer studioDTO1 = new StudioDTOPhotographer();
            studioDTO1.setName(studio1.getName());
            studioDTO1.setUsername(studio1.getMyUser().getUsername());
            studioDTO1.setPhoneNumber(studio1.getPhoneNumber());
            studioDTO1.setEmail(studio1.getMyUser().getEmail());
            studioDTO1.setAddress(studio1.getAddress());
            studioDTO1.setCommercialRecord(studio1.getCommercialRecord());
            studioDTO1.setCity(studio1.getCity());
            studioDTOS.add(studioDTO1);
        }
        return studioDTOS;

    }

    public StudioDTOPhotographer getSpecificStudio(Integer studio_id) {


        Studio studio = studioRepository.findStudioById(studio_id);
        if(studio==null) {
            throw new ApiException("studio not found");
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
