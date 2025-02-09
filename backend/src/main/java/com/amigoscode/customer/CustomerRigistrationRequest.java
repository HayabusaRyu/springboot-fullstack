package com.amigoscode.customer;

public record CustomerRigistrationRequest(
        String name,
        String email,
        Integer age
) {
}
