package mx.com.cesarcorona.directorio.pojo;

import java.io.Serializable;

/**
 * Created by ccabrera on 11/12/17.
 */

public class Noticia implements Serializable {


    private String titulo;
    private String foto;
    private String texto_contenido;


    public Noticia() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getTexto_contenido() {
        return texto_contenido;
    }

    public void setTexto_contenido(String texto_contenido) {
        this.texto_contenido = texto_contenido;
    }
}
