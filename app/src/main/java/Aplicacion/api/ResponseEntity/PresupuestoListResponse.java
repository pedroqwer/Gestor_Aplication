package Aplicacion.api.ResponseEntity;

import java.util.List;

import Aplicacion.api.Entity.PresupuestoEntity;
import Aplicacion.api.Entity.User;

public class PresupuestoListResponse {
    private String status;
    private List<PresupuestoEntity> data;

    public String getStatus() {
        return status;
    }

    public List<PresupuestoEntity> getData() {
        return data;
    }
}
