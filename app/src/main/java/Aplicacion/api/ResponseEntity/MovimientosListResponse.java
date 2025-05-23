package Aplicacion.api.ResponseEntity;

import java.util.List;
import Aplicacion.api.Entity.Movimiento;

public class MovimientosListResponse {
    private String status;  // El estado de la respuesta (puede ser 'success', 'error', etc.)
    private List<Movimiento> data;  // La lista de movimientos obtenidos

    public String getStatus() {
        return status;  // Método para obtener el estado
    }

    public List<Movimiento> getData() {
        return data;  // Método para obtener la lista de movimientos
    }
}
