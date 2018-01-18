package mx.com.cesarcorona.directorio.pojo;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by ccabrera on 27/11/17.
 */

public class Clasificado implements Serializable {

    private String titulo;
    private String contenido;
    private String categoriaReference;
    private String telefono;
    private String publicado;
    private String referencia_categoria;

    @Exclude
    private String reference;


    public Clasificado() {
    }


    public Clasificado(String titulo, String contenido, String categoriaReference, String telefono) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.categoriaReference = categoriaReference;
        this.telefono = telefono;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getCategoriaReference() {
        return categoriaReference;
    }

    public void setCategoriaReference(String categoriaReference) {
        this.categoriaReference = categoriaReference;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPublicado() {
        return publicado;
    }

    public void setPublicado(String publicado) {
        this.publicado = publicado;
    }

    public String getReferencia_categoria() {
        return referencia_categoria;
    }

    public void setReferencia_categoria(String referencia_categoria) {
        this.referencia_categoria = referencia_categoria;
    }
}
