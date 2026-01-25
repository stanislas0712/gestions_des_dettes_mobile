package com.burkina.gestiondesdettes.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.burkina.gestiondesdettes.R;
import com.burkina.gestiondesdettes.api.ClientService;
import com.burkina.gestiondesdettes.models.Client;
import com.burkina.gestiondesdettes.utils.SessionManager;
import com.burkina.gestiondesdettes.utils.ValidationUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditClientActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ClientService clientService;
    private ExecutorService executor;

    // UI Components
    private EditText etNom, etTelephone, etAdresse;
    private Button btnSave;
    private ProgressBar progressBar;

    // Mode édition
    private boolean isEditMode = false;
    private String clientId;
    private Client currentClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        // Initialiser
        sessionManager = new SessionManager(this);
        clientService = new ClientService();
        executor = Executors.newSingleThreadExecutor();

        // Vérifier si mode édition
        if (getIntent().hasExtra("client_id")) {
            isEditMode = true;
            clientId = getIntent().getStringExtra("client_id");
        }

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "Modifier Client" : "Nouveau Client");
        }

        initViews();

        if (isEditMode) {
            loadClientData();
        }
    }

    private void initViews() {
        // Adapter aux IDs de votre layout existant
        etNom = findViewById(R.id.edit_nom);
        etTelephone = findViewById(R.id.edit_telephone);
        etAdresse = findViewById(R.id.edit_adresse);
        btnSave = findViewById(R.id.btn_save_client);
        progressBar = findViewById(R.id.progressBar);

        btnSave.setOnClickListener(v -> saveClient());
    }

    private void loadClientData() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        executor.execute(() -> {
            try {
                Client client = clientService.getClientById(clientId);

                runOnUiThread(() -> {
                    if (client != null) {
                        currentClient = client;
                        etNom.setText(client.getNom());
                        etTelephone.setText(client.getTelephone());
                        etAdresse.setText(client.getAdresse());
                    } else {
                        Toast.makeText(this, "Erreur de chargement",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                });
            }
        });
    }

    private void saveClient() {
        // Récupérer les valeurs
        String nom = etNom.getText().toString().trim();
        String telephone = etTelephone.getText().toString().trim();
        String adresse = etAdresse.getText().toString().trim();

        // Validation
        if (!validateInputs(nom, telephone, adresse)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        executor.execute(() -> {
            try {
                boolean success;

                if (isEditMode) {
                    // Mise à jour
                    currentClient.setNom(nom);
                    currentClient.setTelephone(telephone);
                    currentClient.setAdresse(adresse);
                    success = clientService.updateClient(currentClient);
                } else {
                    // Création
                    String userId = sessionManager.getUserId();
                    Client newClient = new Client(nom, telephone, adresse, userId);
                    Client result = clientService.addClient(newClient);
                    success = (result != null);
                }

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);

                    if (success) {
                        Toast.makeText(this,
                                isEditMode ? "Client modifié" : "Client ajouté",
                                Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Erreur lors de l'enregistrement",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(this, "Erreur: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean validateInputs(String nom, String telephone, String adresse) {
        // Valider le nom
        String nomError = ValidationUtils.getNameError(nom);
        if (nomError != null) {
            etNom.setError(nomError);
            etNom.requestFocus();
            return false;
        }

        // Valider le téléphone
        String phoneError = ValidationUtils.getPhoneError(telephone);
        if (phoneError != null) {
            etTelephone.setError(phoneError);
            etTelephone.requestFocus();
            return false;
        }

        // L'adresse est optionnelle, mais si fournie, la valider
        if (!ValidationUtils.isValidAddress(adresse)) {
            etAdresse.setError("L'adresse doit contenir au moins 5 caractères");
            etAdresse.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}