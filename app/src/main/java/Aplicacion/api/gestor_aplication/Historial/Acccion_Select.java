package Aplicacion.api.gestor_aplication.Historial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.util.List;

import Aplicacion.api.Entity.Auditoria;
import Aplicacion.api.Entity.PresupuestoEntity;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Cuenta.CuentaBancaria;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Presupuesto.Presupuesto_select;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Acccion_Select extends AppCompatActivity {
    private long id = -1,userId;
    private String token;
    TextView user,accion,fecha,deatalles;
    Button volver;
    private static final String TAG = "Acccion_Select";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acccion_select);

        inicializar();
        recibirPost();
        Preferencias();

        if (id != -1 && token != null) {
            buscarAuditoriaEnLista();
        } else {
            Log.e(TAG, "Datos insuficientes - ID: " + id + ", Token: " + (token != null ? "disponible" : "null"));
            Toast.makeText(this, "Error: No se pudo cargar la información del usuario", Toast.LENGTH_SHORT).show();
            finish();
        }
        volver.setOnClickListener(view -> finish());
    }

    private void buscarAuditoriaEnLista() {
        RetrofitClient.getAPI().getAuditoriaListbyUser("Bearer " + token,userId).enqueue(new Callback<List<Auditoria>>() {
            @Override
            public void onResponse(Call<List<Auditoria>> call, Response<List<Auditoria>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<Auditoria> auditorias = response.body();

                    for (Auditoria auditoriakis: auditorias){
                        if(auditoriakis.getId() == id){
                            actualizarUI(auditoriakis);
                            return;
                        }
                    }
                    CustomToast.show(Acccion_Select.this, "Auditoria no encontrado");
                }else {
                    CustomToast.show(Acccion_Select.this, "Error al obtener la lista de presupuestos");
                }
            }

            @Override
            public void onFailure(Call<List<Auditoria>> call, Throwable throwable) {
                Log.e(TAG, "Error de conexión: " + throwable.getMessage(), throwable);
                CustomToast.show(Acccion_Select.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void actualizarUI(Auditoria auditorias) {
        user.setText(auditorias.getNombreUsuario());
        accion.setText(auditorias.getAccion());
        fecha.setText(auditorias.getFecha());
        deatalles.setText(auditorias.getDetalles());
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
            CustomToast.show(this,"Error: Sesión no válida");
            finish();
        } else {
            Log.d(TAG, "Token obtenido correctamente");
        }

        userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("user_id", -1);
        if (userId != -1) {
            Log.d("Historial", "ID de usuario obtenido: " + userId);
        } else {
            Log.d("Historial", "No se encontró un ID de usuario en SharedPreferences");
        }

    }
    private void inicializar() {
        user = findViewById(R.id.textViewNombreUsuario);
        accion = findViewById(R.id.textViewAccion);
        fecha = findViewById(R.id.textViewFecha);
        deatalles = findViewById(R.id.textViewDetalles);
        volver = findViewById(R.id.volver);
    }
}