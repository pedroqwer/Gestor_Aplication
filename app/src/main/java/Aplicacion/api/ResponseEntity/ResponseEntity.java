package Aplicacion.api.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseEntity {
    private int code;
    private String message;
    private Map<String, String> errors;

    private ResponseEntity(int errorCode, String errorMessage)
    {
        this.code = errorCode;
        this.message = errorMessage;
        this.errors = new HashMap<>();
    }

    private ResponseEntity(int errorCode, String errorMessage, Map<String, String> errors)
    {
        this.code = errorCode;
        this.message = errorMessage;
        this.errors = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessege() {
        return message;
    }

    public void setMessege(String messege) {
        this.message = messege;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
