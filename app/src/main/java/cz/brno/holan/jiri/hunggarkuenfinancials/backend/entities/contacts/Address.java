package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

public class Address extends Contact {
    public static final int ICON_PATH = R.drawable.home_black;

    public Address() {
    }

    public Address(long id, String contact, String note) {
        super(id, contact, note);
    }

    @Override
    public int getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String getRunDescription(Context context) {
        return context.getString(R.string.address_run_description, getContent());
    }

    @Override
    public String toString() {
        return "Address{" + super.toString() + "}";
    }

    @Override
    public void run(Context context) {
        Uri uri = Uri.parse("https://www.google.com/maps/place/" + getContent());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}
