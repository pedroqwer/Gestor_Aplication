package Aplicacion.api.gestor_aplication.Movimiento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.util.List;

import Aplicacion.api.Entity.Movimiento;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Presupuesto.ActualizarPresupuesto;
import Aplicacion.api.gestor_aplication.Presupuesto.Presupuesto_select;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Movimineto_Seleccionado extends AppCompatActivity {
    private long id = -1,userId;
    private String token;
    TextView cantidadtv,descripciontv,fechatv,tvCategoria;
    Button volver, actualizar;
    private static final String TAG = "Movimineto_Seleccionado";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimineto_seleccionado);

        inicializar();
        recibirPost();
        Preferencias();

        if (id != -1 && token != null) {
            buscarMovimientoEnLista();
        } else {
            Log.e(TAG, "Datos insuficientes - ID: " + id + ", Token: " + (token != null ? "disponible" : "null"));
            CustomToast.show(this, "Error: No se pudo cargar la información del presupuesto");
            finish();
        }

        volver.setOnClickListener(v -> finish());
        actualizar.setOnClickListener(v -> {
            Intent intent = new Intent(Movimineto_Seleccionado.this, ActualizaMovimiento.class);

            intent.putExtra("descripcion", descripciontv.getText().toString().replace("Descripcion: ", ""));
            intent.putExtra("cantidad", cantidadtv.getText().toString().replace("Cantidad: ", ""));
            intent.putExtra("id", id);
            intent.putExtra("tipo", tvCategoria.getText().toString().replace("Tipo: ", ""));

            startActivityForResult(intent, 200);
        });
    }

    private void buscarMovimientoEnLista() {
        RetrofitClient.getAPI().getMovimentsListbyUser("Bearer " + token,userId).enqueue(new Callback<List<Movimiento>>() {
            @Override
            public void onResponse(Call<List<Movimiento>> call, Response<List<Movimiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movimiento> historial = response.body();

                    for (Movimiento movimiento: historial){
                        if(movimiento.getId() == id){
                            actualizarUI(movimiento);
                            return;
                        }
                    }
                    CustomToast.show(Movimineto_Seleccionado.this, "Movimiento no encontrado");
                } else {
                    CustomToast.show(Movimineto_Seleccionado.this, "Error al obtener la lista de movimientos");
                }
            }

            @Override
            public void onFailure(Call<List<Movimiento>> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage(), t);
                CustomToast.show(Movimineto_Seleccionado.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void actualizarUI(Movimiento movimiento) {
        runOnUiThread(() -> {
            cantidadtv.setText(String.format("%.2f", movimiento.getCantidad()));
            descripciontv.setText( movimiento.getDescripcion());
            fechatv.setText( movimiento.getFecha());
            tvCategoria.setText(movimiento.getTipo().name());
        });
    }

    private void recibirPost() {
        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);
        Log.d(TAG,"ID recibido: " + id);
    }
    private void inicializar() {
        cantidadtv = findViewById(R.id.tvCantidad);
        descripciontv = findViewById(R.id.tvDescripcion);
        fechatv = findViewById(R.id.tvFecha);
        volver =findViewById(R.id.btnVolver);
        actualizar = findViewById(R.id.btnActualizar);
        tvCategoria = findViewById(R.id.tvCategoria);
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
            String nuevoTipo = data.getStringExtra("nuevoTipo");

            cantidadtv.setText(""+nuevaCantidad);
            descripciontv.setText(nuevaDescripcion);
            tvCategoria.setText(nuevoTipo);
        }
    }
}