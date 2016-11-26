package bcit.androidgpstracking;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class PlanTrip extends AppCompatActivity {

    SQLiteDatabaseHelper db;


    ImageButton startDate;
    ImageButton startTime;
    ImageButton endDate;
    ImageButton endTime;
    int startYear, startMonth, startDay;
    int startHour, startMinute;
    int endYear, endMonth, endDay;
    int endHour, endMinute;

    static final int DIALOG_START_DATE = 0;
    static final int DIALOG_END_DATE = 0;
    static final int DIALOG_START_TIME = 2;
    static final int DIALOG_END_TIME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);


        db = new SQLiteDatabaseHelper(this);

        final Calendar cal = Calendar.getInstance();
        startYear = cal.get(Calendar.YEAR);
        startMonth = cal.get(Calendar.MONTH) + 1;   //plus one because month starts at 0
        startDay = cal.get(Calendar.DAY_OF_MONTH);

        endYear = cal.get(Calendar.YEAR);
        endMonth = cal.get(Calendar.MONTH) + 1;   //plus one because month starts at 0
        endDay = cal.get(Calendar.DAY_OF_MONTH);

        Toast.makeText(PlanTrip.this, startYear + "/" + startMonth + "/" + startDay, Toast.LENGTH_LONG).show();
        showDialogOnButtonClick();
        showTimePickerDialog();

    }
/*
    //Start Date button
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
*/
    public void showDialogOnButtonClick(){
        startDate = (ImageButton)findViewById(R.id.startDate);
        endDate = (ImageButton)findViewById(R.id.endDate);   //validate end date is after/on start date

        startDate.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDialog(DIALOG_START_DATE );
                        Toast.makeText(PlanTrip.this, startYear + "/" + startMonth + "/" + startDay, Toast.LENGTH_LONG).show();
                    }
                }
        );
        endDate.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDialog(DIALOG_END_DATE);
                        Toast.makeText(PlanTrip.this, endYear + "/" + endMonth + "/" + endDay, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void showTimePickerDialog(){
        startTime = (ImageButton)findViewById(R.id.startTime);
        endTime = (ImageButton)findViewById(R.id.endTime);   //validate endTime is after startTime if on the same day

        startTime.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDialog(DIALOG_START_TIME);
                    }
                }
        );
        endTime.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDialog(DIALOG_END_TIME);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_START_DATE )
            return new DatePickerDialog(this, datePickerListener, startYear, startMonth, startDay);
        if (id == DIALOG_END_DATE )
            return new DatePickerDialog(this, datePickerListener, endYear, endMonth, endDay);
        if (id == DIALOG_START_TIME)
            return new TimePickerDialog(PlanTrip.this, startTimePickerListener, startHour, startMinute, false);
        if (id == DIALOG_END_TIME)
            return new TimePickerDialog(PlanTrip.this, startTimePickerListener, endHour, endMinute, false);

        return null;
    }

    protected DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            startYear = year;
            startMonth = month + 1;
            startDay = day;
            Toast.makeText(PlanTrip.this, startYear + "/" + startMonth + "/" + startDay, Toast.LENGTH_LONG).show();
        }
    };

    protected TimePickerDialog.OnTimeSetListener startTimePickerListener
            = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            startHour = hour;
            startMinute = minute;
            Toast.makeText(PlanTrip.this, startHour + ":" + startMinute, Toast.LENGTH_LONG).show();
        }
    };
}
