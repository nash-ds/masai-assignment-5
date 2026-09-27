package com.hdfc.ledger.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_no", nullable = false, unique = true)
    private String policyNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private PolicyType productType;

    @Column(name = "base_premium", nullable = false)
    private int basePremium;

    @Column(nullable = false)
    private String status;

    @OneToMany(mappedBy = "policy", fetch = FetchType.LAZY)
    private List<Claim> claims = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "policy_riders",
            joinColumns = @JoinColumn(name = "policy_id"),
            inverseJoinColumns = @JoinColumn(name = "rider_id")
    )
    private List<Rider> riders = new ArrayList<>();


    public Policy(String policyNo, Customer customer, PolicyType productType,
                  int basePremium, String status) {
        this.policyNo = policyNo;
        this.customer = customer;
        this.productType = productType;
        this.basePremium = basePremium;
        this.status = status;
    }

    public void addRider(Rider rider) {
        riders.add(rider);
    }
}