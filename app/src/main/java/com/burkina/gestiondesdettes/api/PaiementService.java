package com.burkina.gestiondesdettes.api;

import com.burkina.gestiondesdettes.models.Paiement;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class PaiementService {
    private SupabaseApi api;

    public PaiementService() {
        api = SupabaseClient.getClient().create(SupabaseApi.class);
    }

    // Récupérer l'historique des paiements pour une dette précise
    public void getHistorique(String detteId, Callback<List<Paiement>> callback) {
        api.getPaiementsByDette("eq." + detteId).enqueue(callback);
    }

    // Enregistrer un nouveau paiement
    public void enregistrerPaiement(Paiement paiement, Callback<List<Paiement>> callback) {
        api.createPaiement(paiement).enqueue(callback);
    }
}