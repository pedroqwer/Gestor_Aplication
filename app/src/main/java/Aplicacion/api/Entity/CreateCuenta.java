package Aplicacion.api.Entity;

import java.math.BigDecimal;

public class CreateCuenta {
    private long usuario;
    private BigDecimal saldo;
    private String moneda;

    public CreateCuenta(long usuario, BigDecimal saldo, String moneda) {
        this.usuario = usuario;
        this.saldo = saldo;
        this.moneda = moneda;
    }

    public long getUsuario() {
        return usuario;
    }

    public void setUsuario(long usuario) {
        this.usuario = usuario;
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
}
