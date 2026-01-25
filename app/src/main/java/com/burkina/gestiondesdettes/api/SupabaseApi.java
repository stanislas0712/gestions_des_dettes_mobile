package com.burkina.gestiondesdettes.api;

import com.burkina.gestiondesdettes.models.Client;
import com.burkina.gestiondesdettes.models.Dette;
import com.burkina.gestiondesdettes.models.Paiement;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface SupabaseApi {
    // --- Pour les Clients ---
    @GET("clients")
    Call<List<Client>> getClients();
    @POST("clients")
    Call<List<Client>> createClient(@Body Client client);

    //Dettes
    @GET("dettes")
    Call<List<Dette>> getDettes();
    @POST("dettes")
    Call<List<Dette>> createDette(@Body Dette dette);
    @PATCH("dettes/{id}")
    Call<Void> updateDette(@Query("id") String id, @Body Dette dette);

    @GET("paiements")
    Call<List<Paiement>> getPaiements();
    @POST("paiements")
    Call<List<Paiement>> createPaiement(@Body Paiement paiement);
    @PATCH("paiements/{id}")
    Call<Void> updatePaiement(@Query("id") String id, @Body Paiement paiement);


    Call<List<Paiement>> getPaiementsByDette(String s);
}