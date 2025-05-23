package Aplicacion.api.gestor_aplication.Presupuesto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.util.List;

import Aplicacion.api.Entity.PresupuestoEntity;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Presupuesto_select extends AppCompatActivity {
    private long id = -1,userId;
    private String token;
    TextView descripcion, cantidad,fecha;
    Button volver;
    private static final String TAG = "Presupuesto_select";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuesto_select);

        Inicializar();
        recibirPost();
        Preferencias();

        if (id != -1 && token != null) {
            buscarPResupuestoEnLista();
        } else {
            Log.e(TAG, "Datos insuficientes - ID: " + id + ", Token: " + (token != null ? "disponible" : "null"));
            //Toast.makeText(this, "Error: No se pudo cargar la información del presupuesto", Toast.LENGTH_SHORT).show();
            CustomToast.show(this,"Error: No se pudo cargar la información del presupuesto");
            finish();
        }

        volver.setOnClickListener(v -> finish());
    }

    private void buscarPResupuestoEnLista() {
        RetrofitClient.getAPI().getPresupuestoListbyUser("Bearer " + token,userId).enqueue(new Callback<List<PresupuestoEntity>>() {
            @Override
            public void onResponse(Call<List<PresupuestoEntity>> call, Response<List<PresupuestoEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PresupuestoEntity> presupuestoEntities = response.body();

                    for (PresupuestoEntity presupuestoEntity: presupuestoEntities){
                        if(presupuestoEntity.getId() == id){
                            actualizarUI(presupuestoEntity);
                            return;
                        }
                    }
                    CustomToast.show(Presupuesto_select.this, "Presupuesto no encontrado");
                }else {
                    CustomToast.show(Presupuesto_select.this, "Error al obtener la lista de presupuestos");
                }
            }

            @Override
            public void onFailure(Call<List<PresupuestoEntity>> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
                CustomToast.show(Presupuesto_select.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void actualizarUI(PresupuestoEntity presupuestoEntity) {
        runOnUiThread(()->{
            descripcion.setText("Descripción: "+presupuestoEntity.getDescripcion());
            cantidad.setText("Cantidad: "+String.valueOf(presupuestoEntity.getCantidadLimite()));
            fecha.setText("Fecha de creación: "+presupuestoEntity.getFecha());
        });
    }

    private void Inicializar() {
        descripcion = findViewById(R.id.tvDescripcion);
        cantidad = findViewById(R.id.tvCantidadLimite);
        fecha = findViewById(R.id.tvFecha);
        volver = findViewById(R.id.btnvolver);
    }

    private void recibirPost() {
        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);
        Log.d(TAG,"ID recibido: " + id);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            double nuevaCantidad = data.getDoubleExtra("nuevaCantidad", 0.0);
            String nuevaDescripcion = data.getStringExtra("nuevaDescripcion");

            // Actualiza los TextView directamente
            descripcion.setText("Descripcion: " + nuevaDescripcion);
            cantidad.setText("Cantidad: " + nuevaCantidad);
        }
    }
}