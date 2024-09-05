package panicathe.catchtable.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import panicathe.catchtable.dto.ReservationDTO;
import panicathe.catchtable.dto.ReviewDTO;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/stores")
    public ResponseEntity<ResponseDTO> getStores(
            @RequestParam(required = false, defaultValue = "alphabet") String sortBy,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @AuthenticationPrincipal String email) {
        return userService.getStores(sortBy, lat, lon);
    }

    @GetMapping("/stores/{name}")
    public ResponseEntity<ResponseDTO> getStoreDetails(@PathVariable String name) {
        return userService.getStoreDetails(name);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ResponseDTO> makeReservation(
            @RequestBody ReservationDTO reservationDTO,
            @AuthenticationPrincipal String email) {
        return userService.makeReservation(reservationDTO, email);
    }

    @PostMapping("/reservations/{id}/confirm")
    public ResponseEntity<ResponseDTO> confirmVisit(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        return userService.confirmVisit(id, email);
    }

    @PostMapping("/reviews")
    public ResponseEntity<ResponseDTO> writeReview(
            @RequestBody ReviewDTO reviewDTO,
            @AuthenticationPrincipal String email) {
        return userService.writeReview(reviewDTO, email);
    }
}
