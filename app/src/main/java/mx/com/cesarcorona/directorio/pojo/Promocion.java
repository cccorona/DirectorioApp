package mx.com.cesarcorona.directorio.pojo;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by ccabrera on 12/12/17.
 */

public class Promocion implements Serializable {

    private String photoUrl;
    private String negocioId;
    private HashMap<String,String> ubicacion;


    public Promocion() {
    }


    public Promocion(String photoUrl, String negocioId, HashMap<String, String> ubicacion) {
        this.photoUrl = photoUrl;
        this.negocioId = negocioId;
        this.ubicacion = ubicacion;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(String negocioId) {
        this.negocioId = negocioId;
    }


    public HashMap<String, String> getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(HashMap<String, String> ubicacion) {
        this.ubicacion = ubicacion;
    }
}
