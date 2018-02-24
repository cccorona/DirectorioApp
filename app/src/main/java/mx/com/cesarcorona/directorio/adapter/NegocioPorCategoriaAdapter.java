package mx.com.cesarcorona.directorio.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.pojo.Categoria;
import mx.com.cesarcorona.directorio.pojo.Negocio;

/**
 * Created by ccabrera on 07/08/17.
 */

public class NegocioPorCategoriaAdapter  extends BaseAdapter {

    private LinkedList<Negocio> allNegocios;
    private Context context;
    private NegocioSelectedListener negocioSelectedListener;
    private Location location;


    public interface NegocioSelectedListener{
        void OnNegocioClicked(Negocio negocio);
    }

    public void setNegocioSelectedListener(NegocioSelectedListener negocioSelectedListener) {
        this.negocioSelectedListener = negocioSelectedListener;
    }

    public NegocioPorCategoriaAdapter(LinkedList<Negocio> allNegocios, Context context) {
        this.allNegocios = allNegocios;
        this.context = context;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public int getCount() {
        return allNegocios.size();
    }

    @Override
    public Negocio getItem(int i) {
        return allNegocios.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View rootView = inflater.inflate(R.layout.negocio_item_layout,viewGroup,false);
        TextView title = (TextView) rootView.findViewById(R.id.negocio_title);
        TextView distancia = (TextView)rootView.findViewById(R.id.negocio_distancia);
        if(location != null){
            String ubicacion[] = allNegocios.get(i).getUbicacion().split(",");
            if(ubicacion != null && ubicacion.length == 2){
                //Location negocioLocation = new Location("Dummy");
                //negocioLocation.setLatitude(Double.parseDouble(ubicacion[0]));
                //negocioLocation.setLongitude(Double.parseDouble(ubicacion[1]));
                String ditanciaEnMEtros = getDistance(ubicacion[0],ubicacion[1]);
                distancia.setText(ditanciaEnMEtros);

            }
        }
        ImageView negocioIcon = (ImageView) rootView.findViewById(R.id.category_icon);
        title.setText(allNegocios.get(i).getNombre());
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(negocioSelectedListener != null){
                    negocioSelectedListener.OnNegocioClicked(allNegocios.get(i));
                }
            }
        });

        Picasso.with(context).load(allNegocios.get(i).getLogo()).resize(50,50).centerInside().into(negocioIcon);
        return  rootView;
    }



    public static String getDistanceInKm(Location clientLocation, Location businnesLocation) {
        double metros = clientLocation.distanceTo(businnesLocation);
        String distanciaMEtros = "0m";
        if (metros < 1000) {
            distanciaMEtros = "" + metros + "m";
        } else if (metros >= 1000) {
            double kilometros = metros / 1000;
            kilometros = Math.round(kilometros * 100.0) / 100.0;

            distanciaMEtros = "" + kilometros + "km";
        }


        return distanciaMEtros;

    }


    private String getDistance(String lat, String lon){
        Location locationA = new Location("point A");

        locationA.setLatitude(Double.valueOf(lat));
        locationA.setLongitude(Double.valueOf(lon));
        float []results = new float[3];
        float metrosGo = 0;
        Location.distanceBetween(Double.parseDouble(lat),Double.parseDouble(lon),location.getLatitude(),location.getLongitude(),results);
        if(results != null && results.length>=1){
            metrosGo = results[0];
        }
        //double metros = locationA.dist(userCurrentLocation);
        String distanciaMEtros = "0m";

        if (metrosGo < 1000) {
            double roundm = Math.round(metrosGo * 100.0) / 100.0;
            distanciaMEtros = "" + roundm + "m";
        } else if (metrosGo >= 1000) {
            double kilometros = metrosGo / 1000;
            kilometros = Math.round(kilometros * 100.0) / 100.0;

            distanciaMEtros = "" + kilometros + "km";
        }


        return distanciaMEtros;

    }



}
