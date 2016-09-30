package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers;

import java.util.ArrayList;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Contact;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Address;
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

    public static Contact createContact(int type, String contact, String note) {
        switch (type) {
            case R.drawable.home_black:
                // TODO translate
                return new Address(contact, note.isEmpty() ? "Home address" : note);
            case R.drawable.mail_black:
                return new Mail(contact, note.isEmpty() ? "Personal mail" : note);
            case R.drawable.phone_black:
                return new Phone(contact, note.isEmpty() ? "Mobile phone" : note);
            default:
                return null;
        }
    }
}
