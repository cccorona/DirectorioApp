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

import static mx.com.cesarcorona.directorio.activities.CategoriaActivity.ALL_NEGOCIO_REFERENCE;

/**
 * Created by ccabrera on 25/10/17.
 */

public class PromosAdapter extends BaseAdapter {

    public static String TAG = PromosAdapter.class.getSimpleName();

    private Context context;
    private LinkedList<PremiumBanner> premiumBanners;
    private OnPromoInterface onPromoInterface;






    public interface OnPromoInterface{
        void OnPromoSelected(PremiumBanner promoselected);
    }

    public void setOnPromoInterface(OnPromoInterface onPromoInterface) {
        this.onPromoInterface = onPromoInterface;
    }

    public PromosAdapter(Context context, LinkedList<PremiumBanner> premiumBanners) {
        this.context = context;
        this.premiumBanners = premiumBanners;
    }

    @Override
    public int getCount() {
        return premiumBanners.size();
    }

    @Override
    public PremiumBanner getItem(int position) {
        return premiumBanners.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.promo_item_layout, parent, false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onPromoInterface != null){
                    onPromoInterface.OnPromoSelected(premiumBanners.get(position));
                }
            }
        });
        final ImageView promoBanner = (ImageView) rootView.findViewById(R.id.banner_promo);
        final DatabaseReference choosedPremiumNegocioReference = FirebaseDatabase.getInstance().getReference(ALL_NEGOCIO_REFERENCE + "/" +
                premiumBanners.get(position).getDatabaseReference());
        choosedPremiumNegocioReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Negocio negocionOnMainBanner = dataSnapshot.getValue(Negocio.class);
                negocionOnMainBanner.setNegocioDataBaseReference(dataSnapshot.getKey());
                premiumBanners.get(position).setRelatedNegocio(negocionOnMainBanner);
                if (negocionOnMainBanner.getPremiumBannerUrl() == null) {

                    if (negocionOnMainBanner.getUrl_promos() == null) {
                        FirebaseCrash.log(TAG + ": No hay banner en negocio premium");
                        //show default banner
                        Picasso.with(context).load(R.drawable.hi_res_logo).fit().into(promoBanner);
                    } else {
                        for (String key : negocionOnMainBanner.getUrl_promos().keySet()) {
                            Picasso.with(context).load(negocionOnMainBanner.getUrl_promos().get(key)).fit().into(promoBanner);
                            break;
                        }
                    }


                } else {
                    Picasso.with(context).load(negocionOnMainBanner.getPremiumBannerUrl()).fit().into(promoBanner);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //show default banner
                Picasso.with(context).load(R.drawable.hi_res_logo).fit().into(promoBanner);


            }
        });

        return rootView;

    }


}
