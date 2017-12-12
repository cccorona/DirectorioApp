package mx.com.cesarcorona.directorio.pojo;

import java.io.Serializable;

/**
 * Created by ccabrera on 11/12/17.
 */

public class Noticia implements Serializable {


    private String titulo;
    private String fotoUrl;
    private String textoContenido;


    public Noticia() {
    }


    public Noticia(String titulo, String fotoUrl, String textoContenido) {
        this.titulo = titulo;
        this.fotoUrl = fotoUrl;
        this.textoContenido = textoContenido;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getTextoContenido() {
        return textoContenido;
    }

    public void setTextoContenido(String textoContenido) {
        this.textoContenido = textoContenido;
    }
}
