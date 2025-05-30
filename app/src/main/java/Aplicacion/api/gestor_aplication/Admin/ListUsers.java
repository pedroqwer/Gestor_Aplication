package Aplicacion.api.gestor_aplication.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.gestor_aplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Aplicacion.api.Adapter.UserAdapter;
import Aplicacion.api.Entity.User;
import Aplicacion.api.ResponseEntity.UserListResponse;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Funcionamiento.Notificaciones;
import Aplicacion.api.gestor_aplication.Userr.Register;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListUsers extends AppCompatActivity {
    private ListView lstPosts;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private String token;
    private final static int CODIGO_RESPUESTA_ACTIVIDAD_DETALLE = 1;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        lstPosts = findViewById(R.id.listaUsers);
        button = findViewById(R.id.btnVolver);

        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("jwt_token", null);
        if (token == null || token.isEmpty()) {
            //Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show();
            CustomToast.show(this,"Tokrn no disponible");
            finish();
            return;
        }
        Log.d("ListUsers", "Token cargado desde SharedPreferences: " + token);

        userAdapter = new UserAdapter(this, userList);
        lstPosts.setAdapter(userAdapter);

        lstPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User model = userList.get(position);
                Log.d(getClass().getName(), model.toString());

                // Opciones para mostrar en el diálogo
                String[] opciones = {"Acceder", "Enviar SMS", "Eliminar", "Cancelar"};

                new AlertDialog.Builder(ListUsers.this)
                        .setTitle("Opciones para " + model.getNombre())
                        .setItems(opciones, (dialog, which) -> {
                            switch (which) {
                                case 0: // Acceder
                                    Intent intentAcceder = new Intent(ListUsers.this, USerSelected.class);
                                    intentAcceder.putExtra("id", (long) model.getId());
                                    startActivity(intentAcceder);
                                    break;

                                case 1: // Enviar SMS
                                    SMS(model);
                                    break;

                                case 2: // Eliminar
                                    eliminarUsuario(model.getId());
                                    break;

                                case 3: // Cancelar
                                    dialog.dismiss();
                                    break;
                            }
                        })
                        .show();
            }
        });

        descargarPosts();
        button.setOnClickListener(v->{
            finish();
        });
    }

    private void SMS(User model) {
        // Crear un EditText para introducir el mensaje
        EditText input = new EditText(ListUsers.this);
        input.setHint("Escribe tu mensaje");

        new AlertDialog.Builder(ListUsers.this)
                .setTitle("Mensaje a " + model.getNombre())
                .setView(input)
                .setPositiveButton("Enviar", (dialog1, which1) -> {
                    String txt = input.getText().toString().trim();
                    if (!txt.isEmpty()) {
                        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
                        intentSMS.setData(Uri.parse("sms:" + model.getTelefono()));
                        intentSMS.putExtra("sms_body", txt);
                        startActivity(intentSMS);
                    } else {
                        CustomToast.show(ListUsers.this,"El mensaje no puede estar vacío");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarUsuario(long idUsuario) {
        Log.d("EliminarUsuario", "Intentando eliminar usuario con ID: " + idUsuario);

        RetrofitClient.getAPI().deleteUser("Bearer " + token, idUsuario).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String mensaje = response.body().string();
                        Log.d("API", "Respuesta: " + mensaje);

                        CustomToast.show(ListUsers.this,mensaje);
                        Notificaciones.mostrarNotificacionSimple(ListUsers.this, "Usuario Eliminado", "¡Eliminado!");
                        descargarPosts();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("EliminarUsuario", "Error al leer la respuesta: " + e.getMessage());
                    }
                } else {
                    Log.e("EliminarUsuario", "Error al eliminar usuario: Código de respuesta: " + response.code());
                    CustomToast.show(ListUsers.this,"Error al eliminar usuario");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("EliminarUsuario", "Error de conexión: " + t.getMessage(), t);
                CustomToast.show(ListUsers.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void descargarPosts() {
        RetrofitClient.getAPI().getUserList("Bearer " + token).enqueue(new retrofit2.Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body().getData());
                    userAdapter.notifyDataSetChanged();
                } else {
                    Log.e("ListUsers", "Error en la respuesta: " + response.code());
                    CustomToast.show(ListUsers.this,"Error al obtener usuarios: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                Log.e("ListUsers", "Fallo al conectar: " + t.getMessage(), t);
                CustomToast.show(ListUsers.this, "No se pudo conectar con el servidor");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_RESPUESTA_ACTIVIDAD_DETALLE && resultCode == RESULT_OK) {
            descargarPosts();
        }
    }
}
