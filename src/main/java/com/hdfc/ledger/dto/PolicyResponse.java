package com.hdfc.ledger.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PolicyResponse {
    String policyNo;
    String customer;
    String email;
    String type;
    int basePremium;
    String status;
    List<String> riderCodes;
}
