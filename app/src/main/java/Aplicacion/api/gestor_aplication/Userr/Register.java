package Aplicacion.api.gestor_aplication.Userr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.gestor_aplication.R;

import java.util.Collections;

import Aplicacion.api.Entity.CreateUser;
import Aplicacion.api.Interfaz.IControllers;
import Aplicacion.api.ResponseEntity.ResponseEntity;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Funcionamiento.Notificaciones;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    private EditText etNombre, etApellido, etDni, etEmail, etTelefono, etUsername, etPassword;
    private Button btnRegistrar, btnCancelar;
    private String nombre, apellido, dni, email, telefono, username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Inicializar();

        btnRegistrar.setOnClickListener(v ->
                Validar()
        );
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void registrarUsuario() {
        CreateUser createUser = new CreateUser(nombre,apellido,username,password,email,dni,telefono, Collections.singletonList(2));

        IControllers iControllers = RetrofitClient.getAPILog();
        Call<ResponseEntity> call = iControllers.registerUser(createUser);

        call.enqueue(new Callback<ResponseEntity>() {
            @Override
            public void onResponse(Call<ResponseEntity> call, Response<ResponseEntity> response) {
                if (response.isSuccessful()) {
                    //CustomToast.show(Register.this,response.message().toString());
                    CustomToast.show(Register.this,"Usuario registrado");
                    finish();
                    Notificaciones.mostrarNotificacionSimple(Register.this, "Usuario registrado", "¡Bienvenido!");
                } else {
                    CustomToast.show(Register.this,"Error al registrar usuario");
                }
            }

            @Override
            public void onFailure(Call<ResponseEntity> call, Throwable throwable) {
                Log.d("CrearPresupuesto_JSON", "error: "+throwable.getMessage());
                CustomToast.show(Register.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void Validar() {
        nombre = etNombre.getText().toString().trim();
        apellido = etApellido.getText().toString().trim();
        dni = etDni.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        telefono = etTelefono.getText().toString().trim();
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        boolean isValid = true;

        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            isValid = false;
        } else {
            etNombre.setError(null);
        }

        if (apellido.isEmpty()) {
            etApellido.setError("El apellido es obligatorio");
            isValid = false;
        } else {
            etApellido.setError(null);
        }

        if (dni.isEmpty()) {
            etDni.setError("El DNI es obligatorio");
            isValid = false;
        } else if (!dni.matches("^\\d{8}[A-Za-z]$")) {
            etDni.setError("DNI inválido (debe tener 8 dígitos y 1 letra)");
            isValid = false;
        } else {
            etDni.setError(null);
        }

        if (email.isEmpty()) {
            etEmail.setError("El correo es obligatorio");
            isValid = false;
        } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            etEmail.setError("Correo electrónico inválido");
            isValid = false;
        } else {
            etEmail.setError(null);
        }

        if (telefono.isEmpty()) {
            etTelefono.setError("El teléfono es obligatorio");
            isValid = false;
        } else if (!telefono.matches("^\\d{9,15}$")) {
            etTelefono.setError("Teléfono inválido (debe tener entre 9 y 15 dígitos)");
            isValid = false;
        } else {
            etTelefono.setError(null);
        }

        if (username.isEmpty()) {
            etUsername.setError("El nombre de usuario es obligatorio");
            isValid = false;
        } else {
            etUsername.setError(null);
        }

        if (password.isEmpty()) {
            etPassword.setError("La contraseña es obligatoria");
            isValid = false;
        } else {
            etPassword.setError(null);
        }

        if (isValid) {
            registrarUsuario();
        }
    }
    private void Inicializar() {
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etDni = findViewById(R.id.etDni);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnCancelar = findViewById(R.id.btnCancelar);
    }
}