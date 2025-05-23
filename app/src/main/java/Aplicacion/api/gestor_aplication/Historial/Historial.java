package Aplicacion.api.gestor_aplication.Historial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.gestor_aplication.R;

import java.util.ArrayList;
import java.util.List;

import Aplicacion.api.Adapter.HistorialAdapter;
import Aplicacion.api.Entity.Auditoria;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Historial extends AppCompatActivity {
    ListView historial;
    private String token;
    long userId;
    Button volver;
    private HistorialAdapter adaptador;
    private List<Auditoria> auditorias = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        Preferencias();
        historial = findViewById(R.id.listViewHistorial);
        volver = findViewById(R.id.btnVolver);

        adaptador = new HistorialAdapter(this, auditorias);
        historial.setAdapter(adaptador);

        historial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Auditoria model = auditorias.get(position);
                Log.d(getClass().getName(), model.toString());

                Intent intent = new Intent(Historial.this, Acccion_Select.class);
                long valor = (long) model.getId();
                intent.putExtra("id", valor);
                startActivity(intent);
            }
        });
        descargarHistorial();
        volver.setOnClickListener(v ->{
            finish();
        } );
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
            CustomToast.show(this,"Error: Sesión no válida");
            finish();
            return;
        }
        Log.d("ListUsers", "Token cargado desde SharedPreferences: " + token);

    }

    private void descargarHistorial() {
        RetrofitClient.getAPI().getAuditoriaListbyUser("Bearer " + token, userId).enqueue(new Callback<List<Auditoria>>() {
            @Override
            public void onResponse(Call<List<Auditoria>> call, Response<List<Auditoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Auditoria> historial = response.body();
                    Log.d("ListUsers", "Historial recibidos: " + historial.size());

                    auditorias.clear();
                    auditorias.addAll(historial);
                    adaptador.notifyDataSetChanged();
                } else {
                    Log.e("ListUsers", "Error en la respuesta: " + response.code());
                    CustomToast.show(Historial.this,"Lista vacia");
                }
            }

            @Override
            public void onFailure(Call<List<Auditoria>> call, Throwable t) {
                Log.e("ListUsers", "Fallo al conectar: " + t.getMessage(), t);
                CustomToast.show(Historial.this, "No se pudo conectar con el servidor");
            }
        });
    }
}