package Aplicacion.api.gestor_aplication.Cuenta;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gestor_aplication.R;

import java.math.BigDecimal;

import Aplicacion.api.Entity.CreateCuenta;
import Aplicacion.api.Interfaz.IControllers;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Crear_cuenta extends AppCompatActivity {

    private EditText etSaldo;
    private Spinner spinnerMoneda;
    private Button btnCrear;
    private BigDecimal saldoInput;
    private long userId;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);

        inicializarUI();
        cargarPreferencias();
        cargarSpinnerMonedas();

        btnCrear.setOnClickListener(v -> {
            if (validar()) {
                crearCuenta();
            }
        });
    }

    private void inicializarUI() {
        etSaldo = findViewById(R.id.etSaldo);
        spinnerMoneda = findViewById(R.id.spinnerMoneda); // Cambiado a Spinner
        btnCrear = findViewById(R.id.btnGuardar);
    }

    private void cargarSpinnerMonedas() {
        // Carga las monedas desde resources (strings.xml)
        String[] monedas = getResources().getStringArray(R.array.moneda);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, monedas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoneda.setAdapter(adapter);
    }

    private boolean validar() {
        String saldoTexto = etSaldo.getText().toString().trim();
        boolean isValid = true;

        if (saldoTexto.isEmpty()) {
            etSaldo.setError("El saldo es obligatorio");
            isValid = false;
        } else {
            try {
                saldoInput = new BigDecimal(saldoTexto);
                etSaldo.setError(null);
            } catch (NumberFormatException e) {
                etSaldo.setError("Saldo inválido");
                isValid = false;
            }
        }

        // Validar que haya seleccionado una moneda (opcional si siempre hay selección)
        if (spinnerMoneda.getSelectedItem() == null) {
            CustomToast.show(this, "Debe seleccionar una moneda");
            isValid = false;
        }

        return isValid;
    }

    private void cargarPreferencias() {
        userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("user_id", -1);
        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("jwt_token", null);

        if (userId == -1 || token == null || token.isEmpty()) {
            CustomToast.show(this, "Error al recibir credenciales");
            Log.e("CrearCuenta", "userId o token inválidos. userId: " + userId + ", token: " + token);
            finish();
        } else {
            Log.d("CrearCuenta", "Credenciales cargadas. userId: " + userId);
        }
    }

    private void crearCuenta() {
        String saldoTexto = etSaldo.getText().toString().trim();
        String moneda = (String) spinnerMoneda.getSelectedItem(); // Obtenemos la moneda seleccionada

        if (saldoTexto.isEmpty() || moneda == null || moneda.isEmpty()) {
            CustomToast.show(this, "Por favor, complete todos los campos");
            return;
        }

        try {
            saldoInput = new BigDecimal(saldoTexto);
        } catch (NumberFormatException e) {
            CustomToast.show(this, "Saldo inválido");
            return;
        }

        CreateCuenta nuevaCuenta = new CreateCuenta(userId, saldoInput, moneda);
        Log.d("CrearCuenta", "Datos: " + nuevaCuenta);

        IControllers api = RetrofitClient.getAPI();
        Call<CreateCuenta> call = api.crearCuenta("Bearer " + token, nuevaCuenta);

        call.enqueue(new Callback<CreateCuenta>() {
            @Override
            public void onResponse(Call<CreateCuenta> call, Response<CreateCuenta> response) {
                if (response.isSuccessful()) {
                    CustomToast.show(Crear_cuenta.this, "Cuenta registrada con éxito");
                    finish();
                } else {
                    Log.e("CrearCuenta", "Error: Código " + response.message());
                    CustomToast.show(Crear_cuenta.this, "Error al registrar cuenta " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CreateCuenta> call, Throwable t) {
                Log.e("CrearCuenta", "Fallo en la conexión: " + t.getMessage());
                CustomToast.show(Crear_cuenta.this, "No se pudo conectar con el servidor");
            }
        });
    }
}
