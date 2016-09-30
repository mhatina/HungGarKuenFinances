package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

/**
 * Created by mhatina on 22/09/16.
 */
public class Mail extends Contact {
    public static final int ICON_PATH = R.drawable.mail_black;

    public Mail(String contact, String note) {
        super(contact, note);
    }

    @Override
    public int getIconPath() {
        return ICON_PATH;
    }

    @Override
    public String getRunDescription() {
        return "Write mail to " + getContent();
    }

    @Override
    public void run(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:" + getContent());
        intent.setData(data);
        context.startActivity(intent);
    }
}
