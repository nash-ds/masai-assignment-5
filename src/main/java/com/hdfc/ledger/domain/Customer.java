package com.hdfc.ledger.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, unique = true)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Policy> policies = new ArrayList<>();

    protected Customer() {
    }

    public Customer(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }
}
