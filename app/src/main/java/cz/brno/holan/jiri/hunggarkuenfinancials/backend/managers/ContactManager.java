/*
 * Copyright (C) 2016  Martin Hatina
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Address;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Contact;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Mail;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Phone;

// TODO finish
public class ContactManager {
    private ArrayList<Contact> contacts;

    public ContactManager() {
        this.contacts = new ArrayList<>();
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public void uploadContacts(DatabaseReference reference) {
        for (Contact contact : contacts) {
            reference = reference.child("contacts")
                    .child(contact.getClass().getSimpleName())
                    .child(String.valueOf(contacts.indexOf(contact)));

            reference.child("content").setValue(contact.getContent());
            reference.child("note").setValue(contact.getNote());
        }
    }

    public Contact createContact(int type, String contact, String note) {
        long id = 0;
        if (!contacts.isEmpty())
            id = contacts.get(contacts.size() - 1).getId() + 1;
        switch (type) {
            case Address.ICON_PATH:
                // TODO translate
                return new Address(id, contact, note.isEmpty() ? "Home address" : note);
            case Mail.ICON_PATH:
                return new Mail(id, contact, note.isEmpty() ? "Personal mail" : note);
            case Phone.ICON_PATH:
                return new Phone(id, contact, note.isEmpty() ? "Mobile phone" : note);
            default:
                return null;
        }
    }

    public void load(DataSnapshot snapshot) {
        for (DataSnapshot postSnapshot : snapshot.child("Address").getChildren()) {
            loadContact(postSnapshot, Address.class);
        }
        for (DataSnapshot postSnapshot : snapshot.child("Mail").getChildren()) {
            loadContact(postSnapshot, Mail.class);
        }
        for (DataSnapshot postSnapshot : snapshot.child("Phone").getChildren()) {
            loadContact(postSnapshot, Phone.class);
        }
    }

    private void loadContact(DataSnapshot snapshot, Class<?> contactClass) {
        Contact contact = (Contact) snapshot.getValue(contactClass);
        contacts.add(contact);
    }
}
