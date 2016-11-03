package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PhoneDescriptionTest {
    private Phone phone;

    @Before
    public void setUp() throws Exception {
        phone = new Phone(0, "Contact", "Note");
    }

    @Test
    public void getRunDescription() throws Exception {
        assertEquals("Call Contact", phone.getRunDescription(InstrumentationRegistry.getTargetContext()));
    }
}