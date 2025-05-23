package Aplicacion.api.Interfaz;

import java.util.List;

import Aplicacion.api.Entity.ActualizarMovimiento;
import Aplicacion.api.Entity.ActualizarPresupuestoEntity;
import Aplicacion.api.Entity.ActualizarUser;
import Aplicacion.api.Entity.Auditoria;
import Aplicacion.api.Entity.Categoria;
import Aplicacion.api.Entity.CreateCuenta;
import Aplicacion.api.Entity.CreateMovimiento;
import Aplicacion.api.Entity.CreatePresupuesto;
import Aplicacion.api.Entity.Cuenta;
import Aplicacion.api.Entity.Movimiento;
import Aplicacion.api.Entity.PresupuestoEntity;
import Aplicacion.api.Entity.User;
import Aplicacion.api.Entity.CreateUser;
import Aplicacion.api.Entity.LoginUser;
import Aplicacion.api.Entity.Token;
import Aplicacion.api.ResponseEntity.MensajeResponse;
import Aplicacion.api.ResponseEntity.MovimientosListResponse;
import Aplicacion.api.ResponseEntity.PorcentajeGastoResponse;
import Aplicacion.api.ResponseEntity.ResponseEntity;
import Aplicacion.api.ResponseEntity.TotalResponse;
import Aplicacion.api.ResponseEntity.UserListResponse;
import Aplicacion.api.gestor_aplication.Movimiento.ActualizaMovimiento;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
public interface IControllers {

    //Login
    @POST("signup/perfil")
    Call<ResponseEntity> registerUser(@Body CreateUser createUser);
    @POST("login")
    Call<Token> loginUser(@Body LoginUser user);

    //Admin
    @GET("listar")
    Call<UserListResponse> getUserList(@Header("Authorization") String token);
    @GET("perfil/{id}")
    Call<User> getUserById(@Header("Authorization") String token,  @Path("id") long id);
    @DELETE("borrar/{id}")
    Call<ResponseBody> deleteUser(@Header("Authorization") String token, @Path("id") long id);

    //User
    @GET("transacciones/{id_User}")
    Call<List<Movimiento>> getMovimentsListbyUser(@Header("Authorization") String token, @Path("id_User") long id);
    @GET("usuarioPresupuestos/{idUser}")
    Call<List<PresupuestoEntity>> getPresupuestoListbyUser(@Header("Authorization") String token, @Path("idUser") long id);

    @GET("usuarioauditrorias/{idUser}")
    Call<List<Auditoria>> getAuditoriaListbyUser(@Header("Authorization") String token, @Path("idUser") long id);

    @POST("Create_Transaccion")
    Call<ResponseBody> crearTransaccion(@Header("Authorization") String token, @Body CreateMovimiento createMovimiento);

    //Ok
    @PUT("crearCuenta")
    Call<CreateCuenta> crearCuenta(@Header("Authorization") String token, @Body CreateCuenta createCuenta);

    @PUT("Perfil_Modificar_User/{id}")
    Call<ResponseBody> modificarPerfil(
            @Header("Authorization") String token,
            @Path("id") long id,
            @Body ActualizarUser user
    );
    @POST("crearPresupuesto")
    Call<ResponseEntity> crearPresupuesto(@Header("Authorization") String token, @Body CreatePresupuesto createPresupuesto);
    @DELETE("presupuestoBorrar/{id}")
    Call<ResponseBody>deletePresupuesto(@Header("Authorization") String token, @Path("id") long id);
    @PUT("Presupuesto_Modi/{id}")
    Call<MensajeResponse> modificarPresupuesto(
            @Header("Authorization") String token,
            @Path("id") long id,
            @Body ActualizarPresupuestoEntity newPresupuesto
    );

    @GET("total-ingresos/{id}")
    Call<TotalResponse> obtenerTotalIngresos(
            @Header("Authorization") String token,
            @Path("id") long id
    );


    @GET("total-gastos/{id}")
    Call<Double> obtenerTotalGastos(
            @Header("Authorization") String token,
            @Path("id") long id
    );

    @GET("cuentaUser/{id}")
    Call<Cuenta> obtenerCuentaBancaria(
            @Header("Authorization") String token,
            @Path("id") long id
    );

    @GET("saldo/{usuarioId}")
    Call<ResponseBody> consultarSaldo(
            @Header("Authorization") String token,
            @Path("usuarioId") Long usuarioId
    );

    @GET("perfilid/{username}/{password}")
    Call<Long> obtenerIdByUsernameAndPass(
            @Header("Authorization") String token,  // Pasamos el token de autorización
            @Path("username") String username,    // Parametro de ruta username
            @Path("password") String password     // Parametro de ruta password
    );
    @GET("tiene-cuenta/{usuarioId}")
    Call<Boolean> tieneCuenta(
            @Header("Authorization") String token,
            @Path("usuarioId") Long usuarioId
    );

    @PUT("TransaccionModificar/{id}")
    Call<ResponseEntity> modificarTransaccion(
            @Header("Authorization") String token,
            @Path("id") long id,
            @Body ActualizarMovimiento movimiento
    );

    @GET("transaccionesByCategoria/{idUsuario}/{tipoId}")
    Call<List<Movimiento>> getTransaccionesByCategoria(
            @Header("Authorization") String token,
            @Path("idUsuario") long idUsuario,
            @Path("tipoId") int tipoId
    );
    @GET("presupuestoactual/{perfilId}")
    Call<PresupuestoEntity> obtenerPresupuestoActual(
            @Header("Authorization") String token,
            @Path("perfilId") long perfilId
    );
    @GET("porcentaje-gastado/{usuarioId}")
    Call<PorcentajeGastoResponse> obtenerPorcentajeGasto(@Header("Authorization") String token, @Path("usuarioId") long usuarioId);

    //Hay que hacer
    @GET("presupuesto/{id}")
    Call<PresupuestoEntity> obtenerPresupuestobyid(@Header("Authorization") String token, @Path("id") long id);
    @GET("auditoria/{id}")
    Call<Auditoria> obtenerAuditoria(@Header("Authorization") String token, @Path("id") long id);

    @GET("transaccion/{id}")
    Call<Movimiento> obtenerMovimiento(@Header("Authorization") String token, @Path("id") long id);
    
    @GET("telefonouser/{id}")
    Call<String> obtenerTelefonoPorId(
            @Header("Authorization") String token,
            @Path("id") long id
    );
}
