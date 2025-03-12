package com.example.contactsmanager;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contacts; // list of all the contacts in the adapter
    private ContactsListener listener; // listener for onClick and onLongClick for holder

    // that way I can write in the MainActivity what will happen when the contacts are clicked
    interface ContactsListener {
        void onContactClick(int position, View view);
        void onContactLongClick(int position, View view);
    }

    // sets the adapters listener
    public void setListener(ContactsListener listener) {
        this.listener = listener;
    }

    // creates the contact adapter with a list of contacts
    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Contact> getContacts() {
        return this.contacts;
    }

    // gets the number of contacts in the adapter
    @Override
    public int getItemCount() {
        return contacts.size();
    }

    // when a new contact is created this function creates its view in the adapter
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ContactViewHolder(layoutInflater.inflate(R.layout.activity_contact_card, parent, false));
    }

    // called each time a contact is scrolled to view, sets the info in the contact's holder according to the contact's info
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactPhone.setText(contact.getPhoneNumber());

        // only if a photo has been chosen (Uri is not null)
        if (contact.getPicture() != null) {
            holder.contactImage.setPadding(0,0,0,0); // removes padding
            Glide.with(holder.itemView).load(Uri.parse(contact.getPicture())).circleCrop().into(holder.contactImage);
        } else {
            holder.contactImage.setPadding(20,20,20,20); // adds padding
            Glide.with(holder.itemView).load(R.drawable.baseline_person_24).circleCrop().into(holder.contactImage);
        }
    }

    // returns the contact at the given index (position)
    public Contact getContactAtPosition(int position) {
        return contacts.get(position);
    }

    // deletes a contact from the recycler view
    public void deleteContact(int position) {
        this.contacts.remove(position); // deletes it from contacts list
        notifyItemRemoved(position); // notifies the recycler that an item has been deleted
    }

    // creates a holder (is only called once for each contact)
    public class ContactViewHolder extends RecyclerView.ViewHolder {

        // the items on each item in the recycler view (contact card)
        ImageView contactImage;
        TextView contactName;
        TextView contactPhone;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            // finds view by ID so later we can put things there
            contactImage = itemView.findViewById(R.id.contact_card_image);
            contactName = itemView.findViewById(R.id.contact_card_name);
            contactPhone = itemView.findViewById(R.id.contact_card_number);

            // listens to when an item is clicked/long clicked and passes it on
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onContactClick(getAdapterPosition(), v);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null)
                        listener.onContactLongClick(getAdapterPosition(), v);
                    return false;
                }
            });
        }
    }


}
