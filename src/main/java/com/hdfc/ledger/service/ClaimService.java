package com.hdfc.ledger.service;

import com.hdfc.ledger.domain.Claim;
import com.hdfc.ledger.domain.Policy;
import com.hdfc.ledger.dto.ClaimResponse;
import com.hdfc.ledger.dto.CreateClaimRequest;
import com.hdfc.ledger.exception.ClaimNotFoundException;
import com.hdfc.ledger.exception.PolicyNotFoundException;
import com.hdfc.ledger.repo.ClaimRepository;
import com.hdfc.ledger.repo.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    public ClaimService(ClaimRepository claimRepository,
                        PolicyRepository policyRepository) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
    }

    public ClaimResponse create(CreateClaimRequest request) {
        Policy policy = policyRepository.findByPolicyNo(request.policyNo)
                .orElseThrow(() -> new PolicyNotFoundException(request.policyNo));

        long next = claimRepository.count() + 1;
        String claimNo = String.format("CLM-%02d", next);

        Claim claim = new Claim(
                claimNo,
                policy,
                request.claimAmount,
                request.urgency,
                "SUBMITTED"
        );

        return toResponse(claimRepository.save(claim));
    }

    public ClaimResponse find(String claimNo) {
        return toResponse(claimRepository.findByClaimNo(claimNo)
                .orElseThrow(() -> new ClaimNotFoundException(claimNo)));
    }

    public List<ClaimResponse> findByPolicy(String policyNo) {
        if (!policyRepository.existsByPolicyNo(policyNo)) {
            throw new PolicyNotFoundException(policyNo);
        }

        return claimRepository.findByPolicy_PolicyNoOrderByClaimNoAsc(policyNo)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(
                claim.getClaimNo(),
                claim.getPolicy().getPolicyNo(),
                claim.getAmount(),
                claim.getUrgency(),
                claim.getStatus()
        );
    }
}
