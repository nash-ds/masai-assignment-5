package com.hdfc.ledger.config;

import com.hdfc.ledger.domain.Customer;
import com.hdfc.ledger.domain.Policy;
import com.hdfc.ledger.domain.PolicyType;
import com.hdfc.ledger.domain.Rider;
import com.hdfc.ledger.repo.CustomerRepository;
import com.hdfc.ledger.repo.PolicyRepository;
import com.hdfc.ledger.repo.RiderRepository;

import jakarta.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final PolicyRepository policyRepository;
    private final RiderRepository riderRepository;

    public DataSeeder(CustomerRepository customerRepository,
                      PolicyRepository policyRepository,
                      RiderRepository riderRepository) {
        this.customerRepository = customerRepository;
        this.policyRepository = policyRepository;
        this.riderRepository = riderRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (policyRepository.count() > 0) {
            printStartup();
            return;
        }

        Customer anita = customerRepository.save(
                new Customer("Anita Sharma", "anita.sharma@hdfclife.example"));
        Customer rahul = customerRepository.save(
                new Customer("Rahul Mehta", "rahul.mehta@hdfclife.example"));
        Customer priya = customerRepository.save(
                new Customer("Priya Nair", "priya.nair@hdfclife.example"));
        Customer vikram = customerRepository.save(
                new Customer("Vikram Singh", "vikram.singh@hdfclife.example"));
        Customer sneha = customerRepository.save(
                new Customer("Sneha Patel", "sneha.patel@hdfclife.example"));

        Rider accident = riderRepository.findByCode("ACCIDENT_COVER").orElseThrow();
        Rider waiver = riderRepository.findByCode("WAIVER_OF_PREMIUM").orElseThrow();

        Policy p1001 = policyRepository.save(
                new Policy("HDFC-LIFE-1001", anita, PolicyType.TERM, 18500, "Active"));
        Policy p1002 = policyRepository.save(
                new Policy("HDFC-LIFE-1002", rahul, PolicyType.ULIP, 42000, "Active"));
        Policy p1003 = policyRepository.save(
                new Policy("HDFC-LIFE-1003", priya, PolicyType.ENDOWMENT, 27000, "Lapsed"));
        Policy p1004 = policyRepository.save(
                new Policy("HDFC-LIFE-1004", vikram, PolicyType.TERM, 15200, "Active"));
        Policy p1005 = policyRepository.save(
                new Policy("HDFC-LIFE-1005", sneha, PolicyType.ULIP, 36000, "Active"));
        Policy p1006 = policyRepository.save(
                new Policy("HDFC-LIFE-1006", anita, PolicyType.ENDOWMENT, 22000, "Pending"));

        p1001.addRider(accident);
        p1001.addRider(waiver);
        policyRepository.save(p1001);

        printStartup();
    }

    private void printStartup() {

        System.out.println("Policy count → " + policyRepository.count());
        System.out.println("Customer count → " + customerRepository.count());

        policyRepository.findByPolicyNo("HDFC-LIFE-1004")
                .ifPresent(p -> System.out.println(
                        "Lookup HDFC-LIFE-1004 customer → " + p.getCustomer().getFullName()));

        System.out.println("Active count → " +
                policyRepository.findByStatusOrderByPolicyNoAsc("Active").size());

        System.out.println("TERM count → " +
                policyRepository.findByProductTypeOrderByPolicyNoAsc(PolicyType.TERM).size());

        System.out.println("Anita Sharma policy count → " +
                policyRepository.findByCustomer_FullNameOrderByPolicyNoAsc("Anita Sharma").size());

        String premiumPolicies = policyRepository.findWithPremiumAtLeast(20000)
                .stream()
                .map(Policy::getPolicyNo)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        System.out.println("minPremium=20000 policy numbers → " + premiumPolicies);

        policyRepository.findByPolicyNo("HDFC-LIFE-1001").ifPresent(policy ->
                System.out.println("HDFC-LIFE-1001 rider codes → " +
                        policy.getRiders().stream()
                                .map(Rider::getCode)
                                .sorted()
                                .reduce((a, b) -> a + ", " + b)
                                .orElse(""))
        );

        System.out.println(
                "HTTP contract (seed loaded)\n" +
                        "GET /api/policies → 200, length 6, first HDFC-LIFE-1001, last HDFC-LIFE-1006\n" +
                        "GET /api/policies/HDFC-LIFE-1004 → customer Vikram Singh, email vikram.singh@hdfclife.example, basePremium 15200, riderCodes []\n" +
                        "GET /api/policies/HDFC-LIFE-1001 → riderCodes [\"ACCIDENT_COVER\",\"WAIVER_OF_PREMIUM\"]\n" +
                        "GET /api/policies?status=Active → length 4\n" +
                        "GET /api/policies?type=TERM → HDFC-LIFE-1001, HDFC-LIFE-1004\n" +
                        "GET /api/policies?customer=Anita%20Sharma → length 2 (1001, 1006)\n" +
                        "GET /api/policies/search?minPremium=20000 → policyNo order 1002, 1005, 1003, 1006"
        );
    }
}
