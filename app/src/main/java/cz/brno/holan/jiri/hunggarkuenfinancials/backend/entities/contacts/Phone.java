package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

/**
 * Created by mhatina on 22/09/16.
 */
public class Phone extends Contact {
    public static final int ICON_PATH = R.drawable.phone_black;

    public Phone(String content, String note) {
        super(content, note);
    }

    @Override
    public int getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String getRunDescription() {
        return "Call " + getContent();
    }

    @Override
    public void run(Context context) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + getContent()));
        context.startActivity(callIntent);
    }
}
