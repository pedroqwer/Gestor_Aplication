package Aplicacion.api.ResponseEntity;

import java.math.BigDecimal;

/*public class SaldoResponse {
    BigDecimal saldo;

    public BigDecimal getSaldo() { return saldo; }
}*/


public class SaldoResponse {
    private BigDecimal saldo;

    public SaldoResponse(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}