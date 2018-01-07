package com.github.erodriguezg.claveunica.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class InfoCiudadanoDto implements Serializable{

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("RolUnico")
    private RolUnico rolUnico;

    @JsonProperty("name")
    private Name name;


    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public RolUnico getRolUnico() {
        return rolUnico;
    }

    public void setRolUnico(RolUnico rolUnico) {
        this.rolUnico = rolUnico;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "InfoCiudadanoDto{" +
                "sub='" + sub + '\'' +
                ", rolUnico=" + rolUnico +
                ", name=" + name +
                '}';
    }

    public static class Name implements Serializable {

        @JsonProperty("nombres")
        private List<String> nombresList;

        @JsonProperty("apellidos")
        private List<String> apellidosList;

        public List<String> getNombresList() {
            return nombresList;
        }

        public void setNombresList(List<String> nombresList) {
            this.nombresList = nombresList;
        }

        public List<String> getApellidosList() {
            return apellidosList;
        }

        public void setApellidosList(List<String> apellidosList) {
            this.apellidosList = apellidosList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Name name = (Name) o;
            return Objects.equals(nombresList, name.nombresList) &&
                    Objects.equals(apellidosList, name.apellidosList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nombresList, apellidosList);
        }

        @Override
        public String toString() {
            return "Name{" +
                    "nombresList=" + nombresList +
                    ", apellidosList=" + apellidosList +
                    '}';
        }
    }


    public static class RolUnico implements Serializable {

        @JsonProperty("numero")
        private Integer numero;

        @JsonProperty("DV")
        private String dv;

        @JsonProperty("tipo")
        private String tipo;

        public Integer getNumero() {
            return numero;
        }

        public void setNumero(Integer numero) {
            this.numero = numero;
        }

        public String getDv() {
            return dv;
        }

        public void setDv(String dv) {
            this.dv = dv;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RolUnico rolUnico = (RolUnico) o;
            return Objects.equals(numero, rolUnico.numero) &&
                    Objects.equals(dv, rolUnico.dv) &&
                    Objects.equals(tipo, rolUnico.tipo);
        }

        @Override
        public int hashCode() {
            return Objects.hash(numero, dv, tipo);
        }

        @Override
        public String toString() {
            return "RolUnico{" +
                    "numero=" + numero +
                    ", dv='" + dv + '\'' +
                    ", tipo='" + tipo + '\'' +
                    '}';
        }
    }

}
