package com.example.contactsmanager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.Calendar;

public class AddContactsActivity extends AppCompatActivity {

    TextInputEditText name;
    TextInputEditText phoneNumber;
    TextInputEditText emailAdr;
    TextInputEditText homeAdr;
    TextInputEditText websiteAdr;
    TextView birthDateText;
    TextView callTimeText;
    TextView callDayText;
    ImageView contactImage;

    String imgUri = null; // Uri of the profile picture
    // opens camera and take picture
    ActivityResultLauncher<String[]> galleryLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri o) {
            // makes the Uri persistent (stay the same even when app dies)
            getContentResolver().takePersistableUriPermission(o, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            contactImage.setPadding(0, 0, 0, 0); // remove padding
            contactImage.setImageURI(o);
            imgUri = o.toString(); // saves the uri as a string
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        // finds all the items by ID
        name = findViewById(R.id.contact_name);
        phoneNumber  = findViewById(R.id.contact_phone_num);
        emailAdr = findViewById(R.id.contact_email_adr);
        homeAdr = findViewById(R.id.contact_home_adr);
        websiteAdr = findViewById(R.id.contact_website);
        contactImage = findViewById(R.id.contact_image);
        birthDateText = findViewById(R.id.contact_birth_date);
        callTimeText = findViewById(R.id.contact_call_time);
        callDayText = findViewById(R.id.contact_call_day);

        // Takes everything written in the textViews and the image uri and creates a new contact with it
        Button saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // turns all textViews to strings
                String nameStr = name.getText().toString();
                String phoneNumberStr  = phoneNumber.getText().toString();
                String contactImageStr = imgUri;
                String emailAdrStr = emailAdr.getText().toString();
                String homeAdrStr = homeAdr.getText().toString();
                String websiteAdrStr = websiteAdr.getText().toString();
                String birthDateStr = birthDateText.getText().toString();
                String callTimeStr = callTimeText.getText().toString();
                String callDayStr = callDayText.getText().toString();

                // makes sure name or number aren't empty
                if (nameStr.equals("")) {
                    Toast.makeText(AddContactsActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (phoneNumberStr.equals("")) {
                    Toast.makeText(AddContactsActivity.this, "Number cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    // creates a new contact
                    Contact contact = new Contact(nameStr, phoneNumberStr, contactImageStr, emailAdrStr, homeAdrStr, websiteAdrStr, birthDateStr, callTimeStr, callDayStr);

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();

                            // adds contact to the database
                            AppDatabase.getInstance(AddContactsActivity.this).contactDao().addContact(contact);
                        }
                    }.start();

                    // goes back to the main activity (the contacts list)
                    Intent intent = new Intent(AddContactsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        // starts the gallery launcher which chooses a photo and sets it as a profile photo
        contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch(new String[]{"image/*"}); // launches gallery launcher
            }
        });

        Calendar calender = Calendar.getInstance(); // creates a calender
        Button birthDateBtn = findViewById(R.id.birth_date_btn);
        // opens a date picking dialog and sets the chosen date in the corresponding textView
        birthDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddContactsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // makes the month and dayOfMonth always two-digit
                        DecimalFormat df = new DecimalFormat("00");
                        String monthStr = df.format(month + 1);
                        String dayOfMonthStr = df.format(dayOfMonth);

                        birthDateText.setText(dayOfMonthStr + "/" + monthStr + "/" + year);
                    }
                }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        Button callTimeBtn = findViewById(R.id.call_time_btn);
        // opens a time picking dialog and sets the chosen time in the corresponding textView
        callTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddContactsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // makes the hourOfDay and minute always two-digit
                        DecimalFormat df = new DecimalFormat("00");
                        String hourOfDayStr = df.format(hourOfDay);
                        String minuteStr = df.format(minute);

                        callTimeText.setText(hourOfDayStr + ":" + minuteStr);
                    }
                }, calender.get(Calendar.HOUR), calender.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        Button callDayBtn = findViewById(R.id.call_day_btn);
        // creates a string of all the days chosen, when a day is chosen again it is removed from string
        callDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AddContactsActivity.this);
                builder.setTitle("Pick a Day")
                        .setItems(daysOfWeek, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedDay = daysOfWeek[which]; // name of selected day
                                String chosenDays = callDayText.getText().toString(); // current chosen days string
                                if (chosenDays.contains(selectedDay)) {
                                    // if current chosen day has already been chosen
                                    // then it removed the day from the string
                                    chosenDays = chosenDays.replace(", " + selectedDay, "");
                                    chosenDays = chosenDays.replace(selectedDay + ", ", "");
                                    chosenDays = chosenDays.replace(selectedDay, "");
                                    callDayText.setText(chosenDays);
                                } else if (chosenDays.equals("")){
                                    // if no days have been chosen yet then this is first item in the string
                                    callDayText.setText(selectedDay);
                                } else { // otherwise, the day is added to the string
                                    callDayText.setText(chosenDays + ", " + selectedDay);
                                }
                            }
                        });
                builder.show();
            }
        });


        Button cancelBtn = findViewById(R.id.cancel_btn);
        // goes back to the main activity without saving anything
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // goes back to home page
                Intent intent = new Intent(AddContactsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}