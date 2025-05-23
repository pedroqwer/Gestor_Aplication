package Aplicacion.api.gestor_aplication.Cuenta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import Aplicacion.api.Entity.Cuenta;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CuentaBancaria extends AppCompatActivity {
    private String token;
    private long userId;
    private TextView iden, nom, saldo, mon;
    private static final String TAG = "CuentaBancaria";
    private Button volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta_bancaria);

        Preferencias();
        Inicializar();
        ObtenerInformacion();

        volver.setOnClickListener(v -> finish());
    }

    private void ObtenerInformacion() {
        RetrofitClient.getAPI().obtenerCuentaBancaria("Bearer " + token, userId)
                .enqueue(new Callback<Cuenta>() {
                    @Override
                    public void onResponse(Call<Cuenta> call, Response<Cuenta> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Cuenta cuenta = response.body();
                            actualizarUI(cuenta);
                        } else {
                            // Si la respuesta no es exitosa, mostramos un mensaje de error
                            String errorMessage = "No se pudo cargar la cuenta";
                            if (response.errorBody() != null) {
                                try {
                                    // Intentar obtener el mensaje de error detallado desde el cuerpo de la respuesta
                                    errorMessage = response.errorBody().string();
                                } catch (Exception e) {
                                    Log.e(TAG, "Error al leer el cuerpo del error: " + e.getMessage());
                                }
                            }
                            Log.e(TAG, "Respuesta no exitosa: " + response.code() + " - " + errorMessage);
                            CustomToast.show(CuentaBancaria.this, errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<Cuenta> call, Throwable throwable) {
                        // Si falla la conexión o hay algún otro problema en la llamada
                        Log.e(TAG, "Error en la llamada: " + throwable.getMessage(), throwable);
                        CustomToast.show(CuentaBancaria.this, "No se pudo conectar con el servidor");
                    }
                });
    }

    private void actualizarUI(Cuenta cuenta) {
        iden.setText(String.valueOf(cuenta.getId()));
        nom.setText(cuenta.getNombreUsuario());
        saldo.setText(cuenta.getSaldo()+" €");
        mon.setText(cuenta.getMoneda());
    }

    private void Inicializar() {
        // Asociar las vistas a los elementos del layout
        iden = findViewById(R.id.idCuenta);
        nom = findViewById(R.id.nombreUsuario);
        saldo = findViewById(R.id.saldoCuenta);
        mon = findViewById(R.id.monedaCuenta);
        volver = findViewById(R.id.btnVolver);
    }

    private void Preferencias() {
        userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("user_id", -1);
        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("jwt_token", null);

        if (userId == -1 || token == null || token.isEmpty()) {
            Log.e(TAG, "Preferencias inválidas: userId=" + userId + ", token=" + token);
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show();
            finish();  // Finalizar la actividad si no hay una sesión válida
        } else {
            Log.d(TAG, "Token y userId obtenidos correctamente");
        }
    }
}
