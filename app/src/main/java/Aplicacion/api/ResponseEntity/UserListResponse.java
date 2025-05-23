package Aplicacion.api.ResponseEntity;

import java.util.List;
import Aplicacion.api.Entity.User;

public class UserListResponse {
    private String status;
    private List<User> data;

    public String getStatus() {
        return status;
    }

    public List<User> getData() {
        return data;
    }
}
