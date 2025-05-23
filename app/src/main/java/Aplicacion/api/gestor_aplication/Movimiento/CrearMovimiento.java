package Aplicacion.api.gestor_aplication.Movimiento;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import Aplicacion.api.Entity.CreateMovimiento;
import Aplicacion.api.Interfaz.IControllers;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Funcionamiento.Notificaciones;
import Aplicacion.api.gestor_aplication.Historial.Acccion_Select;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.gestor_aplication.R;

import java.io.IOException;

public class CrearMovimiento extends AppCompatActivity {

    private EditText etCantidad, etDescripcion, etDescripcionCategoria;
    private Spinner spinnerTipo;
    private Button btnGuardar, btnCancelar;
    private String token,cantidadStr,descripcion,descripcionCategoria;
    long userId;
    double cantidad;
    int tipo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_movimiento);

        Inicializar();
        Preferencias();

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validar()) {
                    CrarMovi();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpiar los campos y regresar
                etCantidad.setText("");
                etDescripcion.setText("");
                spinnerTipo.setSelection(0);
                finish();
            }
        });
    }
    private boolean validar() {
        boolean isValid = true;

        String cantidadStr = etCantidad.getText().toString().trim();
        String descripcionStr = etDescripcion.getText().toString().trim();

        if (cantidadStr.isEmpty()) {
            etCantidad.setError("La cantidad es obligatoria");
            isValid = false;
        } else {
            try {
                double valor = Double.parseDouble(cantidadStr);
                if (valor <= 0) {
                    etCantidad.setError("Debe ser un número mayor a cero");
                    isValid = false;
                } else {
                    etCantidad.setError(null);
                }
            } catch (NumberFormatException e) {
                etCantidad.setError("Ingrese un número válido");
                isValid = false;
            }
        }

        if (descripcionStr.isEmpty()) {
            etDescripcion.setError("La descripción es obligatoria");
            isValid = false;
        } else if (descripcionStr.length() < 3) {
            etDescripcion.setError("Debe tener al menos 3 caracteres");
            isValid = false;
        } else {
            etDescripcion.setError(null);
        }

        return isValid;
    }

    private void CrarMovi() {
        // Validación de los campos
        cantidadStr = etCantidad.getText().toString();
        descripcion = etDescripcion.getText().toString();

        if (cantidadStr.isEmpty() || descripcion.isEmpty()) {
            CustomToast.show(CrearMovimiento.this, "Por favor, rellene todos los campos.");
            return;
        }

        cantidad = Double.parseDouble(cantidadStr);
        tipo = spinnerTipo.getSelectedItemPosition();

        CreateMovimiento buildMovimiento = new CreateMovimiento(userId, cantidad, descripcion,tipo);

        IControllers iControllers = RetrofitClient.getAPI();
        Call<ResponseBody> call = iControllers.crearTransaccion("Bearer " + token, buildMovimiento);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String respuesta = response.body().string();
                        Log.d("CrearMovimiento", "✅ Respuesta: " + respuesta);
                        CustomToast.show(CrearMovimiento.this, respuesta);
                        Notificaciones.mostrarNotificacionSimple(CrearMovimiento.this, "Movimiento creado", "¡Creación!");

                        finish();
                    } catch (IOException e) {
                        Log.e("CrearMovimiento", "❌ Error al leer la respuesta", e);
                    }
                } else {
                    Log.e("CrearMovimiento", "❌ Error : " + response.message());
                    CustomToast.show(CrearMovimiento.this, "Gastos superior al saldo actual");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("CrearMovimiento", "❌ Error de conexión: " + throwable.getMessage(), throwable);
                CustomToast.show(CrearMovimiento.this, "No se pudo conectar con el servidor");
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
            CustomToast.show(this,"Error: Sesión no válida");
            finish();
            return;
        }
        Log.d("ListUsers", "Token cargado desde SharedPreferences: " + token);
    }

    private void Inicializar() {
        etCantidad = findViewById(R.id.etCantidad);
        etDescripcion = findViewById(R.id.etDescripcion);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
    }
}