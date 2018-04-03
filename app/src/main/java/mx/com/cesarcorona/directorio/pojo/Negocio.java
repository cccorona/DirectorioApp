package mx.com.cesarcorona.directorio.pojo;

import android.location.Location;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by ccabrera on 07/08/17.
 */

public class Negocio implements Serializable {

    private String close_time;
   // private String descripcion;
    private String display_title;
  //  private String facebook;
    private String full_url_logo;
    //private String mail;
    private HashMap<String,String> open_days;
    private String open_days_aleter;
    private String open_time;
   // private HashMap<String,String> ubicacion;
    private String url_logo;
    private HashMap<String,String> url_promos;
   // private String web;
   // private String whatsapp;
    private String phone;
   // private String twitter;
    private boolean entregaADomicilio;
    private String premiumBannerUrl;
    private boolean premiumStatus;
    private boolean open24Hours;
    private boolean aceptaTArjeta;
    private String categoria;




    /* New properties*/

    private String abierto_24_horas;
    private String abre_domingo;
    private String abre_jueves;
    private String abre_lunes;
    private String abre_martes;
    private String abre_miercoles;
    private String abre_sabado;
    private String abre_viernes;
    private String acepta_tarjeta;
    private String banner_premium;
    private String descripcion;
    private String entrega_a_domicilio;
    private String status_premium;
    private String facebook;
    private String hora_apertura;
    private String hora_cierre;
    private String imagen_promo1;
    private String imagen_promo2;
    private String imagen_promo3;
    private String logo;
    private String logo_negocio;
    private String mail;
    private String nombre;
    private String publicado;
    private String telefono;
    private String twitter;
    private String ubicacion;
    private String web;
    private String whatsapp;
    private HashMap<String,String> diasAbiertos;
    private String userId;
    private String pagina_web;
    private String direccionName;
    private HashMap<String,FechaEspecial> fechasEspeciales;


    private boolean openNow;


    @Exclude
    private boolean markedForRemove;







    @Exclude
    private String negocioDataBaseReference;




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

    public String getAbierto_24_horas() {
        return abierto_24_horas;
    }

    public void setAbierto_24_horas(String abierto_24_horas) {
        this.abierto_24_horas = abierto_24_horas;
    }

    public String getAbre_domingo() {
        return abre_domingo;
    }

    public void setAbre_domingo(String abre_domingo) {
        this.abre_domingo = abre_domingo;
    }

    public String getAbre_jueves() {
        return abre_jueves;
    }

    public void setAbre_jueves(String abre_jueves) {
        this.abre_jueves = abre_jueves;
    }

    public String getAbre_lunes() {
        return abre_lunes;
    }

    public void setAbre_lunes(String abre_lunes) {
        this.abre_lunes = abre_lunes;
    }

    public String getAbre_martes() {
        return abre_martes;
    }

    public void setAbre_martes(String abre_martes) {
        this.abre_martes = abre_martes;
    }

    public String getAbre_miercoles() {
        return abre_miercoles;
    }

    public void setAbre_miercoles(String abre_miercoles) {
        this.abre_miercoles = abre_miercoles;
    }

    public String getAbre_sabado() {
        return abre_sabado;
    }

    public void setAbre_sabado(String abre_sabado) {
        this.abre_sabado = abre_sabado;
    }

    public String getAbre_viernes() {
        return abre_viernes;
    }

    public void setAbre_viernes(String abre_viernes) {
        this.abre_viernes = abre_viernes;
    }

    public String getAcepta_tarjeta() {
        return acepta_tarjeta;
    }

    public void setAcepta_tarjeta(String acepta_tarjeta) {
        this.acepta_tarjeta = acepta_tarjeta;
    }

    public String getBanner_premium() {
        return banner_premium;
    }

    public void setBanner_premium(String banner_premium) {
        this.banner_premium = banner_premium;
    }

    public String getEntrega_a_domicilio() {
        return entrega_a_domicilio;
    }

    public void setEntrega_a_domicilio(String entrega_a_domicilio) {
        this.entrega_a_domicilio = entrega_a_domicilio;
    }

    public String getStatus_premium() {
        return status_premium;
    }

    public void setStatus_premium(String status_premium) {
        this.status_premium = status_premium;
    }

    public String getHora_apertura() {
        return hora_apertura;
    }

    public void setHora_apertura(String hora_apertura) {
        this.hora_apertura = hora_apertura;
    }

    public String getHora_cierre() {
        return hora_cierre;
    }

    public void setHora_cierre(String hora_cierre) {
        this.hora_cierre = hora_cierre;
    }

    public String getImagen_promo1() {
        return imagen_promo1;
    }

    public void setImagen_promo1(String imagen_promo1) {
        this.imagen_promo1 = imagen_promo1;
    }

    public String getImagen_promo2() {
        return imagen_promo2;
    }

    public void setImagen_promo2(String imagen_promo2) {
        this.imagen_promo2 = imagen_promo2;
    }

    public String getImagen_promo3() {
        return imagen_promo3;
    }

    public void setImagen_promo3(String imagen_promo3) {
        this.imagen_promo3 = imagen_promo3;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo_negocio() {
        return logo_negocio;
    }

    public void setLogo_negocio(String logo_negocio) {
        this.logo_negocio = logo_negocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPublicado() {
        return publicado;
    }

    public void setPublicado(String publicado) {
        this.publicado = publicado;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public HashMap<String, String> getDiasAbiertos() {
        return diasAbiertos;
    }

    public void setDiasAbiertos(HashMap<String, String> diasAbiertos) {
        this.diasAbiertos = diasAbiertos;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPagina_web() {
        return pagina_web;
    }

    public void setPagina_web(String pagina_web) {
        this.pagina_web = pagina_web;
    }


    public String getDireccionName() {
        return direccionName;
    }


    public boolean isMarkedForRemove() {
        return markedForRemove;
    }

    public void setMarkedForRemove(boolean markedForRemove) {
        this.markedForRemove = markedForRemove;
    }

    public void setDireccionName(String direccionName) {
        this.direccionName = direccionName;
    }

    public boolean hoyAbre(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String currentDat ="";
        boolean abreHoy = false;
        String negocioAbre ="";
        switch (day) {
            case Calendar.SUNDAY:
                // Current day is Sunday
                negocioAbre = getAbre_domingo();
                break;

            case Calendar.MONDAY:
                // Current day is Monday
                negocioAbre = getAbre_lunes();

                break;

            case Calendar.TUESDAY:
                negocioAbre = getAbre_martes();

                break;
            case Calendar.THURSDAY:
                negocioAbre = getAbre_jueves();

                break;
            case Calendar.WEDNESDAY:
                negocioAbre = getAbre_miercoles();

                break;
            case Calendar.FRIDAY:
                negocioAbre = getAbre_viernes();

                break;
            case Calendar.SATURDAY:
                negocioAbre = getAbre_sabado();

                break;
            // etc.
        }

        if(negocioAbre.equals("Si")){
            abreHoy = true;
        }

        return abreHoy;
    }


    public HashMap<String, FechaEspecial> getFechasEspeciales() {
        return fechasEspeciales;
    }

    public void setFechasEspeciales(HashMap<String, FechaEspecial> fechasEspeciales) {
        this.fechasEspeciales = fechasEspeciales;
    }

    public void updateData(Negocio oldData){

    }


    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }
}
