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
import mx.com.cesarcorona.directorio.pojo.Noticia;

/**
 * Created by ccabrera on 11/12/17.
 */

public class NoticiasAdapter  extends BaseAdapter{



    private LinkedList<Noticia> noticias;
    private Context context;
    private OnNewsInterface onNewsInterface;


    public interface  OnNewsInterface{
        void OnNewClicked(Noticia noticia);
    }


    public NoticiasAdapter(LinkedList<Noticia> noticias, Context context) {
        this.noticias = noticias;
        this.context = context;
    }

    public void setOnNewsInterface(OnNewsInterface onNewsInterface) {
        this.onNewsInterface = onNewsInterface;
    }

    @Override
    public int getCount() {
        return noticias.size();
    }

    @Override
    public Noticia getItem(int position) {
        return noticias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.noticia_layout_item,parent,false);
        ImageView previewImage = (ImageView)rootView.findViewById(R.id.preview_image);
        TextView previewText = (TextView)rootView.findViewById(R.id.preview_text);
        TextView linkText = (TextView)rootView.findViewById(R.id.complete_link);

        if(noticias.get(position).getFotoUrl() == null || noticias.get(position).getFotoUrl().equals("")){
            Picasso.with(context).load(R.drawable.noticias).into(previewImage);
        }else{
            Picasso.with(context).load(noticias.get(position).getFotoUrl()).centerCrop().resize(100,200).into(previewImage);

        }

        previewText.setText(noticias.get(position).getTextoContenido());
        linkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onNewsInterface != null){
                    onNewsInterface.OnNewClicked(noticias.get(position));
                }
            }
        });

        return  rootView;


    }
}
