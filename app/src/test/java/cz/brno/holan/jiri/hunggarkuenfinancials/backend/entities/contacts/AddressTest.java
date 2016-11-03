package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import org.junit.Test;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

import static org.junit.Assert.assertEquals;

public class AddressTest {
    @Test
    public void getIconPath() throws Exception {
        assertEquals(R.drawable.home_black, Address.ICON_PATH);
    }
}