package mx.com.cesarcorona.directorio.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by ccabrera on 27/11/17.
 */

public class CategoriaClasificado implements Serializable {

    private String nombre;
    private String tipo;
    @Exclude
    private String dataBaseReference;




    public CategoriaClasificado() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDataBaseReference() {
        return dataBaseReference;
    }

    public void setDataBaseReference(String dataBaseReference) {
        this.dataBaseReference = dataBaseReference;
    }


    @Override
    public String toString() {
        return nombre;
    }
}
