package com.hdfc.ledger.web;

import com.hdfc.ledger.domain.PolicyType;
import com.hdfc.ledger.dto.ClaimResponse;
import com.hdfc.ledger.dto.CreatePolicyRequest;
import com.hdfc.ledger.dto.PolicyResponse;
import com.hdfc.ledger.service.ClaimService;
import com.hdfc.ledger.service.PolicyService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;
    private final ClaimService claimService;

    public PolicyController(PolicyService policyService,
                            ClaimService claimService) {
        this.policyService = policyService;
        this.claimService = claimService;
    }

    @GetMapping
    public List<PolicyResponse> list(
            @RequestParam(required = false) String status,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(required = false) String customer) {

        if (status != null) {
            return policyService.findByStatus(status);
        }
        if (type != null) {
            return policyService.findByType(PolicyType.valueOf(type.toUpperCase()));
        }
        if (customer != null) {
            return policyService.findByCustomer(customer);
        }
        return policyService.findAll();
    }

    @GetMapping("/search")
    @ApiResponse(responseCode = "200", description = "Matching policies returned")
    public List<PolicyResponse> search(
            @RequestParam(required = false) Integer minPremium) {
        return policyService.searchPremium(minPremium);
    }

    @GetMapping("/{policyNo}")
    @ApiResponse(responseCode = "200", description = "Policy returned")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public PolicyResponse get(@PathVariable String policyNo) {
        return policyService.findByPolicyNo(policyNo);
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Policy created")
    @ApiResponse(responseCode = "400", description = "Validation failure")
    @ApiResponse(responseCode = "409", description = "Duplicate policy")
    public ResponseEntity<PolicyResponse> create(
            @Valid @RequestBody CreatePolicyRequest request) {

        PolicyResponse response = policyService.create(request);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @DeleteMapping("/{policyNo}")
    @ApiResponse(responseCode = "204", description = "Policy deleted")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public ResponseEntity<Void> delete(@PathVariable String policyNo) {
        policyService.delete(policyNo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{policyNo}/claims")
    @ApiResponse(responseCode = "200", description = "Claims returned")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public List<ClaimResponse> claims(@PathVariable String policyNo) {
        return claimService.findByPolicy(policyNo);
    }
}
