package com.burkina.gestiondesdettes.models;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class Client {
    @SerializedName("id")
    private String id;
    @SerializedName("utilisateur_id")
    private String utilisateurId;
    @SerializedName("nom")
    private String nom;
    @SerializedName("telephone")
    private String telephone;
    @SerializedName("adresse")
    private String adresse;
    @SerializedName("solde_total")
    private double soldeTotal;
    @SerializedName("created_at")
    private String createdAt;

    // Constructeurs
    public Client() {
        this.id = UUID.randomUUID().toString();
    }

    public Client(String nom, String telephone, String adresse, String utilisateurId) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.telephone = telephone;
        this.adresse = adresse;
        this.utilisateurId = utilisateurId;
        this.soldeTotal = 0.0;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getutilisateurId() {
        return utilisateurId;
    }

    public void setutilisateurId(String utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public double getSoldeTotal() {
        return soldeTotal;
    }

    public void setSoldeTotal(double soldeTotal) {
        this.soldeTotal = soldeTotal;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Client{" +
                "nom='" + nom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", soldeTotal=" + soldeTotal +
                '}';
    }
}