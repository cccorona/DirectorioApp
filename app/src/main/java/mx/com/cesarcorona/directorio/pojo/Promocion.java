package mx.com.cesarcorona.directorio.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by ccabrera on 12/12/17.
 */

public class Promocion implements Serializable {

    private String photoUrl;
    private String negocioId;
    private String ubicacion;
    private HashMap<String ,String> tags;


    @Exclude
    private String dataBasereference;


    public Promocion() {
    }


    public Promocion(String photoUrl, String negocioId, HashMap<String, String> ubicacion) {
        this.photoUrl = photoUrl;
        this.negocioId = negocioId;
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


    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, String> tags) {
        this.tags = tags;
    }


    public String getDataBasereference() {
        return dataBasereference;
    }

    public void setDataBasereference(String dataBasereference) {
        this.dataBasereference = dataBasereference;
    }


    public boolean containsTag(String tag){
        boolean contins = false;
        if(tags != null && tags.containsKey(tag)){
            return  true;
        }else{
            return false;
        }
    }
}
