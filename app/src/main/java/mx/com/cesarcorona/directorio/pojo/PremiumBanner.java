package mx.com.cesarcorona.directorio.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by ccabrera on 12/10/17.
 */

public class PremiumBanner implements Serializable {


    private String id_negocio;


    @Exclude
    private String databaseReference;
    @Exclude
    private Negocio relatedNegocio;


    public PremiumBanner() {
    }


    public String getId_negocio() {
        return id_negocio;
    }

    public void setId_negocio(String id_negocio) {
        this.id_negocio = id_negocio;
    }

    public String getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(String databaseReference) {
        this.databaseReference = databaseReference;
    }

    public Negocio getRelatedNegocio() {
        return relatedNegocio;
    }

    public void setRelatedNegocio(Negocio relatedNegocio) {
        this.relatedNegocio = relatedNegocio;
    }
}
