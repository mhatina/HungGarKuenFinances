package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MailDescriptionTest {
    private Mail mail;

    @Before
    public void setUp() throws Exception {
        mail = new Mail(0, "Contact", "Note");
    }

    @Test
    public void getRunDescription() throws Exception {
        assertEquals("Write mail to Contact", mail.getRunDescription(InstrumentationRegistry.getTargetContext()));
    }
}