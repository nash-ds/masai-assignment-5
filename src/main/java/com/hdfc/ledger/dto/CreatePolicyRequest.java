package com.hdfc.ledger.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreatePolicyRequest{
        @NotBlank
        public String policyNo;

        @NotBlank
        public String customer;

        @NotBlank
        @Email
        public String email;

        @NotBlank
        public String type;

        @NotNull
        public Integer basePremium;

        @NotBlank
        public String status;
}
