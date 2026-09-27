package com.hdfc.ledger.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClaimResponse {
    String claimNo;
    String policyNo;
    int claimAmount;
    String urgency;
    String status;
}
