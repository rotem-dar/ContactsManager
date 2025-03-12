package com.example.contactsmanager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * from contact")
    public List<Contact> getContacts();

    @Query("SELECT * from contact WHERE id = :id")
    public Contact getContact(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addContact(Contact contact);

    @Delete
    public void deleteContact(Contact contact);

    @Update
    void updateContact(Contact contact);

}
