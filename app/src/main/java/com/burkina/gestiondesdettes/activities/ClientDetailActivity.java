package com.burkina.gestiondesdettes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.burkina.gestiondesdettes.R;
import com.burkina.gestiondesdettes.adapters.DetteAdapter;
import com.burkina.gestiondesdettes.api.ClientService;
import com.burkina.gestiondesdettes.api.DetteService;
import com.burkina.gestiondesdettes.models.Client;
import com.burkina.gestiondesdettes.models.Dette;
import com.burkina.gestiondesdettes.utils.CurrencyUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientDetailActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_DETTE = 1;
    private static final int REQUEST_ADD_PAIEMENT = 2;

    private ClientService clientService;
    private DetteService detteService;
    private ExecutorService executor;

    // UI Components
    private TextView tvClientNom, tvClientTelephone, tvClientAdresse;
    private TextView tvSoldeTotal, tvNombreDettes;
    private RecyclerView rvDettes;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddDette;

    private String clientId;
    private Client currentClient;
    private DetteAdapter detteAdapter;
    private List<Dette> dettesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);

        // Récupérer l'ID du client
        clientId = getIntent().getStringExtra("client_id");
        String clientNom = getIntent().getStringExtra("client_nom");

        if (clientId == null) {
            Toast.makeText(this, "Erreur: Client introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(clientNom != null ? clientNom : "Détails Client");
        }

        // Initialiser
        clientService = new ClientService();
        detteService = new DetteService();
        executor = Executors.newSingleThreadExecutor();

        initViews();
        setupRecyclerView();
        loadClientData();
    }

    private void initViews() {
        tvClientNom = findViewById(R.id.tvClientNom);
        tvClientTelephone = findViewById(R.id.tvClientTelephone);
        tvClientAdresse = findViewById(R.id.tvClientAdresse);
        tvSoldeTotal = findViewById(R.id.tvSoldeTotal);
        tvNombreDettes = findViewById(R.id.tvNombreDettes);
        rvDettes = findViewById(R.id.rvDettes);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabAddDette = findViewById(R.id.fabAddDette);

        // SwipeRefresh
        swipeRefresh.setOnRefreshListener(this::loadClientData);

        // FAB Add Dette
        fabAddDette.setOnClickListener(v -> {
            Intent intent = new Intent(ClientDetailActivity.this, AddDetteActivity.class);
            intent.putExtra("client_id", clientId);
            intent.putExtra("client_nom", currentClient != null ? currentClient.getNom() : "");
            startActivityForResult(intent, REQUEST_ADD_DETTE);
        });
    }

    private void setupRecyclerView() {
        dettesList = new ArrayList<>();

        detteAdapter = new DetteAdapter(dettesList, new DetteAdapter.OnDetteClickListener() {
            @Override
            public void onDetteClick(Dette dette) {
                // Ouvrir l'écran de paiement
                if (!dette.isPayee()) {
                    Intent intent = new Intent(ClientDetailActivity.this, AddPaiementActivity.class);
                    intent.putExtra("dette_id", dette.getId());
                    intent.putExtra("dette_description", dette.getDescription());
                    intent.putExtra("solde_restant", dette.getSoldeRestant());
                    startActivityForResult(intent, REQUEST_ADD_PAIEMENT);
                } else {
                    Toast.makeText(ClientDetailActivity.this,
                            "Cette dette est déjà payée", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDetteLongClick(Dette dette) {
                showDetteOptionsDialog(dette);
            }
        });

        rvDettes.setLayoutManager(new LinearLayoutManager(this));
        rvDettes.setAdapter(detteAdapter);
    }

    private void loadClientData() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                // Charger les infos du client
                Client client = clientService.getClientById(clientId);

                // Charger les dettes
                List<Dette> dettes = detteService.getDettesByClient(clientId);

                runOnUiThread(() -> {
                    if (client != null) {
                        currentClient = client;
                        displayClientInfo(client);
                        displayDettes(dettes);
                    } else {
                        Toast.makeText(this, "Client introuvable", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                });
            }
        });
    }

    private void displayClientInfo(Client client) {
        tvClientNom.setText(client.getNom());
        tvClientTelephone.setText(client.getTelephone());

        if (client.getAdresse() != null && !client.getAdresse().isEmpty()) {
            tvClientAdresse.setText(client.getAdresse());
            tvClientAdresse.setVisibility(View.VISIBLE);
        } else {
            tvClientAdresse.setVisibility(View.GONE);
        }

        tvSoldeTotal.setText(CurrencyUtils.format(client.getSoldeTotal()));

        // Changer la couleur selon le solde
        if (client.getSoldeTotal() > 0) {
            tvSoldeTotal.setTextColor(getColor(R.color.error));
        } else {
            tvSoldeTotal.setTextColor(getColor(R.color.success));
        }
    }

    private void displayDettes(List<Dette> dettes) {
        dettesList.clear();
        dettesList.addAll(dettes);
        detteAdapter.notifyDataSetChanged();

        // Nombre de dettes
        int nombreDettesEnCours = 0;
        for (Dette d : dettes) {
            if (!d.isPayee()) {
                nombreDettesEnCours++;
            }
        }
        tvNombreDettes.setText(nombreDettesEnCours + " dette(s) en cours");

        // État vide
        if (dettes.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvDettes.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvDettes.setVisibility(View.VISIBLE);
        }
    }

    private void showDetteOptionsDialog(Dette dette) {
        String[] options;
        if (dette.isPayee()) {
            options = new String[]{"Voir détails", "Supprimer"};
        } else {
            options = new String[]{"Ajouter paiement", "Voir détails", "Supprimer"};
        }

        new AlertDialog.Builder(this)
                .setTitle(dette.getDescription())
                .setItems(options, (dialog, which) -> {
                    if (dette.isPayee()) {
                        // Dette payée: Voir détails ou Supprimer
                        if (which == 0) {
                            showDetteDetails(dette);
                        } else if (which == 1) {
                            confirmDeleteDette(dette);
                        }
                    } else {
                        // Dette en cours: Ajouter paiement, Voir détails ou Supprimer
                        if (which == 0) {
                            // Ajouter paiement
                            Intent intent = new Intent(this, AddPaiementActivity.class);
                            intent.putExtra("dette_id", dette.getId());
                            intent.putExtra("dette_description", dette.getDescription());
                            intent.putExtra("solde_restant", dette.getSoldeRestant());
                            startActivityForResult(intent, REQUEST_ADD_PAIEMENT);
                        } else if (which == 1) {
                            showDetteDetails(dette);
                        } else if (which == 2) {
                            confirmDeleteDette(dette);
                        }
                    }
                })
                .show();
    }

    private void showDetteDetails(Dette dette) {
        String message = "Description: " + dette.getDescription() + "\n" +
                "Montant initial: " + CurrencyUtils.format(dette.getMontant()) + "\n" +
                "Solde restant: " + CurrencyUtils.format(dette.getSoldeRestant()) + "\n" +
                "Date: " + dette.getDateDette() + "\n" +
                "Statut: " + (dette.isPayee() ? "Payé" : "En cours");

        new AlertDialog.Builder(this)
                .setTitle("Détails de la dette")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void confirmDeleteDette(Dette dette) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la dette")
                .setMessage("Voulez-vous vraiment supprimer cette dette ?\n\n" +
                        dette.getDescription())
                .setPositiveButton("Supprimer", (dialog, which) -> deleteDette(dette))
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void deleteDette(Dette dette) {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                boolean success = detteService.deleteDette(dette.getId());

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (success) {
                        Toast.makeText(this, "Dette supprimée", Toast.LENGTH_SHORT).show();
                        loadClientData(); // Recharger
                    } else {
                        Toast.makeText(this, "Erreur lors de la suppression",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Erreur: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_client_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_edit_client) {
            // Modifier le client
            Intent intent = new Intent(this, AddEditClientActivity.class);
            intent.putExtra("client_id", clientId);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_delete_client) {
            confirmDeleteClient();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteClient() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le client")
                .setMessage("Voulez-vous vraiment supprimer ce client et toutes ses dettes ?")
                .setPositiveButton("Supprimer", (dialog, which) -> deleteClient())
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void deleteClient() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                boolean success = clientService.deleteClient(clientId);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (success) {
                        Toast.makeText(this, "Client supprimé", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Erreur lors de la suppression",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Erreur: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Recharger après ajout de dette ou paiement
            loadClientData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClientData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}