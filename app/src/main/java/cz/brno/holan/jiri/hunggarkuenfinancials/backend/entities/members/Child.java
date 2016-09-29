package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members;

import java.util.Date;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

/**
 * Created by mhatina on 3/9/16.
 */
public class Child extends Member {

    public final int ICON_PATH = R.drawable.child_icon;

    public Child(long id, String firstName, String surname) {
        super(id, firstName, surname);
    }

    public Child(long id, String firstName, String surname, Date paidUntil) {
        super(id, firstName, surname, paidUntil);
    }

    @Override
    public int getIconPath() {
        return ICON_PATH;
    }
}
