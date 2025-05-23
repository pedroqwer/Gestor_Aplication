package Aplicacion.api.Entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class CuentaDTO implements Serializable {

    private long id;
    private String nombreUsuario;
    private String apellidoUsuario;
    private BigDecimal saldo;
    private String moneda;

    public CuentaDTO() {
    }

    public CuentaDTO(long id, String nombreUsuario, String apellidoUsuario, BigDecimal saldo, String moneda) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.apellidoUsuario = apellidoUsuario;
        this.saldo = saldo;
        this.moneda = moneda;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    @Override
    public String toString() {
        return "CuentaDTO " +
                "identificador= " + id +
                ", nombreUsuario= '" + nombreUsuario + '\'' +
                ", apellidoUsuario= '" + apellidoUsuario + '\'' +
                ", saldo= " + saldo +
                ", moneda= '" + moneda + '\'';
    }
}
