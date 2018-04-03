package mx.com.cesarcorona.directorio.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.pojo.FechaEspecial;

/**
 * Created by ccabrera on 13/03/18.
 */

public class DiasEspecialesAdapter extends RecyclerView.Adapter<DiasEspecialesAdapter.ViewHolder> {


    private Context context;
    private LinkedList<FechaEspecial> diasEspeciales;
    private OnServicioSelected onServicioSelected;




    public interface OnServicioSelected{
        void ONDeleteServicio(FechaEspecial diaEspecial);
    }

    public void setOnServicioSelected(OnServicioSelected onServicioSelected) {
        this.onServicioSelected = onServicioSelected;
    }

    public DiasEspecialesAdapter(Context context, LinkedList<FechaEspecial> diasEspeciales) {
        this.context = context;
        this.diasEspeciales = diasEspeciales;
    }

    @Override
    public DiasEspecialesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if ( parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.servicio_item_layout, parent, false);
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            return new DiasEspecialesAdapter.ViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }





    @Override
    public void onBindViewHolder(DiasEspecialesAdapter.ViewHolder holder, final int position) {





        holder.name.setText(getDate(diasEspeciales.get(position).getFecha()));
        holder.horaCierre.setText(diasEspeciales.get(position).getHoraCierre());
        holder.horaAbre.setText(diasEspeciales.get(position).getHoraApertura());

        holder.iconCierra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onServicioSelected != null){
                    onServicioSelected.ONDeleteServicio(diasEspeciales.get(position));
                }
                diasEspeciales.remove(position);
                notifyDataSetChanged();
            }
        });



    }

    @Override
    public int getItemCount() {
        return diasEspeciales.size();
    }







    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public View mView;
        public TextView horaCierre;
        public TextView horaAbre;
        public ImageView iconCierra;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            name = (TextView) itemView.findViewById(R.id.dia_seleccionado);
            horaCierre = (TextView) itemView.findViewById(R.id.hora_cierra);
            horaAbre = (TextView) itemView.findViewById(R.id.hora_abre);
            iconCierra = (ImageView) itemView.findViewById(R.id.delete_icon);




        }
    }


    public void addDiaEspecial(FechaEspecial fechaEspecial){
        diasEspeciales.add(fechaEspecial);
        notifyDataSetChanged();
    }

    public void removeDiaESpecial(FechaEspecial fechaEspecial){
        diasEspeciales.remove(fechaEspecial);
        notifyDataSetChanged();
    }


    public boolean containsFEcha(FechaEspecial fechaEspecial){

        boolean loContiene = false;
        for(FechaEspecial fechaEnLista:diasEspeciales){
            if(fechaEnLista.getFecha() == fechaEspecial.getFecha()){
                loContiene = true;
                break;
            }
        }

        return  loContiene;

    }



    private String getDate(double timestapm){
        Date hora = new Date((long)timestapm);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
        String date = simpleDateFormat.format(hora);
        String parst[] = date.split(" ");
        if(parst!= null && parst.length >3){
            return parst[0] +" " + parst[1] + " " +parst[2];

        }else{
            return  date;
        }
    }


    public LinkedList<FechaEspecial> getDiasEspeciales() {
        return diasEspeciales;
    }
}