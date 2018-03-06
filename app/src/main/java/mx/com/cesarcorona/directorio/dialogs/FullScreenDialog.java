package mx.com.cesarcorona.directorio.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import mx.com.cesarcorona.directorio.R;

/**
 * Created by Corona on 5/27/2017.
 */

public class FullScreenDialog extends DialogFragment {


    public static String TAG = FullScreenDialog.class.getSimpleName();
    public static String KEY_IMAGE = "image";
    public static String IMAGE_PATH="path";




    public FullScreenDialog(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog fulldialog = new Dialog(getContext(), R.style.FullScreenDialog);
        fulldialog.setContentView(R.layout.modal_screen);
        ImageView imageDialog = (ImageView)fulldialog.findViewById(R.id.dialog_image);
        ImageView back_arrow_dialog = (ImageView)fulldialog.findViewById(R.id.back_arrow_dialog);
        back_arrow_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        fulldialog.getWindow().setLayout(width, height);
        String path = getArguments().getString(IMAGE_PATH);
        if(path != null && !path.equals("")){
            Picasso.with(getActivity()).load(path).into(imageDialog);

        }


        return fulldialog;
    }
}