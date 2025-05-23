package Aplicacion.api.Entity;

public class Categoria {
    private int id;
    private String descripcion;
    private int tipo; // 0 = Ingreso, 1 = Gasto

    // Constructores
    public Categoria() {
    }

    public Categoria(int id, String descripcion, int tipo) {
        this.id = id;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    // Métodos útiles
    public String getTipoString() {
        return tipo == 0 ? "Ingreso" : "Gasto";
    }

    public boolean esIngreso() {
        return tipo == 0;
    }

    public boolean esGasto() {
        return tipo == 1;
    }

    @Override
    public String toString() {
        return descripcion + " (" + getTipoString() + ")";
    }
}
