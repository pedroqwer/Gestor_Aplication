package Aplicacion.api.gestor_aplication.Presupuesto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.io.IOException;

import Aplicacion.api.Entity.ActualizarPresupuestoEntity;
import Aplicacion.api.Entity.PresupuestoEntity;
import Aplicacion.api.Interfaz.IControllers;
import Aplicacion.api.ResponseEntity.MensajeResponse;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Funcionamiento.Notificaciones;
import Aplicacion.api.gestor_aplication.Movimiento.CrearMovimiento;
import Aplicacion.api.gestor_aplication.Movimiento.Movimineto_Seleccionado;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarPresupuesto extends AppCompatActivity {
    private long preid = -1;
    private String token;
    EditText cantidad,descripción;
    Button volver, Guarfar;
    private static final String TAG = "ActualizarPresupuesto";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_presupuesto);

        Inicializar();
        recibirDatos();
        Preferencias();
        volver.setOnClickListener(v -> {
            finish();
        });
        Guarfar.setOnClickListener(v -> {
            if (validar()) {
                ActualizarDatos();
            }
        });
    }
    private boolean validar() {
        boolean isValid = true;

        String cantidadStr = cantidad.getText().toString().trim();
        String descripcionStr = descripción.getText().toString().trim();

        if (cantidadStr.isEmpty()) {
            cantidad.setError("La cantidad es obligatoria");
            isValid = false;
        } else {
            try {
                double valor = Double.parseDouble(cantidadStr);
                if (valor <= 0) {
                    cantidad.setError("Debe ser un número mayor a cero");
                    isValid = false;
                } else {
                    cantidad.setError(null);
                }
            } catch (NumberFormatException e) {
                cantidad.setError("Ingrese un número válido");
                isValid = false;
            }
        }

        if (descripcionStr.isEmpty()) {
            descripción.setError("La descripción es obligatoria");
            isValid = false;
        } else if (descripcionStr.length() < 5) {
            descripción.setError("Debe tener al menos 5 caracteres");
            isValid = false;
        } else {
            descripción.setError(null);
        }

        return isValid;
    }
    private void ActualizarDatos() {
        String newCantidadStr = cantidad.getText().toString();
        String newDescripcion = descripción.getText().toString();

        Double newCantidad;
        try {
            newCantidad = Double.parseDouble(newCantidadStr);
        } catch (NumberFormatException e) {
            CustomToast.show(this, "Cantidad inválida debe ser sin decimales");
            return;
        }

        // Agregar el log para ver los datos nuevos antes de enviarlos
        Log.d(TAG, "Datos nuevos: Cantidad = " + newCantidad + ", Descripción = " + newDescripcion);

        ActualizarPresupuestoEntity actualizarPresupuestoEntity = new ActualizarPresupuestoEntity(newCantidad, newDescripcion);

        IControllers iControllers = RetrofitClient.getAPI();
        Call<MensajeResponse> call = iControllers.modificarPresupuesto("Bearer " + token, preid, actualizarPresupuestoEntity);

        call.enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(Call<MensajeResponse> call, Response<MensajeResponse> response) {
                if (!response.isSuccessful()) {
                    ResponseBody errorBody = response.errorBody();
                    try {
                        String errorResponse = errorBody != null ? errorBody.string() : "Error desconocido";
                        Log.e("API", "Error al actualizar presupuesto: " + response.message());
                        CustomToast.show(ActualizarPresupuesto.this, "Error: " + errorResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomToast.show(ActualizarPresupuesto.this, "Presupuesto actualizado " + response.message());
                    Notificaciones.mostrarNotificacionSimple(ActualizarPresupuesto.this, "Actualización de presupuesto", "¡Actualización!");

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("nuevaCantidad", newCantidad);
                    resultIntent.putExtra("nuevaDescripcion", newDescripcion);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable throwable) {
                Log.e("ActualizarPresupuesto", "Fallo la petición: ", throwable);
                CustomToast.show(ActualizarPresupuesto.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void recibirDatos() {
        String descripcionRecibida = getIntent().getStringExtra("descripcion");
        String cantidadRecibida = getIntent().getStringExtra("cantidad");
        preid = getIntent().getLongExtra("idPre", -1);

        if (descripcionRecibida != null) {
            descripción.setText(descripcionRecibida);
        }

        if (cantidadRecibida != null) {
            cantidad.setText(cantidadRecibida);
        }
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
    }

    private void Inicializar() {
        cantidad = findViewById(R.id.etCantidadLimite);
        descripción = findViewById(R.id.etDescripcion);
        volver = findViewById(R.id.btnCancelar);
        Guarfar = findViewById(R.id.btnActualizar);
    }
}