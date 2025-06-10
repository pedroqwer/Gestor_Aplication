package Aplicacion.api.gestor_aplication.Presupuesto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.io.IOException;
import java.math.BigDecimal;

import Aplicacion.api.Entity.ActualizarPresupuestoEntity;
import Aplicacion.api.Interfaz.IControllers;
import Aplicacion.api.ResponseEntity.MensajeResponse;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Funcionamiento.Notificaciones;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarPresupuesto extends AppCompatActivity {
    private long preid = -1;
    private String token;
    private long userId;
    private BigDecimal saldoDisponible = BigDecimal.ZERO;
    EditText cantidad, descripción;
    Button volver, guardar;
    private static final String TAG = "ActualizarPresupuesto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_presupuesto);

        inicializar();
        recibirDatos();
        cargarPreferencias();

        volver.setOnClickListener(v -> finish());

        guardar.setOnClickListener(v -> {
            if (validar()) {
                actualizarDatos();
            }
        });
    }

    private boolean validar() {
        boolean isValid = true;

        String cantidadStr = cantidad.getText().toString().trim().replace(",", ".");
        String descripcionStr = descripción.getText().toString().trim();

        if (cantidadStr.isEmpty()) {
            cantidad.setError("La cantidad es obligatoria");
            isValid = false;
        } else {
            try {
                double valor = Double.parseDouble(cantidadStr);
                BigDecimal valorDecimal = new BigDecimal(String.valueOf(valor));

                if (valor <= 0) {
                    cantidad.setError("Debe ser un número mayor a cero");
                    isValid = false;
                } else if (saldoDisponible.compareTo(valorDecimal) < 0) {
                    cantidad.setError("La cantidad excede el saldo disponible");
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

    private void actualizarDatos() {
        String newCantidadStr = cantidad.getText().toString().trim().replace(",", ".");
        String newDescripcion = descripción.getText().toString().trim();

        Double newCantidad;
        try {
            newCantidad = Double.parseDouble(newCantidadStr);
        } catch (NumberFormatException e) {
            CustomToast.show(this, "Cantidad inválida");
            return;
        }

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
                        Log.e(TAG, "Error al actualizar presupuesto: " + response.message() + " - " + errorResponse);
                        CustomToast.show(ActualizarPresupuesto.this, "Error: " + errorResponse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomToast.show(ActualizarPresupuesto.this, "Presupuesto actualizado correctamente");
                    Notificaciones.mostrarNotificacionSimple(ActualizarPresupuesto.this, "Actualización de presupuesto", "¡Actualización!");

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("nuevaCantidad", newCantidad);
                    resultIntent.putExtra("nuevaDescripcion", newDescripcion);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<MensajeResponse> call, Throwable t) {
                Log.e(TAG, "Fallo la petición: ", t);
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

    private void cargarPreferencias() {
        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("jwt_token", null);
        userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("user_id", -1);

        if (token == null || token.isEmpty() || userId == -1) {
            Log.e(TAG, "Token o userId no disponible");
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d(TAG, "Token y userId obtenidos correctamente");
            consultarSaldo();
        }
    }

    private void consultarSaldo() {
        RetrofitClient.getAPI().consultarSaldo("Bearer " + token, userId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String respuesta = response.body().string();
                                String saldoStr = respuesta.replace("Saldo:", "").trim();
                                saldoDisponible = new BigDecimal(saldoStr);
                                Log.d(TAG, "Saldo disponible: " + saldoDisponible);
                            } catch (IOException e) {
                                Log.e(TAG, "Error al leer respuesta del saldo", e);
                            }
                        } else {
                            Log.e(TAG, "Error al consultar saldo: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Fallo en la llamada de saldo: " + t.getMessage());
                        CustomToast.show(ActualizarPresupuesto.this, "No se pudo obtener el saldo");
                    }
                });
    }

    private void inicializar() {
        cantidad = findViewById(R.id.etCantidadLimite);
        descripción = findViewById(R.id.etDescripcion);
        volver = findViewById(R.id.btnCancelar);
        guardar = findViewById(R.id.btnActualizar);
    }
}
