package Aplicacion.api.gestor_aplication.Presupuesto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Aplicacion.api.Entity.PresupuestoEntity;
import Aplicacion.api.ResponseEntity.PorcentajeGastoResponse;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PResupuestoActual extends AppCompatActivity {
    private long id = -1, userId, preid;
    private String token;
    TextView descripcion, cantidad, fecha, tvExtraInfo;
    Button volver, editar;
    private static final String TAG = "PResupuestoActual";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuesto_actual);

        Inicializar();
        recibirPost();
        Preferencias();

        if (token != null) {
            biscarPresupuestoActual();
            PorcentajeGastado();
        } else {
            Log.e(TAG, "Datos insuficientes - ID: " + id + ", Token: " + (token != null ? "disponible" : "null"));
            CustomToast.show(this, "Error: No se pudo cargar la información del presupuesto");
            finish();
        }

        volver.setOnClickListener(v -> finish());

        editar.setOnClickListener(v -> {
            Intent intent = new Intent(PResupuestoActual.this, ActualizarPresupuesto.class);
            intent.putExtra("descripcion", descripcion.getText().toString());
            intent.putExtra("cantidad", cantidad.getText().toString());
            intent.putExtra("idPre", preid);
            startActivityForResult(intent, 200);
        });
    }

    private void PorcentajeGastado() {
        RetrofitClient.getAPI().obtenerPorcentajeGasto("Bearer " + token, userId).enqueue(new Callback<PorcentajeGastoResponse>() {
            @Override
            public void onResponse(Call<PorcentajeGastoResponse> call, Response<PorcentajeGastoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String mensaje = response.body().getMensaje();
                    tvExtraInfo.setText(mensaje);

                    Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)%");
                    Matcher matcher = pattern.matcher(mensaje);
                    if (matcher.find()) {
                        double porcentaje = Double.parseDouble(matcher.group(1));
                        Log.d(TAG, "Porcentaje extraído: " + porcentaje);
                    }
                } else {
                    tvExtraInfo.setText("Porcentaje gastado: No disponible");
                }
            }

            @Override
            public void onFailure(Call<PorcentajeGastoResponse> call, Throwable throwable) {
                Log.e(TAG, "Error al obtener el porcentaje de gasto", throwable);
                CustomToast.show(PResupuestoActual.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void biscarPresupuestoActual() {
        RetrofitClient.getAPI().obtenerPresupuestoActual("Bearer " + token, userId)
                .enqueue(new Callback<PresupuestoEntity>() {
                    @Override
                    public void onResponse(Call<PresupuestoEntity> call, Response<PresupuestoEntity> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PresupuestoEntity presupuesto = response.body();
                            preid = presupuesto.getId();

                            descripcion.setText(presupuesto.getDescripcion());
                            double cantidadLimite = presupuesto.getCantidadLimite();
                            cantidad.setText(String.format(Locale.getDefault(), "%.2f", cantidadLimite));
                            fecha.setText(presupuesto.getFecha());

                            editar.setEnabled(true); // Habilitar botón editar
                        } else {
                            Log.e(TAG, "Error al obtener presupuesto: " + response.message());
                            CustomToast.show(PResupuestoActual.this, "No se pudo obtener el presupuesto.");
                            editar.setEnabled(false); // Deshabilitar si no hay presupuesto
                        }
                    }

                    @Override
                    public void onFailure(Call<PresupuestoEntity> call, Throwable throwable) {
                        Log.e(TAG, "Fallo en la conexión: ", throwable);
                        CustomToast.show(PResupuestoActual.this, "No se pudo conectar con el servidor");
                        editar.setEnabled(false); // Deshabilitar en caso de error
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
            Log.e(TAG, "Token no disponible");
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d(TAG, "Token obtenido correctamente");
        }
    }

    private void recibirPost() {
        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);
        Log.d(TAG, "ID recibido: " + id);
    }

    private void Inicializar() {
        descripcion = findViewById(R.id.tvDescripcion);
        cantidad = findViewById(R.id.tvCantidadLimite);
        fecha = findViewById(R.id.tvFecha);
        volver = findViewById(R.id.btnvolver);
        editar = findViewById(R.id.btnEditar);
        tvExtraInfo = findViewById(R.id.tvExtraInfo);

        // Deshabilitar botón editar por defecto
        editar.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            String nuevaDescripcion = data.getStringExtra("nuevaDescripcion");
            Double nuevaCantidad = data.getDoubleExtra("nuevaCantidad", -1);

            if (nuevaDescripcion != null) {
                descripcion.setText(nuevaDescripcion);
            }
            if (nuevaCantidad != -1) {
                cantidad.setText(String.format(Locale.getDefault(), "%.2f", nuevaCantidad));
            }
            PorcentajeGastado();
        }
    }
}
