package com.example.focus.Controller;

import com.example.focus.ApiResponse.ApiResponse;
import com.example.focus.DTO.OfferEditingInputDTO;
import com.example.focus.DTO.OfferEditingOutputDTO;
import com.example.focus.Service.OfferEditingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/focus/offer-editing")
@RequiredArgsConstructor
public class OfferEditingController {

    private final OfferEditingService offerEditingService;

    @GetMapping("/get-all")
    public ResponseEntity getAllOffers() {
        List<OfferEditingOutputDTO> offers = offerEditingService.getAllOffers();
        return ResponseEntity.status(200).body(offers);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity getOfferById(@PathVariable Integer id) {
        OfferEditingOutputDTO offer = offerEditingService.getOfferById(id);
        return ResponseEntity.status(200).body(offer);
    }

    @PostMapping("/create")
    public ResponseEntity createOffer(@RequestBody @Valid OfferEditingInputDTO offerInput) {
        OfferEditingOutputDTO createdOffer = offerEditingService.createOffer(offerInput);
        return ResponseEntity.status(200).body(createdOffer);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateOffer(@PathVariable Integer id, @RequestBody @Valid OfferEditingInputDTO offerInput) {
        OfferEditingOutputDTO updatedOffer = offerEditingService.updateOffer(id, offerInput);
        return ResponseEntity.status(200).body(updatedOffer);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteOffer(@PathVariable Integer id) {
        offerEditingService.deleteOffer(id);
        return ResponseEntity.status(200).body(new ApiResponse("Offer deleted successfully"));
    }

    @PostMapping("/accept/{offerId}")
    public ResponseEntity acceptOffer(@PathVariable Integer offerId) {
        OfferEditingOutputDTO acceptedOffer = offerEditingService.acceptOffer(offerId);
        return ResponseEntity.status(200).body(acceptedOffer);
    }

    @PostMapping("/reject/{offerId}")
    public ResponseEntity rejectOffer(@PathVariable Integer offerId) {
        OfferEditingOutputDTO rejectedOffer = offerEditingService.rejectOffer(offerId);
        return ResponseEntity.status(200).body(rejectedOffer);
    }
}
