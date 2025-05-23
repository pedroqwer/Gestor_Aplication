package Aplicacion.api.gestor_aplication.Movimiento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import Aplicacion.api.Adapter.MovimientoAdapter;
import Aplicacion.api.Entity.Movimiento;
import Aplicacion.api.ResponseEntity.TotalResponse;
import Aplicacion.api.Retrofit.RetrofitClient;
import Aplicacion.api.gestor_aplication.Cuenta.Crear_cuenta;
import Aplicacion.api.gestor_aplication.Cuenta.CuentaBancaria;
import Aplicacion.api.gestor_aplication.Funcionamiento.CustomToast;
import Aplicacion.api.gestor_aplication.Historial.Historial;
import Aplicacion.api.gestor_aplication.Presupuesto.PResupuesto;
import com.example.gestor_aplication.R;

import Aplicacion.api.gestor_aplication.Presupuesto.PResupuestoActual;
import Aplicacion.api.gestor_aplication.Userr.Perfil;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Movimientos extends AppCompatActivity {
    private ListView lstPosts;
    TextView txtgastos,txtingresos,txtsaldo;
    private MovimientoAdapter adaptador;
    private List<Movimiento> movimientoList = new ArrayList<>();
    Button botonIGT,hacerMovinimiento;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private String token;
    long userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        Inicializar();
        Preferecias();
        Nav();

        adaptador = new MovimientoAdapter(this, movimientoList);
        lstPosts.setAdapter(adaptador);
        lstPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movimiento model = movimientoList.get(position);
                Log.d(getClass().getName(), model.toString());

                Intent intent = new Intent(Movimientos.this, Movimineto_Seleccionado.class);
                long valor = (long) model.getId();
                intent.putExtra("id", valor);
                startActivity(intent);
            }
        });
        verificarCuenta();
        descargarPosts();
        Saldo();
        TotalGastos();
        TotalIngresos();
        Botones();
    }

    private void Preferecias() {
        userId = getIntent().getLongExtra("user_id", -1);
        if (userId == -1) {
            userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("user_id", -1);
            if (userId == -1) {
                finish();
                return;
            }
        }

        token = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("jwt_token", null);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("ListUsers", "Token cargado desde SharedPreferences: " + token);
    }

    private void Botones() {
        botonIGT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Area();
            }
        });

        hacerMovinimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Movimientos.this, CrearMovimiento.class);
                startActivity(intent);
            }
        });
    }
    private void verificarCuenta() {
        // Llamada a la API para verificar si el usuario tiene cuenta
        Call<Boolean> call = RetrofitClient.getAPI()
                .tieneCuenta("Bearer " + token, userId);  // Pasa el token y el usuarioId

        // Obtener la URL antes de hacer la solicitud
        Log.d("URL de solicitud ", call.request().url().toString());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        boolean tieneCuenta = response.body();  // Obtiene el valor booleano
                        if (!tieneCuenta) {
                            AlertDialogInicial();
                        }
                    } else {
                        CustomToast.show(Movimientos.this, "Respuesta vacía");
                    }
                } else {
                    CustomToast.show(Movimientos.this, "Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                CustomToast.show(Movimientos.this, "No se pudo conectar con el servidor");
            }
        });
    }
    private void AlertDialogInicial() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No tiene cuanta bancaria");
        builder.setMessage("¿Desea crearse una cuenta?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Movimientos.this, Crear_cuenta.class));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción al pulsar Cancelar
                CustomToast.show(Movimientos.this, "Cancelado");
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    // Acción al pulsar atrás: por ejemplo, cerrar la actividad
                    onBackPressed();  // o finish();
                    dialogInterface.dismiss();
                    return true;
                }
                return false;
            }
        });
        dialog.show();
    }

    private void Nav() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.Presupuesto) {
                Intent intent = new Intent(Movimientos.this, PResupuesto.class);
                startActivity(intent);
            } else if (id == R.id.Perfil) {
                Intent intent = new Intent(this, Perfil.class);
                startActivity(intent);
            } else if (id == R.id.Historial) {
                Intent intent = new Intent(Movimientos.this, Historial.class);
                startActivity(intent);
            } else if (id == R.id.CuentaBancaria) {
                Intent intent = new Intent(this, CuentaBancaria.class);
                startActivity(intent);
            } else if (id == R.id.PresupuestoActul) {
                Intent intent = new Intent(this, PResupuestoActual.class);
                startActivity(intent);
            }else if (id == R.id.nav_logout) {
                finish(); // Cierra la actividad actual
            }
            return true;
        });
    }
    public void Saldo() {
        RetrofitClient.getAPI().consultarSaldo("Bearer " + token, userId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String respuesta = response.body().string();

                                String saldoStr = respuesta.replace("Saldo:", "").trim();
                                BigDecimal saldo = new BigDecimal(saldoStr);

                                txtsaldo.setText(saldo.toPlainString()+"€");
                            } catch (IOException e) {
                                Log.e("API", "Error al leer respuesta", e);
                            }
                        } else {
                            Log.e("API", "Error al consultar saldo: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("API", "Fallo en la llamada: " + t.getMessage());
                        CustomToast.show(Movimientos.this, "No se pudo conectar con el servidor");
                    }
                });
    }

    private void TotalIngresos() {
        RetrofitClient.getAPI().obtenerTotalIngresos("Bearer " + token, userId)
                .enqueue(new Callback<TotalResponse>() {
                    @Override
                    public void onResponse(Call<TotalResponse> call, Response<TotalResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BigDecimal total = response.body().getTotal();

                            Log.d("TotalIngresos", "✅ Total recibido: " + total);
                            txtingresos.setText(total.toPlainString()+"€");
                        } else if (response.code() == 404) {
                            Log.w("TotalIngresos", "⚠️ No se encontraron ingresos para el usuario");
                            txtingresos.setText("0.0€");
                            CustomToast.show(Movimientos.this, "No hay ingresos registrados");
                        } else {
                            Log.e("TotalIngresos", "❌ Error en la respuesta: Código " + response.code());
                            CustomToast.show(Movimientos.this, "No se pudo obtener los ingresos totales");
                        }
                    }

                    @Override
                    public void onFailure(Call<TotalResponse> call, Throwable t) {
                        Log.e("TotalIngresos", "❌ Error de red: " + t.getMessage(), t);
                        CustomToast.show(Movimientos.this, "No se pudo conectar con el servidor");
                    }
                });
    }


    private void TotalGastos() {
        Log.d("TotalGastos", "➡️ Iniciando solicitud para obtener total de gastos...");

        RetrofitClient.getAPI().obtenerTotalGastos("Bearer " + token, userId).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                Log.d("TotalGastos", "📡 onResponse llamado. Código HTTP: " + response.code());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Double total = response.body();
                        Log.d("TotalGastos", "✅ Total recibido: " + total);
                        txtgastos.setText(total+"€");
                    } else {
                        Log.w("TotalGastos", "⚠️ Respuesta exitosa pero sin cuerpo (body null)");
                        txtgastos.setText("0.0€");
                    }
                } else {
                    Log.e("TotalGastos", "❌ Error en la respuesta: Código " + response.code());

                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("TotalGastos", "❌ ErrorBody: " + errorBody);
                    } catch (IOException e) {
                        Log.e("TotalGastos", "❌ Error leyendo errorBody", e);
                    }

                    CustomToast.show(Movimientos.this, "No se ha podido obtener los gastos totales");
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable throwable) {
                Log.e("TotalGastos", "❌ Error de red o excepción: " + throwable.getMessage(), throwable);
                CustomToast.show(Movimientos.this, "No se pudo conectar con el servidor");
            }
        });
    }


    private void Area() {
        String[] opciones = {"Ingresos", "Gastos", "Todo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        obtenerTransaccionesPorCategoria(0);
                        break;
                    case 1:
                        obtenerTransaccionesPorCategoria(1);
                        break;
                    case 2:
                        descargarPosts();
                        break;
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void obtenerTransaccionesPorCategoria(int tipoId) {
        RetrofitClient.getAPI().getTransaccionesByCategoria("Bearer " + token, userId, tipoId).enqueue(new Callback<List<Movimiento>>() {
            @Override
            public void onResponse(Call<List<Movimiento>> call, Response<List<Movimiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movimiento> historial = response.body();
                    Log.d("Movimientos", "Movimientos recibidos: " + historial.size());

                    movimientoList.clear();
                    movimientoList.addAll(historial);
                    adaptador.notifyDataSetChanged();
                    CustomToast.show(Movimientos.this,"Filtrado por tipo");
                } else {
                    Log.e("Movimienyos", "Error en la respuesta: " + response.code());
                    CustomToast.show(Movimientos.this, "Error al obtener los movimientos: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Movimiento>> call, Throwable t) {
                Log.e("Movimientos", "Fallo al conectar: " + t.getMessage(), t);
                CustomToast.show(Movimientos.this, "No se pudo conectar con el servidor");
            }
        });
    }

    private void Inicializar() {
        lstPosts = findViewById(R.id.listView);
        botonIGT = findViewById(R.id.button5);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        hacerMovinimiento = findViewById(R.id.button4);
        txtgastos = findViewById(R.id.txgastos);
        txtingresos = findViewById(R.id.txingresos);
        txtsaldo = findViewById(R.id.txsaldo);
    }
    private String cortarCadenaMaximo(String str, double max) {
        if (str.length() > max) {
            return str.substring(0, (int) max) + "..."; // Corta la cadena si excede el límite
        }
        return str;
    }
    private void descargarPosts() {
        RetrofitClient.getAPI().getMovimentsListbyUser("Bearer " + token, userId).enqueue(new Callback<List<Movimiento>>() {
            @Override
            public void onResponse(Call<List<Movimiento>> call, Response<List<Movimiento>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movimiento> historial = response.body();
                    Log.d("Movimientos", "Movimientos recibidos: " + historial.size());

                    // Actualizar lista con los nuevos movimientos
                    movimientoList.clear();
                    movimientoList.addAll(historial);  // Añadir los movimientos de la respuesta a la lista

                    // Notificar al adaptador para que se actualice el ListView
                    adaptador.notifyDataSetChanged();
                } else {
                    Log.e("Movimientos", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Movimiento>> call, Throwable t) {
                Log.e("Movimientos", "Fallo al conectar: " + t.getMessage(), t);
                CustomToast.show(Movimientos.this, "No se pudo conectar con el servidor");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        descargarPosts();
        Saldo();
        TotalGastos();
        TotalIngresos();
    }
}