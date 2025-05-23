package Aplicacion.api.gestor_aplication.Userr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.io.IOException;

import Aplicacion.api.Entity.ActualizarUser;
import Aplicacion.api.Interfaz.IControllers;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Funcionamiento.Notificaciones;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActualizar extends AppCompatActivity {
    EditText etNombre, etApellido, etDni, etEmail, etTelefono, etUsuario, etContrasena;
    Button btnGuardarCambios, btnCancelar;
    private long id = -1;
    private String token;
    private static final String TAG = "PerfilActualizar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_actualizar);

        Inicializar();
        recibirDatos();
        Preferencias();

        btnCancelar.setOnClickListener(v -> finish());

        btnGuardarCambios.setOnClickListener(v -> {
            if (validar()) {
                ActualizarDatos();
            }
        });
    }

    private boolean validar() {
        boolean isValid = true;

        String dni = etDni.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            isValid = false;
        } else {
            etNombre.setError(null);
        }

        if (etApellido.getText().toString().trim().isEmpty()) {
            etApellido.setError("El apellido es obligatorio");
            isValid = false;
        } else {
            etApellido.setError(null);
        }

        if (dni.isEmpty()) {
            etDni.setError("El DNI es obligatorio");
            isValid = false;
        } else if (!dni.matches("^\\d{8}[A-Za-z]$")) {
            etDni.setError("DNI inválido (debe tener 8 dígitos y 1 letra)");
            isValid = false;
        } else {
            etDni.setError(null);
        }


        if (email.isEmpty()) {
            etEmail.setError("El correo es obligatorio");
            isValid = false;
        } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            etEmail.setError("Correo electrónico inválido");
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        if (telefono.isEmpty()) {
            etTelefono.setError("El teléfono es obligatorio");
            isValid = false;
        } else if (!telefono.matches("^\\d{9,15}$")) {
            etTelefono.setError("Teléfono inválido (debe tener entre 9 y 15 dígitos)");
            isValid = false;
        } else {
            etTelefono.setError(null);
        }

        return isValid;
    }

    private void ActualizarDatos() {
        String nuevoNombre = etNombre.getText().toString().trim();
        String nuevoApellido = etApellido.getText().toString().trim();
        String nuevoDni = etDni.getText().toString().trim();
        String nuevoEmail = etEmail.getText().toString().trim();
        String nuevoTelefono = etTelefono.getText().toString().trim();
        //String nuevoUsuario = etUsuario.getText().toString().trim();
        //String nuevaContrasena = etContrasena.getText().toString().trim();

        ActualizarUser actualizarUser = new ActualizarUser(nuevoNombre, nuevoApellido,nuevoTelefono, nuevoEmail, nuevoDni);

        IControllers iControllers = RetrofitClient.getAPI();
        Call<ResponseBody> call = iControllers.modificarPerfil("Bearer " + token, id, actualizarUser);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        Log.e("API", "Error al actualizar perfil: " + errorResponse);
                        CustomToast.show(PerfilActualizar.this, "Error: " + errorResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("API", "Error al leer el cuerpo del error: " + e.getMessage());
                    }
                } else {
                    try {
                        String message = response.body() != null ? response.body().string() : "Actualizado correctamente";
                        CustomToast.show(PerfilActualizar.this, "Perfil actualizado: " + message);
                        Notificaciones.mostrarNotificacionSimple(PerfilActualizar.this, "Perfil actualizado", "¡Actualización!");

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("nuevoNombre", nuevoNombre);
                        resultIntent.putExtra("nuevoApellido", nuevoApellido);
                        resultIntent.putExtra("nuevoDni", nuevoDni);
                        resultIntent.putExtra("nuevoEmail", nuevoEmail);
                        resultIntent.putExtra("nuevoTelefono", nuevoTelefono);
                        //resultIntent.putExtra("nuevoUsuario", nuevoUsuario);
                        //resultIntent.putExtra("nuevaContrasena", nuevaContrasena);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("API", "Error al leer el cuerpo de éxito: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                CustomToast.show(PerfilActualizar.this, "No se pudo conectar con el servidor");
                Log.e("PerfilActualizar", "Fallo la petición: ", throwable);
            }
        });
    }

    private void recibirDatos() {
        String nombre = getIntent().getStringExtra("nombre");
        String apellido = getIntent().getStringExtra("apellido");
        String dni = getIntent().getStringExtra("dni");
        String email = getIntent().getStringExtra("email");
        String telefono = getIntent().getStringExtra("telefono");

        etNombre.setText(nombre);
        etApellido.setText(apellido);
        etDni.setText(dni);
        etEmail.setText(email);
        etTelefono.setText(telefono);
        //etUsuario.setText(usuario);
        //etContrasena.setText("");
    }

    private void Preferencias() {
        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("jwt_token", null);
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token no disponible");
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d(TAG, "Token obtenido correctamente");
        }

        id = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("user_id", -1);
        if (id != -1) {
            Log.d("Historial", "ID de usuario obtenido: " + id);
        } else {
            Log.d("Historial", "No se encontró un ID de usuario en SharedPreferences");
        }
    }

    private void Inicializar() {
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etDni = findViewById(R.id.etDni);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        //etUsuario = findViewById(R.id.etUsuario);
        //etContrasena = findViewById(R.id.etContrasena);

        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnCancelar = findViewById(R.id.btnCancelar);
    }
}
