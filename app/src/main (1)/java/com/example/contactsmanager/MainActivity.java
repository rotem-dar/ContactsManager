package com.example.contactsmanager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Contact> contacts; // list of contacts for adapter
    private  ContactAdapter adapter;

    // to run code in a thread on the main process
    Handler handler = new Handler(Looper.getMainLooper());

    String phoneNumber;

    ActivityResultLauncher<String> callRequestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            Intent intent;
            if (o) { // if permission was given then app calls person
                intent = new Intent(Intent.ACTION_CALL);
            } else { // if permission was nos given, app opens the dial app
                intent = new Intent(Intent.ACTION_DIAL);
            }
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creates the recycler view and set its layout to linear
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread() {
            @Override
            public void run() {
                super.run();

                // connects the recycler to the database
                contacts = AppDatabase.getInstance(MainActivity.this).contactDao().getContacts();

                // organises the contacts in alphabetical order
                List<String> contactsNames = new ArrayList<>(); // creates a list of only the contact names
                for (int i = 0; i < contacts.size(); i++) { // fills the list
                    contactsNames.add(contacts.get(i).getName());
                }
                Collections.sort(contactsNames); // sorts the list alphabetically

                List<Contact> newContacts = new ArrayList<>(); // creates new list of contacts
                for (int i = 0; i < contactsNames.size(); i++) { // goes over all names in names list
                    for (int j = 0; j < contacts.size(); j++) { // goes over all contacts
                        if (contacts.get(j) != null) {
                            if (contactsNames.get(i).equals(contacts.get(j).getName())) {
                                newContacts.add(contacts.get(j)); // sets the contacts in the new list in order
                                // turns contact in old list to null so if there are two contacts with the
                                // same name they won't appear twice each
                                contacts.set(j, null);
                            }
                        }
                    }
                }
                contacts = newContacts;

                // creates adapter
                adapter = new ContactAdapter(contacts);

                handler.post(new Runnable() {
                    @Override
                    public void run() {// sets adapter's on click listener
                        adapter.setListener(new ContactAdapter.ContactsListener() {
                            // when short clicked, the app opens the contact view page
                            @Override
                            public void onContactClick(int position, View view) {

                                // gets all the contact's info according to the position pressed
                                String name = contacts.get(position).getName();
                                phoneNumber = contacts.get(position).getPhoneNumber();
                                String picture = contacts.get(position).getPicture();
                                String emailAdr = contacts.get(position).getEmailAdr();
                                String homeAdr = contacts.get(position).getHomeAdr();
                                String websiteAdr = contacts.get(position).getWebsiteAdr();
                                String birthDate = contacts.get(position).getBirthDate();
                                String callTime = contacts.get(position).getCallTime();
                                String callDays = contacts.get(position).getCallDays();
                                int contactId = contacts.get(position).getId();

                                Intent intent = new Intent(MainActivity.this, ContactViewActivity.class);

                                // sets all the info in intent
                                intent.putExtra("name", name);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.putExtra("picture", picture);
                                intent.putExtra("emailAdr", emailAdr);
                                intent.putExtra("homeAdr", homeAdr);
                                intent.putExtra("websiteAdr", websiteAdr);
                                intent.putExtra("birthDate", birthDate);
                                intent.putExtra("callTime", callTime);
                                intent.putExtra("callDays", callDays);
                                intent.putExtra("id", contactId);

                                startActivity(intent);
                            }

                            // when long clicked, app opens a menu with four options, to edit, call, delete, or view contact
                            @Override
                            public void onContactLongClick(int position, View view) {

                                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                                getMenuInflater().inflate(R.menu.contact_card_menu, popupMenu.getMenu());
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        int itemId = item.getItemId();

                                        if (itemId == R.id.edit_menu_btn) { // if edit is chosen
                                            // gets all info according to position
                                            String name = contacts.get(position).getName();
                                            phoneNumber = contacts.get(position).getPhoneNumber();
                                            String picture = contacts.get(position).getPicture();
                                            String emailAdr = contacts.get(position).getEmailAdr();
                                            String homeAdr = contacts.get(position).getHomeAdr();
                                            String websiteAdr = contacts.get(position).getWebsiteAdr();
                                            String birthDate = contacts.get(position).getBirthDate();
                                            String callTime = contacts.get(position).getCallTime();
                                            String callDays = contacts.get(position).getCallDays();
                                            int contactId = contacts.get(position).getId();

                                            Intent intent = new Intent(MainActivity.this, EditContactsActivity.class);

                                            // sets info in intent
                                            intent.putExtra("name", name);
                                            intent.putExtra("phoneNumber", phoneNumber);
                                            intent.putExtra("picture", picture);
                                            intent.putExtra("emailAdr", emailAdr);
                                            intent.putExtra("homeAdr", homeAdr);
                                            intent.putExtra("websiteAdr", websiteAdr);
                                            intent.putExtra("birthDate", birthDate);
                                            intent.putExtra("callTime", callTime);
                                            intent.putExtra("callDays", callDays);
                                            intent.putExtra("id", contactId);

                                            startActivity(intent);

                                        } else if (itemId == R.id.call_menu_btn) {
                                            // gets contact's phone number
                                            phoneNumber = contacts.get(position).getPhoneNumber();

                                            // launches the call permission launcher
                                            callRequestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);

                                        } else if (itemId == R.id.delete_menu_btn) {
                                            // asks in user is sure they want to delete the contact
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                            builder.setTitle("Confirm Delete").setMessage("Are you sure you want to delete this contact?")
                                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // gets the contact according to position clicked
                                                            Contact contact = adapter.getContactAtPosition(position);
                                                            new Thread() {
                                                                @Override
                                                                public void run() {
                                                                    super.run();
                                                                    // deletes contact from database
                                                                    AppDatabase.getInstance(MainActivity.this).contactDao().deleteContact(contact);
                                                                }
                                                            }.start();
                                                            adapter.deleteContact(position); // deletes contact from adapter
                                                        }
                                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // does nothing
                                                        }
                                                    }).show();

                                        } else if (itemId == R.id.view_menu_btn) {
                                            // if view is chosen, then it starts the function called when contact is short clicked
                                            onContactClick(position, view);
                                        }
                                        return false;
                                    }
                                });
                                popupMenu.show();
                            }
                        });


                        // connects the recycler view to the adapter
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }.start();


        // chooses what happens when the contact card is swiped
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            // cannot drag contact
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // when contact is swiped, app calls it
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                Contact contact = adapter.getContactAtPosition(viewHolder.getAdapterPosition());
                phoneNumber = contact.getPhoneNumber();

                callRequestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
                // notifies that nothing actually changed so item will go back to place
                adapter.notifyDataSetChanged();
            }

        });
        helper.attachToRecyclerView(recyclerView);

        // when button is pressed then the contacts add page opens
        FloatingActionButton fab = findViewById(R.id.add_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddContactsActivity.class);
                startActivity(intent);
            }
        });
    }
}