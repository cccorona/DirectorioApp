package mx.com.cesarcorona.directorio.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by ccabrera on 12/10/17.
 */

public class PremiumBanner implements Serializable {

    private String beginingTime;
    private int numClicks;
    private int numTimeShow;

    @Exclude
    private String databaseReference;
    @Exclude
    private Negocio relatedNegocio;


    public PremiumBanner() {
    }


    public PremiumBanner(String beginingTime,  int numClicks, int numTimeShow) {
        this.beginingTime = beginingTime;
        this.numClicks = numClicks;
        this.numTimeShow = numTimeShow;
    }

    public String getBeginingTime() {
        return beginingTime;
    }

    public void setBeginingTime(String beginingTime) {
        this.beginingTime = beginingTime;
    }

    public int getNumClicks() {
        return numClicks;
    }

    public void setNumClicks(int numClicks) {
        this.numClicks = numClicks;
    }



    public int getNumTimeShow() {
        return numTimeShow;
    }

    public void setNumTimeShow(int numTimeShow) {
        this.numTimeShow = numTimeShow;
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
