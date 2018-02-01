package mx.com.cesarcorona.directorio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.pojo.Clasificado;

/**
 * Created by ccabrera on 27/11/17.
 */

public class ClasificadosAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<Clasificado> clasificados;


    public ClasificadosAdapter(Context context, LinkedList<Clasificado> clasificados) {
        this.context = context;
        this.clasificados = clasificados;
    }

    @Override
    public int getCount() {
        return clasificados.size();
    }

    @Override
    public Clasificado getItem(int position) {
        return clasificados.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = LayoutInflater.from(context).inflate(R.layout.clasificado_layout,parent,false);
        TextView title = (TextView) root.findViewById(R.id.clasificado_title);
        TextView contenido = (TextView) root.findViewById(R.id.clasificado_contenido);
        TextView categoria = (TextView) root.findViewById(R.id.categoria_clasificado);
        TextView telefono = (TextView)root.findViewById(R.id.telefono_clasificado);

        title.setText(clasificados.get(position).getTitulo());
        contenido.setText(clasificados.get(position).getContenido());
        categoria.setText(clasificados.get(position).getReferencia_categoria());
        telefono.setText("Tel:"+clasificados.get(position).getTelefono());
        return root;

    }
}
