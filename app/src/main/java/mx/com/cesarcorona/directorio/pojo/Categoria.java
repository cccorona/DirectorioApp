package mx.com.cesarcorona.directorio.pojo;

import java.io.Serializable;

/**
 * Created by Corona on 8/7/2017.
 */

public class Categoria implements Serializable {

    private String display_title;
    private String type;
    private String url_icon;

    public Categoria(String display_title, String type, String url_icon) {
        this.display_title = display_title;
        this.type = type;
        this.url_icon = url_icon;
    }

    public Categoria() {
    }

    public String getDisplay_title() {
        return display_title;
    }

    public void setDisplay_title(String display_title) {
        this.display_title = display_title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl_icon() {
        return url_icon;
    }

    public void setUrl_icon(String url_icon) {
        this.url_icon = url_icon;
    }
}
