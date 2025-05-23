package Aplicacion.api.Entity;

public enum Tipo {
    INGRESO(0),
    GASTO(1);

    private final int valor;

    // Constructor del enum
    Tipo(int valor) {
        this.valor = valor;
    }

    // Getter para obtener el valor asociado
    public int getValor() {
        return valor;
    }

    // Método para obtener el enum basado en el valor
    public static Tipo fromValor(int valor) {
        for (Tipo tipo : Tipo.values()) {
            if (tipo.getValor() == valor) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Valor desconocido: " + valor);
    }

    // Método para obtener el enum basado en el nombre del tipo (String)
    public static Tipo fromString(String tipoStr) {
        for (Tipo tipo : Tipo.values()) {
            if (tipo.name().equalsIgnoreCase(tipoStr)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo desconocido: " + tipoStr);
    }
}
