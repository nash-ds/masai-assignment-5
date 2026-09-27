package com.hdfc.ledger.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateClaimRequest{
        @NotBlank
        public String policyNo;

        @NotNull
        public Integer claimAmount;

        @NotBlank
        public String urgency;

        String hospitalName;
}
