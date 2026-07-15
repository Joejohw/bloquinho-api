package com.bloquinho.professional.domain;

public record Professional(
    String publicId,
    String name,
    String businessName,
    String description,
    String phone,
    String whatsapp,
    String email,
    String instagram,
    String city,
    String state,
    boolean active
) {
}
