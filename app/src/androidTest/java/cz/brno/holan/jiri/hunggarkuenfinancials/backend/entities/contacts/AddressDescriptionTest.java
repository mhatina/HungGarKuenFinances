package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AddressDescriptionTest {
    private Address address;

    @Before
    public void setUp() throws Exception {
        address = new Address(0, "Contact", "Note");
    }

    @Test
    public void getRunDescription() throws Exception {
        assertEquals("Show Contact", address.getRunDescription(InstrumentationRegistry.getTargetContext()));
    }
}