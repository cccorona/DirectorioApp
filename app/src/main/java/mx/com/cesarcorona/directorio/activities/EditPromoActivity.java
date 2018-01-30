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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.LinkedList;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.pojo.Promocion;

import static mx.com.cesarcorona.directorio.activities.PublicarPromocionActivity.EDIT_CODE_INT;

public class EditPromoActivity extends BaseAnimatedActivity {

    public static String TAG = EditPromoActivity.class.getSimpleName();
    public static String KEY_PROMO_SELECTED = "promo";

    public static final int BANNER = 1;


    private Promocion promoSelected;
    private Uri bannerImageUrl;
    private ImageView attache, banner_image;
    private EditText tagsText;
    private Button publicarButton;


    private HashMap<String, String> tagsnegocio;
    private ProgressDialog pDialog;
    private LinkedList<Promocion> misPromociones;
    private ListView misPromocionesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_promo);


        promoSelected = (Promocion) getIntent().getExtras().getSerializable(KEY_PROMO_SELECTED);
        banner_image = (ImageView)findViewById(R.id.banner_image);
        tagsText = (EditText) findViewById(R.id.negocio_tags);
        attache = (ImageView)findViewById(R.id.attach_banner);

        publicarButton = (Button) findViewById(R.id.publicar_button_negocio) ;
        pDialog = new ProgressDialog(EditPromoActivity.this);
        pDialog.setMessage("Por favor espere");
        pDialog.setCancelable(false);
        tagsnegocio = new HashMap<>();

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

        if(promoSelected.getPhotoUrl()!= null && !promoSelected.getPhotoUrl().equals("")){
            Picasso.with(EditPromoActivity.this).load(promoSelected.getPhotoUrl()).into(banner_image);

        }

        if(promoSelected.getTags()!=null){
            StringBuilder tags = new StringBuilder();
            for(String tag:promoSelected.getTags().keySet()){
                tags.append(tag).append(",");
            }
            tags = new StringBuilder( tags.subSequence(0,tags.length()-1).toString());
            tagsText.setText(tags.toString());
        }



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

    }



    public void publicarPromocion(){
        if(tagsText.getText().length() >0){
            String tags[] = tagsText.getText().toString().split(",");
            if(tags != null && tags.length >0){
                tagsnegocio = new HashMap<>();
                for(String tag:tags){
                    tagsnegocio.put(tag.trim().toLowerCase(),tag.trim().toLowerCase());
                }
                promoSelected.setTags(tagsnegocio);

            }

            if(bannerImageUrl != null){//image change
                StorageReference fotoref = FirebaseStorage.getInstance().getReference("promofotos/"+bannerImageUrl.getLastPathSegment());
                UploadTask uploadTask = fotoref.putFile(bannerImageUrl);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(EditPromoActivity.this,exception.getMessage(),Toast.LENGTH_LONG).show();
                        return;
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        promoSelected.setPhotoUrl(taskSnapshot.getDownloadUrl().toString());
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Example/promociones/"+promoSelected.getDataBasereference());
                        databaseReference.setValue(promoSelected).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(EditPromoActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                }else {
                                    Toast.makeText(EditPromoActivity.this,"Se actualizo correctamente la promo",Toast.LENGTH_LONG).show();
                                    setResult(RESULT_OK);
                                    finishActivity(EDIT_CODE_INT);
                                }
                            }
                        });
                    }
                });
            }else{
                //just tag changed
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Example/promociones/"+promoSelected.getDataBasereference());
                databaseReference.child("tags").setValue(promoSelected.getTags()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(EditPromoActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                        }else {
                            Toast.makeText(EditPromoActivity.this,"Se actualizo correctamente la promo",Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finishActivity(EDIT_CODE_INT);
                        }
                    }
                });
            }



        }else {
            Toast.makeText(EditPromoActivity.this,"Rellena la imagen y los tags",Toast.LENGTH_LONG).show();
        }
    }

}
