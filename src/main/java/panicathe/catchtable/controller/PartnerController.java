package panicathe.catchtable.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.StoreDTO;
import panicathe.catchtable.service.PartnerService;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping
    public ResponseEntity<String> getPartner(
            @Parameter(name = "Authorization", description = "Bearer [JWT 토큰]", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String authorizationHeader,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(email + " details");
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> addStore(StoreDTO storeDTO, @AuthenticationPrincipal String email) {
        return partnerService.addStore(storeDTO, email);
    }
}
