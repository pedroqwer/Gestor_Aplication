package Aplicacion.api.gestor_aplication.Admin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.gestor_aplication.R;

import java.util.List;

import Aplicacion.api.Entity.User;
import Aplicacion.api.ResponseEntity.UserListResponse;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class USerSelected extends AppCompatActivity {
    private long id = -1;
    private String token;
    TextView telefono, nombre, apellido, email, dni, username, password, viewId;
    Button btnVolver;
    private static final String TAG = "USerSelected";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selected);

        inicializar();
        recibirPost();
        Preferencias();

        if (id != -1 && token != null) {
            buscarUsuarioEnLista();
        } else {
            Log.e(TAG, "Datos insuficientes - ID: " + id + ", Token: " + (token != null ? "disponible" : "null"));
            CustomToast.show(this,"Error: No se pudo cargar la información del usuario");
            finish();
        }

        btnVolver.setOnClickListener(view -> finish());
    }

    private void buscarUsuarioEnLista() {
        RetrofitClient.getAPI().getUserList("Bearer " + token).enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body().getData();

                    for (User user : users) {
                        if (user.getId() == id) {
                            actualizarUI(user);
                            return;
                        }
                    }
                    CustomToast.show(USerSelected.this,response.message().toString());
                    //Toast.makeText(USerSelected.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(USerSelected.this, "Error al obtener la lista de usuarios", Toast.LENGTH_SHORT).show();
                    CustomToast.show(USerSelected.this,response.message().toString());
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
                CustomToast.show(USerSelected.this, "No se pudo conectar con el servidor");

            }
        });
    }

    private void actualizarUI(User user) {
        runOnUiThread(() -> {
            nombre.setText("Nombre: " + user.getNombre());
            apellido.setText("Apellido: " + user.getApellido());
            email.setText("Correo: " + user.getEmail());
            dni.setText("DNI: " + user.getDni());
            telefono.setText("Teléfono: " + user.getTelefono());
            username.setText("Usuario: " + user.getUsername());
            password.setText("Contraseña: " + user.getPassword());
            viewId.setText("ID: " + user.getId());
        });
    }

    private void inicializar() {
        nombre = findViewById(R.id.viewNombre);
        apellido = findViewById(R.id.viewApellido);
        email = findViewById(R.id.viewEmail);
        dni = findViewById(R.id.viewDni);
        telefono = findViewById(R.id.viewTelefono);
        username = findViewById(R.id.viewUsername);
        password = findViewById(R.id.viewPassword);
        viewId = findViewById(R.id.viewId);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void recibirPost() {
        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);
        Log.d(TAG, "ID recibido: " + id);
    }

    private void Preferencias() {
        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("jwt_token", null);
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token no disponible");
            CustomToast.show(this, "Error: Sesión no válida");
            //Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d(TAG, "Token obtenido correctamente");
        }
    }
}
