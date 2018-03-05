package mx.com.cesarcorona.directorio.pojo;

import java.io.Serializable;

/**
 * Created by ccabrera on 04/03/18.
 */

public class BannerCategoria implements Serializable {

    private String categoria_del_banner;
    private String nombre_categoria;
    private String nombre_del_negocio;


    public BannerCategoria() {
    }


    public String getCategoria_del_banner() {
        return categoria_del_banner;
    }

    public void setCategoria_del_banner(String categoria_del_banner) {
        this.categoria_del_banner = categoria_del_banner;
    }

    public String getNombre_categoria() {
        return nombre_categoria;
    }

    public void setNombre_categoria(String nombre_categoria) {
        this.nombre_categoria = nombre_categoria;
    }

    public String getNombre_del_negocio() {
        return nombre_del_negocio;
    }

    public void setNombre_del_negocio(String nombre_del_negocio) {
        this.nombre_del_negocio = nombre_del_negocio;
    }
}
