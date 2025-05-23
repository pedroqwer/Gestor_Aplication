package Aplicacion.api.gestor_aplication.Movimiento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gestor_aplication.R;

import java.io.IOException;

import Aplicacion.api.Entity.ActualizarMovimiento;
import Aplicacion.api.Entity.Tipo;
import Aplicacion.api.ResponseEntity.ResponseEntity;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Funcionamiento.Notificaciones;
import Aplicacion.api.gestor_aplication.Presupuesto.ActualizarPresupuesto;
import Aplicacion.api.gestor_aplication.Userr.Register;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizaMovimiento extends AppCompatActivity {
    private EditText etCantidad, etDescripcion;
    private Spinner spinnerTipo;
    private Button btnCancelar, btnGuardar;
    private String token;
    private long userId, idMovimiento;
    private Tipo tipoMovimiento; // Cambié el tipo a Tipo en vez de String

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualiza_movimiento);

        Inicializar();
        RecibirDatos();
        Preferencias();

        btnCancelar.setOnClickListener(v -> {
            finish();
        });

        btnGuardar.setOnClickListener(v -> {
            if (validar()) {
                Actualizar();
            }
        });
    }
    private boolean validar() {
        boolean isValid = true;

        String cantidadStr = etCantidad.getText().toString().trim();
        String descripcionStr = etDescripcion.getText().toString().trim();
        String tipoSeleccionado = (String) spinnerTipo.getSelectedItem();

        // Validar cantidad
        if (cantidadStr.isEmpty()) {
            etCantidad.setError("La cantidad es obligatoria");
            isValid = false;
        } else {
            try {
                double valor = Double.parseDouble(cantidadStr);
                if (valor <= 0) {
                    etCantidad.setError("Debe ser mayor a cero");
                    isValid = false;
                } else {
                    etCantidad.setError(null);
                }
            } catch (NumberFormatException e) {
                etCantidad.setError("Ingrese un número válido");
                isValid = false;
            }
        }

        // Validar descripción
        if (descripcionStr.isEmpty()) {
            etDescripcion.setError("La descripción es obligatoria");
            isValid = false;
        } else if (descripcionStr.length() < 3) {
            etDescripcion.setError("Debe tener al menos 3 caracteres");
            isValid = false;
        } else {
            etDescripcion.setError(null);
        }

        // Validar tipo (usando posición del spinner)
        if (spinnerTipo.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
            CustomToast.show(this, "Seleccione un tipo de movimiento válido");
            isValid = false;
        }

        return isValid;
    }
    private void Actualizar() {
        String cantidadStr = etCantidad.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String tipoSeleccionado = (String) spinnerTipo.getSelectedItem();

        // Validación básica
        if (cantidadStr.isEmpty() || descripcion.isEmpty() || tipoSeleccionado == null) {
            CustomToast.show(this,"Todos los campos son obligatorios");
            return;
        }

        double cantidadNueva;
        try {
            cantidadNueva = Double.parseDouble(cantidadStr);
        } catch (NumberFormatException e) {
            CustomToast.show(this,"Cantidad inválida");
            return;
        }

        Tipo tipo = Tipo.fromString(tipoSeleccionado);
        if (tipo == null) {
            CustomToast.show(this,"Tipo de movimiento inválido");
            return;
        }

        ActualizarMovimiento actualizarMovimiento = new ActualizarMovimiento(cantidadNueva, descripcion, tipo);


        RetrofitClient.getAPI().modificarTransaccion("Bearer " + token, idMovimiento, actualizarMovimiento)
                .enqueue(new Callback<ResponseEntity>() {
                    @Override
                    public void onResponse(Call<ResponseEntity> call, Response<ResponseEntity> response) {
                        if (!response.isSuccessful()) {
                            try {
                                String errorResponse = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "Error desconocido";
                                Log.e("API", "Error al actualizar movimiento: " + errorResponse);
                                CustomToast.show(ActualizaMovimiento.this, "Error: " + errorResponse);
                            } catch (IOException e) {
                                Log.e("API", "Error al leer el cuerpo del error", e);
                            }
                        } else {
                            CustomToast.show(ActualizaMovimiento.this, "Movimiento actualizado correctamente");
                            Notificaciones.mostrarNotificacionSimple(ActualizaMovimiento.this, "Movimiento actualizado", "¡Actualización!");

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("nuevaCantidad", cantidadNueva);
                            resultIntent.putExtra("nuevaDescripcion", descripcion);
                            resultIntent.putExtra("nuevoTipo", tipo.name());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseEntity> call, Throwable t) {
                        Log.e("ActualizaMovimiento", "Error de red o servidor", t);
                        CustomToast.show(ActualizaMovimiento.this, "No se pudo conectar con el servidor");
                    }
                });
    }
    private void RecibirDatos() {
        // Obtener los datos del Intent
        idMovimiento = getIntent().getLongExtra("id", -1);
        String descripcion = getIntent().getStringExtra("descripcion");
        String cantidad = getIntent().getStringExtra("cantidad");
        String tipoStr = getIntent().getStringExtra("tipo");

        // Establecer los valores en los campos correspondientes
        if (descripcion != null) {
            etDescripcion.setText(descripcion);
        }
        if (cantidad != null) {
            etCantidad.setText(cantidad);
        }

        if (tipoStr != null) {
            // Convertir el String al tipo de enum Tipo
            tipoMovimiento = Tipo.fromString(tipoStr); // Usamos el método fromString
            // Asignar el tipo al spinner si es necesario
            int position = getTipoPosition(tipoMovimiento);
            spinnerTipo.setSelection(position);
        }
    }

    // Determinar la posición del tipo de movimiento en el Spinner
    private int getTipoPosition(Tipo tipo) {
        if (tipo == Tipo.INGRESO) {
            return 0;  // El primer elemento del Spinner (INGRESO)
        } else if (tipo == Tipo.GASTO) {
            return 1;  // El segundo elemento del Spinner (GASTO)
        }
        return 0;  // Por defecto INGRESO si no se encuentra
    }

    // Obtener las preferencias (userId y token)
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

    // Inicializar los elementos de la interfaz
    private void Inicializar() {
        etCantidad = findViewById(R.id.etCantidad);
        etDescripcion = findViewById(R.id.etDescripcion);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Crear un ArrayAdapter para los tipos de movimiento
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cate, android.R.layout.simple_spinner_item);

        // Definir el estilo del item del Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Asignar el ArrayAdapter al Spinner
        spinnerTipo.setAdapter(adapter);
    }
}
