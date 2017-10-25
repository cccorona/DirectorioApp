package mx.com.cesarcorona.directorio.pojo;

import android.location.Location;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by ccabrera on 07/08/17.
 */

public class Negocio implements Serializable {

    private String close_time;
    private String descripcion;
    private String display_title;
    private String facebook;
    private String full_url_logo;
    private String mail;
    private HashMap<String,String> open_days;
    private String open_days_aleter;
    private String open_time;
    private HashMap<String,String> ubicacion;
    private String url_logo;
    private HashMap<String,String> url_promos;
    private String web;
    private String whatsapp;
    private String phone;
    private String twitter;
    private boolean entregaADomicilio;
    private String premiumBannerUrl;
    private boolean premiumStatus;
    private boolean open24Hours;
    private boolean aceptaTArjeta;


    @Exclude
    private String negocioDataBaseReference;


    public Negocio(String close_time, String descripcion, String display_title, String facebook, String full_url_logo, String mail, HashMap<String, String> open_days, String open_days_aleter, String open_time, HashMap<String, String> ubicacion, String url_logo, HashMap<String, String> url_promos, String web, String whatsapp, String phone, String twitter, boolean entregaADomicilio, String premiumBannerUrl, boolean premiumStatus, boolean open24Hours, boolean aceptaTArjeta) {
        this.close_time = close_time;
        this.descripcion = descripcion;
        this.display_title = display_title;
        this.facebook = facebook;
        this.full_url_logo = full_url_logo;
        this.mail = mail;
        this.open_days = open_days;
        this.open_days_aleter = open_days_aleter;
        this.open_time = open_time;
        this.ubicacion = ubicacion;
        this.url_logo = url_logo;
        this.url_promos = url_promos;
        this.web = web;
        this.whatsapp = whatsapp;
        this.phone = phone;
        this.twitter = twitter;
        this.entregaADomicilio = entregaADomicilio;
        this.premiumBannerUrl = premiumBannerUrl;
        this.premiumStatus = premiumStatus;
        this.open24Hours = open24Hours;
        this.aceptaTArjeta = aceptaTArjeta;
    }

    public String getPremiumBannerUrl() {
        return premiumBannerUrl;
    }

    public void setPremiumBannerUrl(String premiumBannerUrl) {
        this.premiumBannerUrl = premiumBannerUrl;
    }

    public boolean isPremiumStatus() {
        return premiumStatus;
    }

    public void setPremiumStatus(boolean premiumStatus) {
        this.premiumStatus = premiumStatus;
    }

    public Negocio() {
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDisplay_title() {
        return display_title;
    }

    public void setDisplay_title(String display_title) {
        this.display_title = display_title;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getFull_url_logo() {
        return full_url_logo;
    }

    public void setFull_url_logo(String full_url_logo) {
        this.full_url_logo = full_url_logo;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public HashMap<String, String> getOpen_days() {
        return open_days;
    }

    public void setOpen_days(HashMap<String, String> open_days) {
        this.open_days = open_days;
    }

    public String getOpen_days_aleter() {
        return open_days_aleter;
    }

    public void setOpen_days_aleter(String open_days_aleter) {
        this.open_days_aleter = open_days_aleter;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public HashMap<String, String> getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(HashMap<String, String> ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getUrl_logo() {
        return url_logo;
    }

    public void setUrl_logo(String url_logo) {
        this.url_logo = url_logo;
    }

    public HashMap<String, String> getUrl_promos() {
        return url_promos;
    }

    public void setUrl_promos(HashMap<String, String> url_promos) {
        this.url_promos = url_promos;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getNegocioDataBaseReference() {
        return negocioDataBaseReference;
    }

    public void setNegocioDataBaseReference(String negocioDataBaseReference) {
        this.negocioDataBaseReference = negocioDataBaseReference;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public boolean isEntregaADomicilio() {
        return entregaADomicilio;
    }

    public void setEntregaADomicilio(boolean entregaADomicilio) {
        this.entregaADomicilio = entregaADomicilio;
    }

    public boolean isOpen24Hours() {
        return open24Hours;
    }

    public void setOpen24Hours(boolean open24Hours) {
        this.open24Hours = open24Hours;
    }

    public boolean isAceptaTArjeta() {
        return aceptaTArjeta;
    }

    public void setAceptaTArjeta(boolean aceptaTArjeta) {
        this.aceptaTArjeta = aceptaTArjeta;
    }
}
