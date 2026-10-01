package com.homeforge.document.domain;

public enum DocumentStatus {
    DRAFT,
    PENDING_SIGNATURE,
    PARTIALLY_SIGNED,
    SIGNED,
    COMPLETED,
    CANCELLED,
    EXPIRED
}
