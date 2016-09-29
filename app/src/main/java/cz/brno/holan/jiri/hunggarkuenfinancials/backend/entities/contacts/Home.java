package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

/**
 * Created by mhatina on 22/09/16.
 */
public class Home extends Contact {
    public static final int ICON_PATH = R.drawable.home_black;

    public Home(String contact, String note) {
        super(contact, note);
    }

    @Override
    public int getIconPath() {
        return ICON_PATH;
    }
}
