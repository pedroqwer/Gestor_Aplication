package Aplicacion.api.gestor_aplication.Userr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.util.List;

import Aplicacion.api.Entity.User;
import Aplicacion.api.ResponseEntity.UserListResponse;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Perfil extends AppCompatActivity {
    TextView tvNombre, tvApellido, tvDni, tvEmail, tvTelefono, tvUsuario, tvContrasena;
    Button btnEditar, btnVolver;
    private String token;
    long userId;
    private static final String TAG = "Perfil";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Inicializar();
        Preferencias();

        if (userId != -1 && token != null) {
            buscarUsuarioEnLista();
        } else {
            Log.e(TAG, "Datos insuficientes - ID: " + userId + ", Token: " + (token != null ? "disponible" : "null"));
            CustomToast.show(this,"Error: No se pudo cargar la información del usuario");
            finish();
        }


        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, PerfilActualizar.class);
                intent.putExtra("nombre", tvNombre.getText().toString().replace("Nombre: ", ""));
                intent.putExtra("apellido", tvApellido.getText().toString().replace("Apellido: ", ""));
                intent.putExtra("dni", tvDni.getText().toString().replace("DNI: ", ""));
                intent.putExtra("email", tvEmail.getText().toString().replace("Email: ", ""));
                intent.putExtra("telefono", tvTelefono.getText().toString().replace("Teléfono: ", ""));
                //intent.putExtra("usuario", tvUsuario.getText().toString().replace("Usuario: ", ""));
                //intent.putExtra("contrasena", "********");
                startActivityForResult(intent, 200);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void buscarUsuarioEnLista() {
        RetrofitClient.getAPI().getUserList("Bearer " + token).enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body().getData();

                    for (User user : users) {
                        if (user.getId() == userId) {
                            actualizarUI(user);
                            return;
                        }
                    }
                    CustomToast.show(Perfil.this,response.message().toString());
                    //Toast.makeText(USerSelected.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(USerSelected.this, "Error al obtener la lista de usuarios", Toast.LENGTH_SHORT).show();
                    CustomToast.show(Perfil.this,response.message().toString());
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
                CustomToast.show(Perfil.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void actualizarUI(User user) {
        tvNombre.setText("Nombre: " + user.getNombre());
        tvApellido.setText("Apellido: " + user.getApellido());
        tvDni.setText("DNI: " + user.getDni());
        tvEmail.setText("Email: " + user.getEmail());
        tvTelefono.setText("Teléfono: " + user.getTelefono());
        tvUsuario.setText("Usuario: " + user.getUsername());
        tvContrasena.setText("Contraseña: ********"); // Nunca muestres contraseñas reales
    }

    private void Inicializar() {
        tvNombre = findViewById(R.id.tvNombre);
        tvApellido = findViewById(R.id.tvApellido);
        tvDni = findViewById(R.id.tvDni);
        tvEmail = findViewById(R.id.tvEmail);
        tvTelefono = findViewById(R.id.tvTelefono);
        tvUsuario = findViewById(R.id.tvUsuario);
        tvContrasena = findViewById(R.id.tvContrasena);

        btnEditar = findViewById(R.id.btnEditar);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void Preferencias() {
        userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("user_id", -1);
        if (userId != -1) {
            Log.d("Historial", "ID de usuario obtenido: " + userId);
        } else {
            Log.d("Historial", "No se encontró un ID de usuario en SharedPreferences");
        }

        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("jwt_token", null);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("ListUsers", "Token cargado desde SharedPreferences: " + token);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            String nuevoNombre = data.getStringExtra("nuevoNombre");
            String nuevoApellido = data.getStringExtra("nuevoApellido");
            String nuevoDni = data.getStringExtra("nuevoDni");
            String nuevoEmail = data.getStringExtra("nuevoEmail");
            String nuevoTelefono = data.getStringExtra("nuevoTelefono");
            String nuevoUsuario = data.getStringExtra("nuevoUsuario");
            String nuevaContrasena = data.getStringExtra("nuevaContrasena");

            // Actualiza los TextView con los nuevos datos
            tvNombre.setText("Nombre: " + nuevoNombre);
            tvApellido.setText("Apellido: " + nuevoApellido);
            tvDni.setText("DNI: " + nuevoDni);
            tvEmail.setText("Email: " + nuevoEmail);
            tvTelefono.setText("Teléfono: " + nuevoTelefono);
        }
    }

}