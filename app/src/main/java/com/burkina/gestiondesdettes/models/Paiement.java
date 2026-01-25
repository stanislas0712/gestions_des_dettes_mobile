package com.burkina.gestiondesdettes.models;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Paiement {
    @SerializedName("id")
    private String id;
    @SerializedName("dette_id")
    private String detteId;
    @SerializedName("montant")
    private double montant;
    @SerializedName("date_paiement")
    private String datePaiement;
    @SerializedName("created_at")
    private String createdAt;

    // Constructeurs
    public Paiement() {
        this.id = UUID.randomUUID().toString();
    }

    public Paiement(String detteId, double montant, String datePaiement) {
        this.id = UUID.randomUUID().toString();
        this.detteId = detteId;
        this.montant = montant;
        this.datePaiement = datePaiement;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetteId() {
        return detteId;
    }

    public void setDetteId(String detteId) {
        this.detteId = detteId;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(String datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "montant=" + montant +
                ", datePaiement='" + datePaiement + '\'' +
                '}';
    }
}