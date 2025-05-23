package Aplicacion.api.Entity;

import java.math.BigDecimal;

public class Cuenta {
    private long id;
    private String nombreUsuario;
    private BigDecimal saldo;
    private String moneda;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }


    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    @Override
    public String toString() {
        return "CuentaDTO{" +
                "id=" + id +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", saldo=" + saldo +
                ", moneda='" + moneda + '\'' +
                '}';
    }
}
