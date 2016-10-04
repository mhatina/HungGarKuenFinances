package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

/**
 * Created by mhatina on 22/09/16.
 */
public class Address extends Contact {
    public static final int ICON_PATH = R.drawable.home_black;

    public Address() {
    }

    public Address(String contact, String note) {
        super(contact, note);
    }

    @Override
    public int getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String getRunDescription() {
        return "Show " + getContent();
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
