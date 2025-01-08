package com.example.focus.Service;

import com.example.focus.ApiResponse.ApiException;
import com.example.focus.DTO.*;
import com.example.focus.Model.*;
import com.example.focus.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotographerService {
    private final MyUserRepository myUserRepository;
    private final PhotographerRepository photographerRepository;
    private final ProfilePhotographerRepository profilePhotographerRepository;
    private final RentToolsRepository rentToolsRepository;
    private final ToolRepository toolRepository;
    private final EmailService emailService;

    public List<PhotographerDTO> getAllPhotographers() {
        List<Photographer> photographers = photographerRepository.findAll();
        List<PhotographerDTO> photographerDTOS = new ArrayList<>();

        for (Photographer photographer : photographers) {
            PhotographerDTO photographerDTO = new PhotographerDTO(
                    photographer.getName(),
                    photographer.getMyUser().getUsername(),
                    photographer.getMyUser().getEmail(),
                    photographer.getCity(),
                    photographer.getPhoneNumber()
            );
            photographerDTOS.add(photographerDTO);
        }
        return photographerDTOS;
    }

    public void PhotographerRegistration(PhotographerDTOin photographerDTOin) {
        String hashPass = new BCryptPasswordEncoder().encode(photographerDTOin.getPassword());

        MyUser checkUsername = myUserRepository.findMyUserByUsername(photographerDTOin.getUsername());
        MyUser checkEmail = myUserRepository.findMyUserByEmail(photographerDTOin.getEmail());
        if (checkUsername != null) {
            throw new ApiException("Username already exists");
        }
        if (checkEmail != null) {
            throw new ApiException("Email already exists");
        }

        MyUser user = new MyUser();
        user.setUsername(photographerDTOin.getUsername());
        user.setEmail(photographerDTOin.getEmail());
        user.setPassword(hashPass);
        user.setRole("PHOTOGRAPHER");

        // Save MyUser first to ensure it's managed
        MyUser savedUser = myUserRepository.save(user);

        Photographer photographer = new Photographer();
        photographer.setMyUser(savedUser); // Use the managed entity
        photographer.setName(photographerDTOin.getName());
        photographer.setCity(photographerDTOin.getCity());
        photographer.setPhoneNumber(photographerDTOin.getPhoneNumber());

        // Save Photographer and ProfilePhotographer
        photographerRepository.save(photographer);
        photographerProfile(savedUser);
    }
    private void photographerProfile(MyUser newUser) {

        ProfilePhotographer profilePhotographer = new ProfilePhotographer();
        profilePhotographer.setPhotographer(photographerRepository.findPhotographersById(newUser.getId())); // Use the managed entity
        profilePhotographer.setNumberOfPosts(0);

        // Save Photographer and ProfilePhotographer
        profilePhotographerRepository.save(profilePhotographer);
    }

    public void updatePhotographer(Integer id, PhotographerDTOin photographerDTOin) {
        Photographer existingPhotographer = photographerRepository.findPhotographersById(id);
        if (existingPhotographer == null) {
            throw new ApiException("Photographer Not Found");
        }

        existingPhotographer.setName(photographerDTOin.getName());
        existingPhotographer.setCity(photographerDTOin.getCity());
        existingPhotographer.getMyUser().setUsername(photographerDTOin.getUsername());
        existingPhotographer.getMyUser().setEmail(photographerDTOin.getEmail());
        existingPhotographer.setPhoneNumber(photographerDTOin.getPhoneNumber());

        photographerRepository.save(existingPhotographer);
    }

    public void deletePhotographer(Integer id) {
        MyUser myUser = myUserRepository.findMyUserById(id);
        if (myUser == null) {
            throw new ApiException("Photographer Not Found");
        }

        Photographer photographer = photographerRepository.findPhotographersById(id);
        ProfilePhotographer profilePhotographer = profilePhotographerRepository.findProfilePhotographerById(id);

        // Disassociate relationships
        if (photographer != null) {
            photographer.setMyUser(null);
        }
        if (profilePhotographer != null) {
            profilePhotographer.setPhotographer(null);
        }
        myUser.setPhotographer(null);

        myUserRepository.delete(myUser);
    }

    public void rentToolRequest(Integer photographerId, Integer toolId, RentToolsDTOIn rentTool) {
        Photographer photographer = photographerRepository.findPhotographersById(photographerId);
        if (photographer == null) {
            throw new ApiException("Photographer not found");
        }

        Tool tool = toolRepository.findToolById(toolId);
        if (tool == null) {
            throw new ApiException("Tool not found");
        }
        if (photographerId.equals(tool.getPhotographer().getId())) {
            throw new ApiException("The photographer is the owner of the required tool.");
        }

        for (RentTools existingRent : rentToolsRepository.findAll()) {
            if (toolId.equals(existingRent.getTool().getId())) {
                if (!(rentTool.getEndDate().isBefore(existingRent.getStartDate()) || rentTool.getStartDate().isAfter(existingRent.getEndDate()))) {
                    throw new ApiException("This tool is rented now for another photographer.");
                }
            }
        }

        if (rentTool.getStartDate().isAfter(rentTool.getEndDate())) {
            throw new ApiException("The start date is after the end date.");
        }

        long daysBetween = ChronoUnit.DAYS.between(rentTool.getStartDate(), rentTool.getEndDate());
        Double totalPrice = tool.getRentalPrice() * (daysBetween + 1);
        rentTool.setRentPrice(totalPrice);

        RentTools rentTools = new RentTools();
        tool.setNumberOfRentals(tool.getNumberOfRentals() + 1);
        rentTools.setStartDate(rentTool.getStartDate());
        rentTools.setEndDate(rentTool.getEndDate());
        rentTools.setOwner(tool.getPhotographer());
        rentTools.setRenter(photographer);
        rentTools.setRentPrice(totalPrice);
        rentTools.setTool(tool);

        rentToolsRepository.save(rentTools);
        emailService.sendEmail(
                photographer.getMyUser().getEmail(),
                "Tool Rental Confirmation",
                "Dear " + photographer.getName() + ",\n\n" +
                        "The tool has been successfully rented.\n" +
                        "Tool Name: " + tool.getName() + "\n" +
                        "Total Price: " + totalPrice + " SR\n\n" +
                        "Best regards,\nFocus Team"
        );
    }

    public List<ToolDTO> viewMyRentTools(Integer photographerId) {
        Photographer photographer = photographerRepository.findPhotographersById(photographerId);
        if (photographer == null) {
            throw new ApiException("Photographer not found");
        }

        List<ToolDTO> toolDTOS = new ArrayList<>();
        for (RentTools rentTool : rentToolsRepository.findAll()) {
            if (rentTool.getRenter().getId().equals(photographer.getId())) {
                Tool tool = rentTool.getTool();
                ToolDTO toolDTO = new ToolDTO(tool.getName(), tool.getDescription(), tool.getCategory(), tool.getBrand(),
                        tool.getNumberOfRentals(), tool.getModelNumber(), tool.getRentalPrice(), tool.getImageURL());
                toolDTOS.add(toolDTO);
            }
        }

        if (toolDTOS.isEmpty()) {
            throw new ApiException("Photographer has no rented tools");
        }

        return toolDTOS;
    }

    public List<ToolDTO> viewRentalTools(Integer photographerId) {
        Photographer photographer = photographerRepository.findPhotographersById(photographerId);
        if (photographer == null) {
            throw new ApiException("Photographer not found");
        }

        List<ToolDTO> toolDTOS = new ArrayList<>();
        for (RentTools rentTool : rentToolsRepository.findAll()) {
            if (rentTool.getOwner().getId().equals(photographer.getId())) {
                Tool tool = rentTool.getTool();
                ToolDTO toolDTO = new ToolDTO(tool.getName(), tool.getDescription(), tool.getCategory(), tool.getBrand(),
                        tool.getNumberOfRentals(), tool.getModelNumber(), tool.getRentalPrice(), tool.getImageURL());
                toolDTOS.add(toolDTO);
            }
        }

        if (toolDTOS.isEmpty()) {
            throw new ApiException("Photographer has no rental tools");
        }

        return toolDTOS;
    }
}
