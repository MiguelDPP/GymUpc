package com.miguecode.gymupc.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class Usuario implements java.io.Serializable {


    private static Usuario UsuarioLogueado;
    private String id;
    private String cedula;
    private String nombre;
    private String correo;
    private String carrera;
    private String semestre;
    private String password;

    private String role;

    public Usuario() {
    }
    public Usuario(String id, String cedula, String nombre, String correo, String carrera, String semestre, String password, String role) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.carrera = carrera;
        this.semestre = semestre;
        this.password = password;
        this.role = role;
    }
    public Usuario(String cedula, String nombre, String correo, String carrera, String semestre, String password, String role) {
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.carrera = carrera;
        this.semestre = semestre;
        this.password = password;
        this.role = role;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Usuario getUsuarioLogueado() {
        return UsuarioLogueado;
    }

    public static void setUsuarioLogueado(Usuario usuarioLogueado) {
        UsuarioLogueado = usuarioLogueado;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
