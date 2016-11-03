package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import org.junit.Test;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

import static org.junit.Assert.assertEquals;

public class PhoneTest {
    @Test
    public void getIconPath() throws Exception {
        assertEquals(R.drawable.phone_black, Phone.ICON_PATH);
    }
}