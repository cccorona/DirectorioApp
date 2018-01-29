package mx.com.cesarcorona.directorio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.activities.BaseAnimatedActivity;
import mx.com.cesarcorona.directorio.activities.CategoriaActivity;
import mx.com.cesarcorona.directorio.pojo.Negocio;
import mx.com.cesarcorona.directorio.pojo.PremiumBanner;
import mx.com.cesarcorona.directorio.pojo.Promocion;

import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.ALL_NEGOCIO_REFERENCE;

/**
 * Created by ccabrera on 25/10/17.
 */

public class PromosAdapter extends BaseAdapter {

    public static String TAG = PromosAdapter.class.getSimpleName();
    public static int ADAPTER_TYPE_INFO =0;
    public static int ADAPTER_TYPE_EDIT =1;

    private Context context;
    private LinkedList<Promocion> premiumBanners;
    private OnPromoInterface onPromoInterface;
    private int adapterType;






    public interface OnPromoInterface{
        void OnPromoSelected(Promocion promoselected);
        void OnEditSelected(Promocion promocion);
        void OnDeletePromo(Promocion promocion);
    }

    public void setOnPromoInterface(OnPromoInterface onPromoInterface) {
        this.onPromoInterface = onPromoInterface;
    }

    public PromosAdapter(Context context, LinkedList<Promocion> premiumBanners,int adapterType) {
        this.context = context;
        this.premiumBanners = premiumBanners;
        this.adapterType = adapterType;
    }

    @Override
    public int getCount() {
        return premiumBanners.size();
    }

    @Override
    public Promocion getItem(int position) {
        return premiumBanners.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.mi_promocion_item, parent, false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onPromoInterface != null){
                    if(adapterType == ADAPTER_TYPE_INFO){
                        onPromoInterface.OnPromoSelected(premiumBanners.get(position));

                    }
                }
            }
        });

        ImageView editImage= (ImageView) rootView.findViewById(R.id.edit_icon);
        ImageView deleteImage= (ImageView) rootView.findViewById(R.id.delete_icon);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onPromoInterface != null){
                    onPromoInterface.OnEditSelected(premiumBanners.get(position));
                }
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onPromoInterface != null){
                    onPromoInterface.OnDeletePromo(premiumBanners.get(position));
                }
            }
        });


        if(adapterType == ADAPTER_TYPE_INFO){
            editImage.setVisibility(View.GONE);
            deleteImage.setVisibility(View.GONE);

        }else if(adapterType == ADAPTER_TYPE_EDIT){
            editImage.setVisibility(View.VISIBLE);
            deleteImage.setVisibility(View.VISIBLE);

        }






        final ImageView promoBanner = (ImageView) rootView.findViewById(R.id.banner_image);
        if(premiumBanners.get(position).getPhotoUrl()!= null && !premiumBanners.get(position).getPhotoUrl().equals("")){
            Picasso.with(context).load(premiumBanners.get(position).getPhotoUrl()).into(promoBanner);
        }

        return rootView;

    }


    public void addPromocion(Promocion promocion){
        if(!premiumBanners.contains(promocion)){
            premiumBanners.add(promocion);
            notifyDataSetChanged();
        }
    }


}
