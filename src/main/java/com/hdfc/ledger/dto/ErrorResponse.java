package com.hdfc.ledger.dto;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class ErrorResponse {
    int status;
    String error;
    String message;
    Map<String, String> fields;
}
