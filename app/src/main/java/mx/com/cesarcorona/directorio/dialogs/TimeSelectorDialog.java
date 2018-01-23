package mx.com.cesarcorona.directorio.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import mx.com.cesarcorona.directorio.R;


/**
 * Created by ccabrera on 22/10/17.
 */

public class TimeSelectorDialog extends DialogFragment {


    public static String TAG = TimeSelectorDialog.class.getSimpleName();


    private TimePicker timePicker ;
    private OnTimeSelectedInterface onTimeSelectedInterface;
    private TimePicker timepickerView;
    int hour;
    int minutes;


    public interface  OnTimeSelectedInterface{
        void OnTimeChangedDialog(TimePicker view, int hourOfDay, int minute);
    }

    public void setOnTimeSelectedInterface(OnTimeSelectedInterface onTimeSelectedInterface) {
        this.onTimeSelectedInterface = onTimeSelectedInterface;
    }

    public TimeSelectorDialog(){

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
        Dialog fulldialog = new Dialog(getContext(), R.style.FullScreenDialog2);
        fulldialog.setContentView(R.layout.time_selector_layout);
        fulldialog.setCancelable(false);
        Calendar c = Calendar.getInstance();

        timePicker = (TimePicker) fulldialog.findViewById(R.id.time_picker);
        timePicker.setCurrentHour(c.get(Calendar.HOUR));
        timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        timepickerView = timePicker;
        hour =c.get(Calendar.HOUR);
        minutes = c.get(Calendar.MINUTE);

        Button cancelButton =(Button)fulldialog.findViewById(R.id.cancelar_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button aceptarButton =(Button)fulldialog.findViewById(R.id.aceptar_button);
        aceptarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onTimeSelectedInterface != null){
                    onTimeSelectedInterface.OnTimeChangedDialog(timepickerView,hour,minutes);
                }
                dismiss();
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                timepickerView = view;
                hour = hourOfDay;
                minutes = minute;

            }
        });



        return fulldialog;
    }






}