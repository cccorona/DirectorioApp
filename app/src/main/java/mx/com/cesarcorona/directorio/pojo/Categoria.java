package mx.com.cesarcorona.directorio.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by Corona on 8/7/2017.
 */

public class Categoria implements Serializable {

    private String nombre;
    private String imagen;
    @Exclude
    private String dataBaseReference;

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Categoria() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }



    public String getDataBaseReference() {
        return dataBaseReference;
    }

    public void setDataBaseReference(String dataBaseReference) {
        this.dataBaseReference = dataBaseReference;
    }
}
