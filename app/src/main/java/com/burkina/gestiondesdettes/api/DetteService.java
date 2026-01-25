package com.burkina.gestiondesdettes.api;

import com.burkina.gestiondesdettes.models.Dette;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class DetteService {
    private SupabaseApi api;

    public DetteService() {
        api = SupabaseClient.getClient().create(SupabaseApi.class);
    }

    // Récupérer toutes les dettes d'un client spécifique
    public void getDettesParClient(String clientId, Callback<List<Dette>> callback) {
        String filter = "eq." + clientId;
        api.getDettesByClient(filter).enqueue(callback);
    }

    // Enregistrer une nouvelle dette
    public void creerDette(Dette dette, Callback<List<Dette>> callback) {
        api.createDette(dette).enqueue(callback);
    }

    // Mettre à jour une dette (ex: après un paiement)
    public void mettreAJourDette(String id, Dette dette, Callback<Void> callback) {
        String filter = "eq." + id;
        api.updateDette(filter, dette).enqueue(callback);
    }
}