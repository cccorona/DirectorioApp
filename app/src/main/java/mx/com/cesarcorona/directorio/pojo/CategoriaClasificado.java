package mx.com.cesarcorona.directorio.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by ccabrera on 27/11/17.
 */

public class CategoriaClasificado implements Serializable {

    private String display_title;
    private String type;
    @Exclude
    private String dataBaseReference;


    public CategoriaClasificado(String display_title, String type, String url_icon) {
        this.display_title = display_title;
        this.type = type;
    }

    public CategoriaClasificado() {
    }

    public String getDisplay_title() {
        return display_title;
    }

    public void setDisplay_title(String display_title) {
        this.display_title = display_title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getDataBaseReference() {
        return dataBaseReference;
    }

    public void setDataBaseReference(String dataBaseReference) {
        this.dataBaseReference = dataBaseReference;
    }


    @Override
    public String toString() {
        return display_title;
    }
}
