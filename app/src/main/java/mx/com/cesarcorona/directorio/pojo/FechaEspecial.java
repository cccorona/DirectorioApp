package mx.com.cesarcorona.directorio.pojo;

import java.io.Serializable;

/**
 * Created by ccabrera on 19/03/18.
 */

public class FechaEspecial implements Serializable {


    private boolean abierto;
    private String horaApertura;
    private String horaCierre;
    private long fecha;


    public boolean isAbierto() {
        return abierto;
    }

    public void setAbierto(boolean abierto) {
        this.abierto = abierto;
    }

    public String getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(String horaApertura) {
        this.horaApertura = horaApertura;
    }

    public String getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(String horaCierre) {
        this.horaCierre = horaCierre;
    }


    public FechaEspecial() {
    }


    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }


}
