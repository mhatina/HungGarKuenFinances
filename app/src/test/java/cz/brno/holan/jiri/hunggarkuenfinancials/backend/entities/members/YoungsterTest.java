package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members;

import org.junit.Test;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

import static org.junit.Assert.*;

public class YoungsterTest {
    @Test
    public void getIconPath() throws Exception {
        assertEquals(R.drawable.youngster_icon, Youngster.ICON_PATH);
    }

}