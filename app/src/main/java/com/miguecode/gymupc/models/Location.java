package com.miguecode.gymupc.models;

public class Location {
    private double latitude;
    private double longitude;

    private String ciudad;
    private String departamento;
    private String pais;

    public Location() {
    }

    public Location(double latitude, double longitude, String ciudad, String departamento, String pais) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.ciudad = ciudad;
        this.departamento = departamento;
        this.pais = pais;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}
