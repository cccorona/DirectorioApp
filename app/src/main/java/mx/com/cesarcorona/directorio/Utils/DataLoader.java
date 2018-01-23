package mx.com.cesarcorona.directorio.Utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.LinkedList;

/**
 * Created by ccabrera on 19/01/18.
 */

public class DataLoader {


    private LinkedList<Uri> filestoUpload;
    private Context context;
    private UploadTaskInterface uploadTaskInterface;
    private int errorCounter;
    private int totalFiles ;
    private int fileCounter;


    public static String PROMOS_PHOTOS_REFRENCE = "fotos";



    public void setUploadTaskInterface(UploadTaskInterface uploadTaskInterface) {
        this.uploadTaskInterface = uploadTaskInterface;
    }

    public interface  UploadTaskInterface {
        void OnSucess();
        void OnError(String message);
    }


    public DataLoader(LinkedList<Uri> filestoUpload, Context context) {
        this.filestoUpload = filestoUpload;
        this.context = context;
        this.errorCounter = 0;
        this.totalFiles = filestoUpload.size();
        this.fileCounter = 0;
    }


    public void execute(){
        for(Uri ruta:filestoUpload){
            StorageReference fotoref = FirebaseStorage.getInstance().getReference(PROMOS_PHOTOS_REFRENCE+"/"+ruta.getLastPathSegment());
            UploadTask uploadTask = fotoref.putFile(ruta);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    errorCounter++;
                    fileCounter++;
                    if(fileCounter == totalFiles && errorCounter > 0){
                        if(uploadTaskInterface != null){
                            uploadTaskInterface.OnError(""+errorCounter +"Archivos no pudieron ser subidos ");
                        }
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                   // bannerURL = taskSnapshot.getDownloadUrl();
                   // uploadAttemp();
                }
            });
        }

    }
}
