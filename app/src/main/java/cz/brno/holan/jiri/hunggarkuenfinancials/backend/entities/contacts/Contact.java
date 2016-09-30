package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.content.Context;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

/**
 * Created by mhatina on 3/9/16.
 */
public abstract class Contact {
    public static final int ICON_PATH = R.drawable.home_black;
    private String contact;
    private String note;

    public Contact(String contact, String note) {
        this.contact = contact;
        this.note = note;
    }

    public String getContent() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIconPath() {
        return ICON_PATH;
    }

    public abstract String getRunDescription();

    public abstract void run(Context context);
}
