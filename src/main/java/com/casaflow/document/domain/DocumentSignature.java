package com.casaflow.document.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "document_signatures")
public class DocumentSignature {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    private UUID documentId;

    @Column(nullable = false)
    private String signerName;

    @Column(nullable = false, length = 180)
    private String signerEmail;

    @Column(length = 50)
    private String signerRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SignatureStatus status = SignatureStatus.PENDING;

    @Column(columnDefinition = "text")
    private String signatureData;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    private Instant signedAt;

    private Instant sentAt;

    private Instant expiresAt;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected DocumentSignature() {}

    public DocumentSignature(
        UUID companyId,
        UUID documentId,
        String signerName,
        String signerEmail,
        String signerRole
    ) {
        this.companyId = companyId;
        this.documentId = documentId;
        this.signerName = signerName;
        this.signerEmail = signerEmail;
        this.signerRole = signerRole;
    }

    public UUID getId() { return id; }
    public UUID getCompanyId() { return companyId; }
    public UUID getDocumentId() { return documentId; }
    public String getSignerName() { return signerName; }
    public String getSignerEmail() { return signerEmail; }
    public String getSignerRole() { return signerRole; }
    public SignatureStatus getStatus() { return status; }
    public String getSignatureData() { return signatureData; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Instant getSignedAt() { return signedAt; }
    public Instant getSentAt() { return sentAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }

    public void setStatus(SignatureStatus status) { this.status = status; }
    public void setSignatureData(String signatureData) { this.signatureData = signatureData; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public void setSignedAt(Instant signedAt) { this.signedAt = signedAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
