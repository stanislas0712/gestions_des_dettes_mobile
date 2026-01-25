package com.burkina.gestiondesdettes.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.UUID;

public class Dette {
    @SerializedName("id")
    private String id;
    @SerializedName("client_id")
    private String clientId;
    @SerializedName("description")
    private String description;
    @SerializedName("monatnt")
    private double montant;
    @SerializedName("date_dette")
    private String dateDette;
    @SerializedName("solde_restant")
    private double soldeRestant;
    @SerializedName("statut")
    private String statut;
    @SerializedName("created_at")
    private String createdAt;


    // Constructeurs
    public Dette() {
        this.id = UUID.randomUUID().toString();
        this.statut = "en_cours";
    }

    public Dette(String clientId, String description, double montant, String dateDette) {
        this.id = UUID.randomUUID().toString();
        this.clientId = clientId;
        this.description = description;
        this.montant = montant;
        this.dateDette = dateDette;
        this.soldeRestant = montant;
        this.statut = "en_cours";
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getDateDette() {
        return dateDette;
    }

    public void setDateDette(String dateDette) {
        this.dateDette = dateDette;
    }

    public double getSoldeRestant() {
        return soldeRestant;
    }

    public void setSoldeRestant(double soldeRestant) {
        this.soldeRestant = soldeRestant;
        // Mettre à jour le statut automatiquement
        if (this.soldeRestant <= 0) {
            this.statut = "payé";
        }
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPayee() {
        return "payé".equals(statut) || soldeRestant <= 0;
    }

    @Override
    public String toString() {
        return "Dette{" +
                "description='" + description + '\'' +
                ", montant=" + montant +
                ", soldeRestant=" + soldeRestant +
                ", statut='" + statut + '\'' +
                '}';
    }
}