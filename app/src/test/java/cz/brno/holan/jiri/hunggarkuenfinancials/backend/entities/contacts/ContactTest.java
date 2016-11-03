package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContactTest {
    private Contact contact;

    @Before
    public void setUp() throws Exception {
        contact = new Contact(0, "SomeContact", "SomeNote") {
            @Override
            public String getRunDescription(Context context) {
                return null;
            }

            @Override
            public void run(Context context) {

            }
        };
    }

    @Test
    public void getContent() throws Exception {
        assertEquals("SomeContact", contact.getContent());
    }

    @Test
    public void setContent() throws Exception {
        contact.setContent("Contact");
        assertEquals("Contact", contact.getContent());
    }

    @Test
    public void getNote() throws Exception {
        assertEquals("SomeNote", contact.getNote());
    }

    @Test
    public void setNote() throws Exception {
        contact.setNote("Note");
        assertEquals("Note", contact.getNote());
    }

}