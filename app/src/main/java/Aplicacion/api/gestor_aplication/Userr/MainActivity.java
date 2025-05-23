package Aplicacion.api.gestor_aplication.Userr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import Aplicacion.api.Entity.LoginUser;
import Aplicacion.api.Entity.Token;
import Aplicacion.api.Interfaz.IControllers;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Funcionamiento.Funcionamiento;
import Aplicacion.api.gestor_aplication.Movimiento.Movimientos;
import com.example.gestor_aplication.R;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import Aplicacion.api.gestor_aplication.Admin.ListUsers;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button log, button2;
    String username, password;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private ProgressBar progressBar;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Inicializar();
        Nav();
        PedirPermisos();
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                boolean isValid = true;

                if (username.isEmpty()) {
                    etUsername.setError("El usuario es obligatorio");
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
                    hacerLogin();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });
    }

    private void PedirPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    private void Nav() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.Ayuda) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sanchezpere80@gmail.com.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Aplicacion");
                intent.putExtra(Intent.EXTRA_TEXT, "Hola, necesito ayuda con...");

                try {
                    startActivity(Intent.createChooser(intent, "Enviar correo con..."));
                } catch (android.content.ActivityNotFoundException ex) {

                }

            } else if (id == R.id.descarga) {
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.manual);
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File outputFile = new File(downloadDir, "manual.pdf");

                OutputStream outputStream = new FileOutputStream(outputFile);
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();

                Toast.makeText(MainActivity.this, "PDF guardado en Descargas", Toast.LENGTH_SHORT).show();

                Intent openPdfIntent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", outputFile);
                openPdfIntent.setDataAndType(uri, "application/pdf");
                openPdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(openPdfIntent);

            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Error al guardar el PDF", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.work) {
                Intent intent = new Intent(MainActivity.this, Funcionamiento.class);
                startActivity(intent);
            }
            return true;
        });
    }

    private void hacerLogin() {
        progressBar.setVisibility(View.VISIBLE); // Mostrar loading
        log.setEnabled(false); // Opcional: desactivar botón
        button2.setEnabled(false); // Desactivar registro

        LoginUser loginUser = new LoginUser(username, password);
        IControllers iControllers = RetrofitClient.getAPILog();
        Call<Token> call = iControllers.loginUser(loginUser);

        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                progressBar.setVisibility(View.GONE);
                log.setEnabled(true);
                button2.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    guardarToken(token);
                    obtenerUserId(token, username, password);
                } else {
                    CustomToast.show(MainActivity.this, "Verifica las credenciales");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                log.setEnabled(true);
                button2.setEnabled(true);
                CustomToast.show(MainActivity.this, "No se pudo conectar con el servidor");
            }
        });
    }


    private void obtenerUserId(String token, String username, String password) {
        RetrofitClient.getAPI()
                .obtenerIdByUsernameAndPass("Bearer " + token, username, password)
                .enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, Response<Long> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            long userId = response.body();
                            guardarUserId(userId);
                            Intent intent;
                            if (username.equals("admin") && password.equals("admin")) {
                                intent = new Intent(MainActivity.this, ListUsers.class);
                            } else {
                                intent = new Intent(MainActivity.this, Movimientos.class);
                            }
                            startActivity(intent);
                            etUsername.setText("");
                            etPassword.setText("");
                        } else {
                            CustomToast.show(MainActivity.this,"Error al obetener el valor deseado");
                        }
                    }

                    @Override
                    public void onFailure(Call<Long> call, Throwable t) {
                        CustomToast.show(MainActivity.this, "No se pudo conectar con el servidor");
                        Log.d("MainActivity", "Error de red al obtener ID: " + t.getMessage());
                    }
                });
    }

    private void guardarToken(String token) {
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .edit()
                .putString("jwt_token", token)
                .apply();
        Log.d("MainActivity", "Token guardado en SharedPreferences");
    }
    private void guardarUserId(long userId) {
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .edit()
                .putLong("user_id", userId)
                .apply();
        Log.d("MainActivity", "ID de usuario guardado: " + userId);
    }
    private void Inicializar() {
        progressBar = findViewById(R.id.progressBar);
        etUsername = findViewById(R.id.usernameEditText);
        etPassword = findViewById(R.id.passwordEditText);
        log = findViewById(R.id.loginButton);
        button2 = findViewById(R.id.registerButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}