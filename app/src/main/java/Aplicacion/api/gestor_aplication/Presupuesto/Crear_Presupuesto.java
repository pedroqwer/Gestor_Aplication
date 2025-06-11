package Aplicacion.api.gestor_aplication.Presupuesto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Aplicacion.api.Entity.CreatePresupuesto;
import Aplicacion.api.Interfaz.IControllers;
import Aplicacion.api.ResponseEntity.ResponseEntity;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Funcionamiento.Notificaciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Crear_Presupuesto extends AppCompatActivity {
    private EditText etCantidadLimite, etDescripcion;
    private Button btnGuardar, btnCancelar;
    long userId;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_presupuesto);

        Inicializar();
        Preferencias();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validar()) {
                    guardarPresupuesto();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the fields or go back
                etCantidadLimite.setText("");
                etDescripcion.setText("");
                finish();
            }
        });
    }
    private boolean validar() {
        boolean isValid = true;

        String cantidadStr = etCantidadLimite.getText().toString().trim();
        String descripcionStr = etDescripcion.getText().toString().trim();

        if (cantidadStr.isEmpty()) {
            etCantidadLimite.setError("La cantidad es obligatoria");
            isValid = false;
        } else {
            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad <= 0) {
                    etCantidadLimite.setError("Debe ser un número mayor que cero");
                    isValid = false;
                } else {
                    etCantidadLimite.setError(null);
                }
            } catch (NumberFormatException e) {
                etCantidadLimite.setError("Ingrese un número válido");
                isValid = false;
            }
        }

        if (descripcionStr.isEmpty()) {
            etDescripcion.setError("La descripción es obligatoria");
            isValid = false;
        } else if (descripcionStr.length() < 5) {
            etDescripcion.setError("Debe tener al menos 5 caracteres");
            isValid = false;
        } else {
            etDescripcion.setError(null);
        }

        return isValid;
    }


    private void guardarPresupuesto() {
        String cantidadLimiteStr = etCantidadLimite.getText().toString();
        String descripcion = etDescripcion.getText().toString();

        if (cantidadLimiteStr.isEmpty() || descripcion.isEmpty()) {
            CustomToast.show(Crear_Presupuesto.this, "Por favor, rellene todos los campos.");
            return;
        }

        Double cantidadLimite = Double.parseDouble(cantidadLimiteStr);

        String mesActual = new SimpleDateFormat("MMMM", new Locale("es", "ES")).format(new Date());
        mesActual = mesActual.substring(0, 1).toUpperCase() + mesActual.substring(1); // Primera letra en mayúscula

        CreatePresupuesto presupuesto = new CreatePresupuesto(cantidadLimite, descripcion, mesActual, userId);

        Toast.makeText(this, mesActual, Toast.LENGTH_SHORT).show();
        IControllers iControllers = RetrofitClient.getAPI();
        Call<ResponseEntity> call = iControllers.crearPresupuesto("Bearer " + token, presupuesto);

        call.enqueue(new Callback<ResponseEntity>() {
            @Override
            public void onResponse(Call<ResponseEntity> call, Response<ResponseEntity> response) {
                if (response.isSuccessful()) {
                    // Asegúrate de que la respuesta no esté vacía
                    if (response.body() != null) {
                        CustomToast.show(Crear_Presupuesto.this, "Presupuesto registrado con éxito");
                        Notificaciones.mostrarNotificacionSimple(Crear_Presupuesto.this, "Presupuesto creado", "¡Creación!");
                        finish();
                    } else {
                        CustomToast.show(Crear_Presupuesto.this, "Respuesta vacía del servidor");
                        Log.e("CrearPresupuesto", "Respuesta vacía del servidor");
                    }
                } else {
                    // Si la respuesta no es exitosa, mostrar el código de error
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "Respuesta sin cuerpo";
                        Log.e("CrearPresupuesto", "Error al registrar presupuesto: " + errorResponse);
                        CustomToast.show(Crear_Presupuesto.this, "Error al registrar presupuesto cantidaad invalida o ya dispone de presupuesto ");
                    } catch (IOException e) {
                        Log.e("CrearPresupuesto", "Error al leer el error del servidor", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseEntity> call, Throwable throwable) {
                // Asegúrate de manejar correctamente el caso de error
                CustomToast.show(Crear_Presupuesto.this, "No se pudo conectar con el servidor");
                Log.e("CrearPresupuesto", "Fallo la petición: ", throwable);
            }
        });
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
    private void Inicializar() {
        etCantidadLimite = findViewById(R.id.etCantidadLimite);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
    }
}