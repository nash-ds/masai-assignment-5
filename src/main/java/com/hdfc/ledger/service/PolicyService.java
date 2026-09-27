package com.hdfc.ledger.service;

import com.hdfc.ledger.domain.Customer;
import com.hdfc.ledger.domain.Policy;
import com.hdfc.ledger.domain.PolicyType;
import com.hdfc.ledger.domain.Rider;
import com.hdfc.ledger.dto.CreatePolicyRequest;
import com.hdfc.ledger.dto.PolicyResponse;
import com.hdfc.ledger.exception.DuplicatePolicyException;
import com.hdfc.ledger.exception.InvalidRequestException;
import com.hdfc.ledger.exception.PolicyNotFoundException;
import com.hdfc.ledger.repo.CustomerRepository;
import com.hdfc.ledger.repo.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;

    public PolicyService(PolicyRepository policyRepository,
                         CustomerRepository customerRepository) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
    }

    public List<PolicyResponse> findAll() {
        return policyRepository.findAll().stream()
                .sorted(Comparator.comparing(Policy::getPolicyNo))
                .map(this::toResponse)
                .toList();
    }

    public PolicyResponse findByPolicyNo(String policyNo) {
        return toResponse(policyRepository.findByPolicyNo(policyNo)
                .orElseThrow(() -> new PolicyNotFoundException(policyNo)));
    }

    public List<PolicyResponse> findByStatus(String status) {
        return policyRepository.findByStatusOrderByPolicyNoAsc(status)
                .stream().map(this::toResponse).toList();
    }

    public List<PolicyResponse> findByType(PolicyType type) {
        return policyRepository.findByProductTypeOrderByPolicyNoAsc(type)
                .stream().map(this::toResponse).toList();
    }

    public List<PolicyResponse> findByCustomer(String customer) {
        return policyRepository.findByCustomer_FullNameOrderByPolicyNoAsc(customer)
                .stream().map(this::toResponse).toList();
    }

    public List<PolicyResponse> searchPremium(Integer minPremium) {
        if (minPremium == null || minPremium < 0) {
            throw new InvalidRequestException(
                    "minPremium must be present and greater than or equal to 0");
        }

        return policyRepository.findWithPremiumAtLeast(minPremium)
                .stream().map(this::toResponse).toList();
    }

    public PolicyResponse create(CreatePolicyRequest request) {
        if (policyRepository.existsByPolicyNo(request.policyNo)) {
            throw new DuplicatePolicyException(request.policyNo);
        }

        Customer customer = customerRepository.findByEmail(request.email)
                .orElseGet(() -> customerRepository.save(
                        new Customer(request.customer, request.email)));

        Policy policy = new Policy(
                request.policyNo,
                customer,
                PolicyType.valueOf(request.type),
                request.basePremium,
                request.status
        );

        return toResponse(policyRepository.save(policy));
    }

    public void delete(String policyNo) {
        Policy policy = policyRepository.findByPolicyNo(policyNo)
                .orElseThrow(() -> new PolicyNotFoundException(policyNo));

        policyRepository.delete(policy);
    }

    public List<PolicyResponse> findPremiumAtLeast(int minPremium) {
        return policyRepository.findWithPremiumAtLeast(minPremium)
                .stream().map(this::toResponse).toList();
    }

    private PolicyResponse toResponse(Policy policy) {
        List<String> riderCodes = policy.getRiders().stream()
                .map(Rider::getCode)
                .sorted()
                .toList();

        return new PolicyResponse(
                policy.getPolicyNo(),
                policy.getCustomer().getFullName(),
                policy.getCustomer().getEmail(),
                policy.getProductType().name(),
                policy.getBasePremium(),
                policy.getStatus(),
                riderCodes
        );
    }
}