package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members;

import org.junit.Test;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

import static org.junit.Assert.*;

public class JuniorTest {
    @Test
    public void getIconPath() throws Exception {
        assertEquals(R.drawable.junior_icon, Junior.ICON_PATH);
    }

}