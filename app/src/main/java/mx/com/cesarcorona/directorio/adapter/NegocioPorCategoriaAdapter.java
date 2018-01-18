package mx.com.cesarcorona.directorio.adapter;

import android.content.Context;
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
}
