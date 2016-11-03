package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members;

import org.junit.Test;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;

import static org.junit.Assert.*;

public class ChildTest {
    @Test
    public void getIconPath() throws Exception {
        assertEquals(R.drawable.child_icon, Child.ICON_PATH);
    }

}