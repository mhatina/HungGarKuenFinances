package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members;

import org.junit.Test;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

import static org.junit.Assert.assertEquals;

public class AdultTest {
    @Test
    public void getIconPath() throws Exception {
        assertEquals(R.drawable.adult_icon, Adult.ICON_PATH);
    }

}