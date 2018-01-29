package mx.com.cesarcorona.directorio.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.adapter.PromosAdapter;
import mx.com.cesarcorona.directorio.pojo.Categoria;
import mx.com.cesarcorona.directorio.pojo.Negocio;
import mx.com.cesarcorona.directorio.pojo.Promocion;

import static mx.com.cesarcorona.directorio.adapter.PromosAdapter.ADAPTER_TYPE_EDIT;

public class PublicarPromocionActivity extends BaseAnimatedActivity  implements PromosAdapter.OnPromoInterface{



    public static final int BANNER = 1;
    public static String TAG = PublicarPromocionActivity.class.getSimpleName();
    public static int EDIT_CODE_INT =990;


    private LinkedList<Negocio> misNegocios;
    private boolean firstTopic = true;
    private Negocio negocioSeleccionado;
    private HashMap<String, String> tagsnegocio;
    private ProgressDialog pDialog;
    private LinkedList<Promocion> misPromociones;
    private ListView misPromocionesList;

    private Uri bannerImageUrl;
    private ImageView attache, banner_image;
    private EditText tagsText;
    private Button publicarButton;
    private PromosAdapter promosAdapter;




    private Spinner negocioSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar_promocion);
        negocioSpinner = (Spinner) findViewById(R.id.negocioSpinner);
        misPromocionesList = (ListView)findViewById(R.id.mis_prommociones_list);
        attache = (ImageView)findViewById(R.id.attach_banner);
        banner_image = (ImageView)findViewById(R.id.banner_image);
        tagsText = (EditText) findViewById(R.id.negocio_tags);
        publicarButton = (Button) findViewById(R.id.publicar_button_negocio) ;
        pDialog = new ProgressDialog(PublicarPromocionActivity.this);
        pDialog.setMessage("Por favor espere");
        pDialog.setCancelable(false);
        promosAdapter = new PromosAdapter(PublicarPromocionActivity.this,new LinkedList<Promocion>(),ADAPTER_TYPE_EDIT);
        promosAdapter.setOnPromoInterface(this);

        misPromocionesList.setAdapter(promosAdapter);
        tagsnegocio = new HashMap<>();

         fillNegocios();


         attache.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i = new Intent(
                         Intent.ACTION_PICK,
                         android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                 startActivityForResult(i, BANNER);
             }
         });



         publicarButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 publicarPromocion();
             }
         });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data && requestCode == BANNER) {

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            bannerImageUrl = data.getData();
            Cursor cursor = getContentResolver().query(data.getData(),
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            banner_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));


        }


        if(resultCode == RESULT_OK && requestCode == EDIT_CODE_INT){
            reloadPromociones();
        }

    }


    public void publicarPromocion(){
        if(tagsText.getText().length() >0 && bannerImageUrl != null){
            final Promocion promocion = new Promocion();
            String tags[] = tagsText.getText().toString().split(",");
            if(tags != null && tags.length >0){
                tagsnegocio = new HashMap<>();
                for(String tag:tags){
                    tagsnegocio.put(tag,tag);
                }
                promocion.setTags(tagsnegocio);

            }
            promocion.setNegocioId(negocioSeleccionado.getNegocioDataBaseReference());
            StorageReference fotoref = FirebaseStorage.getInstance().getReference("promofotos/"+bannerImageUrl.getLastPathSegment());
        UploadTask uploadTask = fotoref.putFile(bannerImageUrl);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PublicarPromocionActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                promocion.setPhotoUrl(taskSnapshot.getDownloadUrl().toString());
                promocion.setUbicacion(negocioSeleccionado.getUbicacion());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Example/promociones");
                String keyTopush = "promocion" + databaseReference.push();
                databaseReference.child(keyTopush).setValue(promocion).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }else {
                            Toast.makeText(PublicarPromocionActivity.this,"No se pudo publicar la promoción, intente despues",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


        }else {
            Toast.makeText(PublicarPromocionActivity.this,"Rellena la imagen y los tags",Toast.LENGTH_LONG).show();
        }
    }


    private void fillNegocios() {
        showpDialog();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Example/allnegocios");

        Query query = databaseReference.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Negocio selectATopi = new Negocio();
        selectATopi.setNombre("Seleccione un negocio");
        misNegocios.add(selectATopi);



        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot negocioSnap : dataSnapshot.getChildren()) {
                    Negocio negocio = negocioSnap.getValue(Negocio.class);
                    negocio.setNegocioDataBaseReference(negocioSnap.getKey());
                    misNegocios.add(negocio);
                }

                LinkedList<String> list = new LinkedList<String>();
                for (Negocio topic : misNegocios) {
                    list.add(topic.getNombre());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PublicarPromocionActivity.this,
                        R.layout.spinner_style, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                negocioSpinner.setAdapter(dataAdapter);

                hidepDialog();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidepDialog();

            }
        });

        negocioSpinner.setOnItemSelectedListener(new PublicarPromocionActivity.mySpinnerListener());


    }

    @Override
    public void OnPromoSelected(Promocion promoselected) {

    }

    @Override
    public void OnEditSelected(Promocion promocion) {
        Intent editIntent = new Intent(PublicarPromocionActivity.this,EditPromoActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(EditPromoActivity.KEY_PROMO_SELECTED,promocion);
        editIntent.putExtras(extras);
        startActivityForResult(editIntent,EDIT_CODE_INT);

    }

    @Override
    public void OnDeletePromo(Promocion promocion) {
         FirebaseDatabase.getInstance().getReference("Example/promociones")
                  .child(promocion.getDataBasereference()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful()){
                     reloadPromociones();

                 }else{
                     Toast.makeText(PublicarPromocionActivity.this,"No se pudo eliminar la promocion, intente mas tarde",Toast.LENGTH_LONG).show();

                 }
             }
         });

    }


    class mySpinnerListener implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView parent, View v, int position,
                                   long id) {


            if (firstTopic) {
                firstTopic = false;
                return;
            } else {
                if (position > 0) {

                    negocioSeleccionado = misNegocios.get(position);
                    reloadPromociones();

                } else {
                    negocioSeleccionado = null;


                }
            }


        }


        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    private void reloadPromociones(){
        if(negocioSeleccionado != null){
            misPromociones = new LinkedList<>();
            promosAdapter = new PromosAdapter(PublicarPromocionActivity.this,new LinkedList<Promocion>(),ADAPTER_TYPE_EDIT);
            promosAdapter.setOnPromoInterface(this);
            misPromocionesList.setAdapter(promosAdapter);
            DatabaseReference promosReference = FirebaseDatabase.getInstance().getReference("Example/promociones");
            Query query = promosReference.orderByChild("negocioId").equalTo(negocioSeleccionado.getNegocioDataBaseReference());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot promoStap:dataSnapshot.getChildren()){
                        Promocion promocion = promoStap.getValue(Promocion.class);
                        promocion.setDataBasereference(promoStap.getKey());
                        promosAdapter.addPromocion(promocion);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
