package bcit.androidgpstracking;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class PlanTrip extends AppCompatActivity {

    SQLiteDatabaseHelper db;

    Button startDate;
    Button startTime;
    Button endDate;
    Button endTime;
    EditText frequencyNumberEditText;
    Spinner frequencyTypeSpinner;
    Button contacts;
    Button save;
    EditText tripNameEdit;

    TextView startView;
    TextView endView;

    int startYear, startMonth, startDay;
    int startHour, startMinute;
    String start;
    String startDateInput;
    String startTimeInput;

    int endYear, endMonth, endDay;
    int endHour, endMinute;
    String end;
    String endDateInput;
    String endTimeInput;

    String frequencyNumberInput;
    String frequencyTypeInput;
    String contactsInput;
    int trip_id = 2;
    String trip_name;

    static final int DIALOG_START_DATE = 0;
    static final int DIALOG_END_DATE = 1;
    static final int DIALOG_START_TIME = 2;
    static final int DIALOG_END_TIME = 3;

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

        //default frequencyNumber
        frequencyNumberEditText = (EditText) findViewById(R.id.frequencyNumber);
        tripNameEdit = (EditText) findViewById(R.id.trip_name);
        startView = (TextView) findViewById(R.id.tripStartView);
        endView = (TextView) findViewById(R.id.tripEndView);

        //default frequencyType
        frequencyTypeInput = "hour";
        frequencyTypeSpinner = (Spinner) findViewById(R.id.frequencyTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_options, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        frequencyTypeSpinner.setAdapter(adapter);
        frequencyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(),parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                frequencyTypeInput = parent.getItemAtPosition(position).toString();
                //Toast.makeText(PlanTrip.this, frequencyNumberInput + " FREQU", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startShowDialogOnButtonClick();
        endShowDialogOnButtonClick();
        startShowTimePickerDialog();
        endShowTimePickerDialog();

    }

    public void startShowDialogOnButtonClick() {
        startDate = (Button) findViewById(R.id.startDate);
        endDate = (Button) findViewById(R.id.endDate);   //validate end date is after/on start date

        startDate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_START_DATE);
                        startDateInput = startYear + "/" + startMonth + "/" + startDay;
                        //Toast.makeText(PlanTrip.this, startYear + "/" + startMonth + "/" + startDay, Toast.LENGTH_LONG).show();
                        Toast.makeText(PlanTrip.this, "showD start: " + startDateInput, Toast.LENGTH_LONG).show();
                        startView.setText(startDateInput);
                    }
                }
        );
    }
    public void endShowDialogOnButtonClick(){
        endDate.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDialog(DIALOG_END_DATE);
                        endDateInput = endYear + "/" + endMonth + "/" + endDay;
                        Toast.makeText(PlanTrip.this, "showD end: " + endYear + "/" + endMonth + "/" + endDay, Toast.LENGTH_LONG).show();
                        endView.setText(endDateInput);
                    }
                }
        );
    }

    public void startShowTimePickerDialog() {
        startTime = (Button) findViewById(R.id.startTime);
        endTime = (Button) findViewById(R.id.endTime);   //validate endTime is after startTime if on the same day

        startTime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_START_TIME);
                        startTimeInput = startHour + ":" + startMinute;
                        Toast.makeText(PlanTrip.this, "showT start: " + startTimeInput, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public void endShowTimePickerDialog(){
        endTime.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDialog(DIALOG_END_TIME);
                        endTimeInput = endHour + ":" + endMinute;
                        Toast.makeText(PlanTrip.this, "showT end: " + endTimeInput, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_START_DATE )
            return new DatePickerDialog(this, startDatePickerListener, startYear, startMonth, startDay);
        if (id == DIALOG_END_DATE )
            return new DatePickerDialog(this, endDatePickerListener, endYear, endMonth, endDay);
        if (id == DIALOG_START_TIME)
            return new TimePickerDialog(PlanTrip.this, startTimePickerListener, startHour, startMinute, false);
        if (id == DIALOG_END_TIME)
            return new TimePickerDialog(PlanTrip.this, endTimePickerListener, endHour, endMinute, false);

        return null;
    }

    protected DatePickerDialog.OnDateSetListener startDatePickerListener
            = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            startYear = year;
            startMonth = month + 1;
            startDay = day;
            Toast.makeText(PlanTrip.this, startYear + "/" + startMonth + "/" + startDay, Toast.LENGTH_LONG).show();
        }
    };

    protected DatePickerDialog.OnDateSetListener endDatePickerListener
            = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            endYear = year;
            endMonth = month + 1;
            endDay = day;
            Toast.makeText(PlanTrip.this, endYear + "/" + endMonth + "/" + endDay, Toast.LENGTH_LONG).show();
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

    protected TimePickerDialog.OnTimeSetListener endTimePickerListener
            = new TimePickerDialog.OnTimeSetListener(){

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            endHour = hour;
            endMinute = minute;
            Toast.makeText(PlanTrip.this, endHour + ":" + endMinute, Toast.LENGTH_LONG).show();
        }
    };

    public void frequencyOnClick(final View view){
//        frequencyNumberInput = Integer.parseInt(frequencyNumber.getText().toString());
    }

    public void contacts(final View view){
        //JA - calls activity to get contact list
        Intent intent = new Intent(this, contactListActivity.class);
        startActivityForResult(intent, 1);
//        Toast.makeText(PlanTrip.this, "adding: Jacob Frank", Toast.LENGTH_LONG).show();
        //contactsInput = "Jacob Frank";
        //Toast.makeText(PlanTrip.this, "start: " + startDateInput + " " + startTimeInput + "\nend: " + endDateInput + " " + endTimeInput, Toast.LENGTH_LONG).show();
    }

    //Method called when user selects contacts
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> numbers = data.getStringArrayListExtra("numbers");
                contactsInput = Arrays.toString(numbers.toArray());
                //Store numbers into database here
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    /*
    To Do:
        -increment Trip_id every time save is successful
     */
    public void save(final View view){
        startDateInput = startYear + "/" + startMonth + "/" + startDay;
        endDateInput = endYear + "/" + endMonth + "/" + endDay;
        startTimeInput = startHour + ":" + startMinute;
        endTimeInput = endHour + ":" + endMinute;

        start =  startDateInput + " " + startTimeInput;
        end = endDateInput + " " + endTimeInput;

        startView.setText(start);
        endView.setText(end);

        frequencyNumberInput = frequencyNumberEditText.getText().toString();

        trip_name = tripNameEdit.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("ID", trip_id);

        if (db.insertData(trip_id, "", "", start, end, frequencyNumberInput, frequencyTypeInput, contactsInput, trip_name)) {
            Toast.makeText(this, "Insert successful", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK, intent);
        } else {
            Toast.makeText(this, "Insert FAILED, YOUR WROONG", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED, intent);
        }

        finish();
    }
}



