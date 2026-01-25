package com.burkina.gestiondesdettes.api;

import com.burkina.gestiondesdettes.models.Client;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public class ClientService {
    private SupabaseApi supabaseApi;

    public ClientService() {
        api = SupabaseClient.getClient().create(SupabaseApi.class);
    }
    public void chargerTousLesClients(Callback<List<Client>> callback) {
        api.getClients()enqueue(callback);
    }

}
