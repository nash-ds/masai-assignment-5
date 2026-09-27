package com.hdfc.ledger.web;

import com.hdfc.ledger.dto.ClaimResponse;
import com.hdfc.ledger.dto.CreateClaimRequest;
import com.hdfc.ledger.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    public ResponseEntity<ClaimResponse> create(
            @Valid @RequestBody CreateClaimRequest request) {

        ClaimResponse response = claimService.create(request);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @GetMapping("/{claimNo}")
    public ClaimResponse get(@PathVariable String claimNo) {
        return claimService.find(claimNo);
    }
}