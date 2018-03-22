package mx.com.cesarcorona.directorio.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mx.com.cesarcorona.directorio.R;
import mx.com.cesarcorona.directorio.pojo.FechaEspecial;

/**
 * Created by ccabrera on 09/03/18.
 */

public class SpecialDayDialog extends DialogFragment {


    public static String TAG = SpecialDayDialog.class.getSimpleName();
    public static String KEY_IMAGE = "image";
    public static String IMAGE_PATH="path";



    private FechaEspecial fechaEspecial;
    private SimpleDateFormat dateFormatForMonth;
    private TextView current_date;
    private String horaCierra,horaAbre;

    private long fechaSeleccionada;
    private OnUpdateESpecialDayInterfac onUpdateESpecialDayInterfac;





    public interface OnUpdateESpecialDayInterfac{
        void OnAddEspecialDay(FechaEspecial fechaEspecial);
    }


    public void setOnUpdateESpecialDayInterfac(OnUpdateESpecialDayInterfac onUpdateESpecialDayInterfac) {
        this.onUpdateESpecialDayInterfac = onUpdateESpecialDayInterfac;
    }

    public SpecialDayDialog(){

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
        final Dialog fulldialog = new Dialog(getContext(), R.style.FullScreenDialog);
        fechaSeleccionada = 0;
        dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

        fechaEspecial = new FechaEspecial();


        fulldialog.setContentView(R.layout.special_day_layout);
        final RadioGroup myradioGrupp = (RadioGroup) fulldialog.findViewById(R.id.myRadioGroup24);
        Button agregarButton = (Button) fulldialog.findViewById(R.id.publicar_button_negocio);
        Button cancelarButton = (Button)fulldialog.findViewById(R.id.cancelar_buttpm);


        agregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fechaSeleccionada >0){
                    if(myradioGrupp.getCheckedRadioButtonId() == R.id.yes24){
                        if(horaAbre != null && horaCierra != null && !horaAbre.equals("") && !horaCierra.equals("")){
                            fechaEspecial.setFecha(fechaSeleccionada);
                            fechaEspecial.setHoraApertura(horaAbre);
                            fechaEspecial.setHoraCierre(horaCierra);
                            fechaEspecial.setAbierto(true);
                            if(onUpdateESpecialDayInterfac != null){
                                onUpdateESpecialDayInterfac.OnAddEspecialDay(fechaEspecial);
                                dismiss();
                            }
                        }else{
                            Toast.makeText(getActivity(),"Seleccione la hora de apertura y cierre",Toast.LENGTH_LONG).show();

                        }
                    }else if(myradioGrupp.getCheckedRadioButtonId() == R.id.no24){
                        fechaEspecial.setFecha(fechaSeleccionada);
                        fechaEspecial.setAbierto(false);
                        if(onUpdateESpecialDayInterfac != null){
                            onUpdateESpecialDayInterfac.OnAddEspecialDay(fechaEspecial);
                            dismiss();
                        }
                    }
                }else{
                    Toast.makeText(getActivity(),"Seleccione una fecha",Toast.LENGTH_LONG).show();
                }


            }
        });


        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fulldialog.dismiss();
            }
        });

        final TextView la = (TextView) fulldialog.findViewById(R.id.la);
        final TextView lc = (TextView) fulldialog.findViewById(R.id.lc);

        myradioGrupp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.yes24){
                    fechaEspecial.setAbierto(true);
                    la.setEnabled(true);
                    lc.setEnabled(true);
                } else if(checkedId == R.id.no24){
                    fechaEspecial.setAbierto(false);
                    la.setEnabled(false);
                    lc.setEnabled(false);

                }

            }
        });



        la.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimeSelectorDialog timeSelectorDialog = new TimeSelectorDialog();
                timeSelectorDialog.setOnTimeSelectedInterface(new TimeSelectorDialog.OnTimeSelectedInterface() {
                    @Override
                    public void OnTimeChangedDialog(TimePicker view, int hourOfDay, int minute) {

                        Calendar rightNow = Calendar.getInstance();
                        Date now =Calendar.getInstance().getTime();
                        int hourNow = rightNow.get(Calendar.HOUR_OF_DAY);


                        boolean isPassedTime = false;
                        if(hourNow>hourOfDay){
                            isPassedTime = true;
                        }
                        String aMpM = "AM";
                        if(hourOfDay >11)
                        {
                            aMpM = "PM";
                        }
                        //Make the 24 hour time format to 12 hour time format
                        int currentHour;
                        if(hourOfDay>11)
                        {
                            currentHour = hourOfDay - 12;
                        }
                        else
                        {
                            currentHour = hourOfDay;
                        }

                        String formatedCurrentHour = ""+ hourOfDay;
                        String formatedMinute =""+minute;

                        if(hourOfDay < 10){
                            formatedCurrentHour = "0"+hourOfDay;
                        }

                        if(minute < 10){
                            formatedMinute = "0"+minute;
                        }

                        if(hourOfDay == 0){
                            hourOfDay = 24;
                        }


                        String hourSelected = String.valueOf(currentHour)
                                + " : " + String.valueOf(minute) + " " + aMpM;
                        horaAbre = formatedCurrentHour+":"+formatedMinute;
                        la.setText(hourSelected);

                    }
                });
                timeSelectorDialog.show(getChildFragmentManager(),TimeSelectorDialog.TAG);

            }
        });



        lc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeSelectorDialog timeSelectorDialog = new TimeSelectorDialog();
                timeSelectorDialog.setOnTimeSelectedInterface(new TimeSelectorDialog.OnTimeSelectedInterface() {
                    @Override
                    public void OnTimeChangedDialog(TimePicker view, int hourOfDay, int minute) {

                        Calendar rightNow = Calendar.getInstance();
                        Date now =Calendar.getInstance().getTime();
                        int hourNow = rightNow.get(Calendar.HOUR_OF_DAY);


                        boolean isPassedTime = false;
                        if(hourNow>hourOfDay){
                            isPassedTime = true;
                        }
                        String aMpM = "AM";
                        if(hourOfDay >11)
                        {
                            aMpM = "PM";
                        }
                        //Make the 24 hour time format to 12 hour time format
                        int currentHour;
                        if(hourOfDay>11)
                        {
                            currentHour = hourOfDay - 12;
                        }
                        else
                        {
                            currentHour = hourOfDay;
                        }

                        String formatedCurrentHour = ""+ hourOfDay;
                        String formatedMinute =""+minute;

                        if(hourOfDay < 10){
                            formatedCurrentHour = "0"+hourOfDay;
                        }

                        if(minute < 10){
                            formatedMinute = "0"+minute;
                        }

                        if(hourOfDay == 0){
                            hourOfDay = 24;
                        }


                        String hourSelected = String.valueOf(currentHour)
                                + " : " + String.valueOf(minute) + " " + aMpM;
                        horaCierra = formatedCurrentHour+":"+formatedMinute;
                        lc.setText(hourSelected);

                    }
                });
                timeSelectorDialog.show(getChildFragmentManager(),TimeSelectorDialog.TAG);
            }
        });


        CompactCalendarView  calendarView = (CompactCalendarView) fulldialog.findViewById(R.id.compactcalendar_view);
         current_date = (TextView) fulldialog.findViewById(R.id.current_date);
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                //fechaEspecial.setFecha(dateClicked.getTime());
                fechaSeleccionada = dateClicked.getTime();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                current_date.setText(dateFormatForMonth.format(firstDayOfNewMonth));


            }
        });
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        fulldialog.getWindow().setLayout(width, height);



        return fulldialog;
    }
}