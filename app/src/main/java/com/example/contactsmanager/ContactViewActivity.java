package com.example.contactsmanager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

public class ContactViewActivity extends AppCompatActivity {

    TextView name;
    TextView phoneNumber;
    TextView emailAdr;
    TextView homeAdr;
    TextView websiteAdr;
    TextView birthDateText;
    TextView callTimeText;
    TextView callDayText;
    ImageView contactImage;

    String imgUri;

    int contactId;

    Handler handler = new Handler(Looper.getMainLooper());

    ActivityResultLauncher<String> callRequestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            if(o) {
                // if permission is granted then app calls the number
                String phone = phoneNumber.getText().toString();

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            } else {
                // if not granted then app opens the dialer with the number inside
                String phone = phoneNumber.getText().toString();

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_view);

        // finds all items by ID
        name = findViewById(R.id.contact_name);
        phoneNumber  = findViewById(R.id.contact_phone_num);
        emailAdr = findViewById(R.id.contact_email_adr);
        homeAdr = findViewById(R.id.contact_home_adr);
        websiteAdr = findViewById(R.id.contact_website);
        birthDateText = findViewById(R.id.contact_birth_date);
        callTimeText = findViewById(R.id.contact_call_time);
        callDayText = findViewById(R.id.contact_call_day);
        contactImage = findViewById(R.id.contact_image);

        // gets from the intent the info that needs to be inside each item
        name.setText(getIntent().getStringExtra("name"));
        phoneNumber.setText(getIntent().getStringExtra("phoneNumber"));
        emailAdr.setText(getIntent().getStringExtra("emailAdr"));
        homeAdr.setText(getIntent().getStringExtra("homeAdr"));
        websiteAdr.setText(getIntent().getStringExtra("websiteAdr"));
        birthDateText.setText(getIntent().getStringExtra("birthDate"));
        callTimeText.setText(getIntent().getStringExtra("callTime"));
        callDayText.setText(getIntent().getStringExtra("callDays"));
        imgUri = getIntent().getStringExtra("picture");
        // only if a photo has been chosen (Uri is not null)
        if (imgUri != null) {
            contactImage.setPadding(0, 0, 0, 0); // remove padding
            contactImage.setImageURI(Uri.parse(imgUri));
        } else {
            contactImage.setImageResource(R.drawable.baseline_person_24);
        }

        Button returnBtn = findViewById(R.id.back_btn);
        // goes back to main activity
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        contactId = getIntent().getIntExtra("id",0);
        Button deleteBtn = findViewById(R.id.delete_btn);
        // deletes the contact
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // shows a dialog asking if user is sure they want to delete the contact
                AlertDialog.Builder builder = new AlertDialog.Builder(ContactViewActivity.this);
                builder.setTitle("Confirm Delete").setMessage("Are you sure you want to delete this contact?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        // gets contact and deletes it from the database
                                        Contact contact = AppDatabase.getInstance(ContactViewActivity.this).contactDao().getContact(contactId);
                                        AppDatabase.getInstance(ContactViewActivity.this).contactDao().deleteContact(contact);
                                    }
                                }.start();

                                // goes back to main activity
                                Intent intent = new Intent(ContactViewActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // doesn't do anything
                            }
                        }).show();

            }
        });

        Button editBtn = findViewById(R.id.edit_btn);
        // opens the edit activity to edit the contact
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactViewActivity.this, EditContactsActivity.class);

                // sends all items with the intent
                intent.putExtra("name", name.getText().toString());
                intent.putExtra("phoneNumber", phoneNumber.getText().toString());
                intent.putExtra("picture", getIntent().getStringExtra("picture"));
                intent.putExtra("emailAdr", emailAdr.getText().toString());
                intent.putExtra("homeAdr", homeAdr.getText().toString());
                intent.putExtra("websiteAdr", websiteAdr.getText().toString());
                intent.putExtra("birthDate", birthDateText.getText().toString());
                intent.putExtra("callTime", callTimeText.getText().toString());
                intent.putExtra("callDays", callDayText.getText().toString());
                intent.putExtra("id", contactId);

                startActivity(intent);
            }
        });

        Button callBtn = findViewById(R.id.call_btn);
        // starts the call permission launcher
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRequestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
            }
        });

        // opens an email sending app with the email address inside the "to" box
        emailAdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailAdr.getText().toString();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

                startActivity(intent);
            }
        });

        // opens the home address on the map
        homeAdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gets the address from the TextView
                String addr = homeAdr.getText().toString();

                Geocoder geocoder = new Geocoder(ContactViewActivity.this);
                // only if the address is not empty
                if (!(addr.equals(""))) {
                    try {
                        // creates a list of latitude and longitude addresses from the given text address
                        List<Address> addresses = geocoder.getFromLocationName(addr, 1);
                        // only if at least one address was found
                        if(addresses != null && addresses.size() > 0) {
                            Address bestAddr = addresses.get(0); // chooses the best address as the first one

                            // opens a map viewing app and sets the address' longitude and latitude inside
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("geo:" + bestAddr.getLatitude() + "," + bestAddr.getLongitude()));
                            startActivity(intent);
                        } else { // if no such address was found
                            Toast.makeText(ContactViewActivity.this, "Could not find address", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) { // if an exception was found
                        throw new RuntimeException(e);
                    }
                } else { // if the address box was empty
                    Toast.makeText(ContactViewActivity.this, "Address is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // opens the website address in a web browser
        websiteAdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = websiteAdr.getText().toString();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://" + address));
                startActivity(intent);
            }
        });

        // enlarges the photo and gives an option to delete it
        contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creates dialog builder
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ContactViewActivity.this);
                // inflates the layout of the enlarged photo
                View dialogView = getLayoutInflater().inflate(R.layout.enlarged_photo, null, false);

                // finds the enlarged photo imageView from the inflated layout
                ImageView contactEnlargedPhoto = dialogView.findViewById(R.id.contact_image);
                // only if a photo has been chosen (Uri is not null)
                if (imgUri != null) {
                    // sets the profile photo in the enlarged imageView
                    contactEnlargedPhoto.setPadding(0, 0, 0, 0); // remove padding
                    contactEnlargedPhoto.setImageURI(Uri.parse(imgUri));
                } else {
                    // sets the profile photo to the default picture
                    contactEnlargedPhoto.setImageResource(R.drawable.baseline_person_24);
                }
                // creates the option to delete the profile picture
                builder.setNeutralButton("Delete profile picture", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                // gets the contact we are viewing
                                Contact contact = AppDatabase.getInstance(ContactViewActivity.this).contactDao().getContact(contactId);

                                contact.setPicture(null); // changes picture Uri to null
                                imgUri = null; // changes the local variable as well

                                // updates the contact
                                AppDatabase.getInstance(ContactViewActivity.this).contactDao().updateContact(contact);
                            }
                        }.start();

                        // sets both pictures to default
                        contactEnlargedPhoto.setImageResource(R.drawable.baseline_person_24);
                        contactImage.setPadding(30, 30, 30, 30);
                        contactImage.setImageResource(R.drawable.baseline_person_24);
                    }
                });
                builder.setView(dialogView);
                builder.create().show();
            }
        });
    }
}