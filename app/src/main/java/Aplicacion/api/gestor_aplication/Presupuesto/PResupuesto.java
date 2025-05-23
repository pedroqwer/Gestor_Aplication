package Aplicacion.api.gestor_aplication.Presupuesto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Aplicacion.api.Adapter.PresupuestoAdapter;
import Aplicacion.api.Entity.Movimiento;
import Aplicacion.api.Entity.PresupuestoEntity;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PResupuesto extends AppCompatActivity {

    private PresupuestoAdapter adaptador;
    private List<PresupuestoEntity> pResupuestos = new ArrayList<>();
    ListView presupuestos;
    private String token;
    long userId;
    Button ir, volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuesto);

        Inicializar();
        Preferencias();

        adaptador = new PresupuestoAdapter(this, pResupuestos);
        presupuestos.setAdapter(adaptador);
        presupuestos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PresupuestoEntity model = (PresupuestoEntity) adaptador.getItem(position);
                Log.d(getClass().getName(), model.toString());

                new AlertDialog.Builder(PResupuesto.this)
                        .setTitle("Opciones para " + model.getDescripcion())
                        .setMessage("¿Qué deseas hacer con este presupuesto?")
                        .setPositiveButton("Acceder", (dialog, which) -> {
                            Intent intent = new Intent(PResupuesto.this, Presupuesto_select.class);
                            long valor = (long) model.getId();
                            intent.putExtra("id", valor);
                            startActivity(intent);
                        })
                        .setNegativeButton("Eliminar", (dialog, which) -> {
                            eliminarPresupuesto(model.getId()); // Función que tú defines para eliminar
                        })
                        .setNeutralButton("Cancelar", null)
                        .show();
            }
        });
        descargarPResupuestos();

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PResupuesto.this, Crear_Presupuesto.class);
                startActivity(intent);
            }
        });
    }

    private void Inicializar() {
        presupuestos = findViewById(R.id.listViewPresupuesto);
        ir =findViewById(R.id.btnAgregar);
        volver = findViewById(R.id.btnVolver);
    }

    private void eliminarPresupuesto(long id) {
        RetrofitClient.getAPI().deletePresupuesto("Bearer " + token, id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String mensaje = response.body().string();
                        Log.d("API", "Respuesta: " + mensaje);
                        CustomToast.show(PResupuesto.this, "Presupuesto eliminado");
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("PResupuesto", "Error al leer la respuesta: " + e.getMessage());
                    }
                }else {
                    Log.e("PResupuesto", "Error al eliminar usuario: Código de respuesta: " + response.code());
                    CustomToast.show(PResupuesto.this, "Error al eliminar presupuesto");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("PResupuesto", "Error de conexión: " + t.getMessage(), t);
                CustomToast.show(PResupuesto.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void descargarPResupuestos() {
        RetrofitClient.getAPI().getPresupuestoListbyUser("Bearer " + token, userId).enqueue(new Callback<List<PresupuestoEntity>>() {
            @Override
            public void onResponse(Call<List<PresupuestoEntity>> call, Response<List<PresupuestoEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PresupuestoEntity> historial = response.body();
                    Log.d("ListUsers", "PResupuestos recibidos: " + historial.size());

                    pResupuestos.clear();
                    pResupuestos.addAll(historial);
                    adaptador.notifyDataSetChanged();
                } else {
                    Log.e("ListUsers", "Error en la respuesta: " + response.code());
                    CustomToast.show(PResupuesto.this, "No tienes presupuestos");
                }
            }

            @Override
            public void onFailure(Call<List<PresupuestoEntity>> call, Throwable t) {
                Log.e("ListUsers", "Fallo al conectar: " + t.getMessage(), t);
                //CustomToast.show(PResupuesto.this, "No se pudo conectar con el servidor");
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
    private String cortarCadenaMaximo(String str, int max) {
        if (str.length() > max) {
            str = str.substring(0, max) + "...";
        }
        return str;
    }
    @Override
    protected void onResume() {
        super.onResume();
        descargarPResupuestos();
    }
}