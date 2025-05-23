package Aplicacion.api.ResponseEntity;

import java.util.List;

import Aplicacion.api.Entity.Auditoria;
import Aplicacion.api.Entity.Movimiento;

import java.util.List;

public class AuditroriaListResponse {
    private List<Auditoria> data;

    public List<Auditoria> getData() {
        return data;
    }

    public void setData(List<Auditoria> data) {
        this.data = data;
    }
}

