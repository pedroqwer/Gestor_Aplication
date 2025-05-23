package Aplicacion.api.Entity;

import java.time.LocalDateTime;

public class User {
    private long id;
    private String nombre;
    private String apellido;
    private String username;
    private String password;
    private String telefono;
    private String email;
    private String dni;
    private LocalDateTime Ultimologon;

    public User(long id, String nombre, String apellido, String username, String password, String telefono, String email, String dni, LocalDateTime ultimologon) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.username = username;
        this.password = password;
        this.telefono = telefono;
        this.email = email;
        this.dni = dni;
        Ultimologon = ultimologon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDateTime getUltimologon() {
        return Ultimologon;
    }

    public void setUltimologon(LocalDateTime ultimologon) {
        Ultimologon = ultimologon;
    }
}
