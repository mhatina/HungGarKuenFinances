package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers;

import com.google.firebase.database.DatabaseReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Adult;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;

import static org.junit.Assert.*;

public class MemberManagerTest {
    private MemberManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new TestMemberManager();
        manager.addMember(new Adult(0, "Name", "Surname"));
        manager.getDatabaseReference().child("id").setValue(0);
    }

    @After
    public void TearDown() throws Exception {

    }

    @Test
    public void getInstance() throws Exception {
        assertNotNull(MemberManager.getInstance());
    }

    @Test
    public void getMembers() throws Exception {
        assertNotNull(manager.getMembers());
        assertTrue(manager.getMembers().size() > 0);
    }

    @Test
    public void getMembersFilter() throws Exception {
        assertNotNull(manager.getMembers("name"));
        assertTrue(manager.getMembers("name").size() > 0);

        assertNotNull(manager.getMembers("surname"));
        assertTrue(manager.getMembers("surname").size() > 0);

        assertNotNull(manager.getMembers("name", "surname"));
        assertTrue(manager.getMembers("name", "surname").size() > 0);
    }

    @Test
    public void createMember() throws Exception {
        Member member = manager.createMember(Adult.ICON_PATH, "TestName", "TestSurname", new Date());
        assertNotNull(member);
        assertEquals("TestName", member.getName());
    }

    @Test
    public void addMember() throws Exception {

    }

    @Test
    public void deleteMember() throws Exception {

    }

    @Test
    public void replaceMember() throws Exception {

    }

    @Test
    public void findMember() throws Exception {

    }

    @Test
    public void exportDescription() throws Exception {

    }

    @Test
    public void load() throws Exception {

    }

    @Test
    public void upload() throws Exception {

    }

    @Test
    public void update() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }

    @Test
    public void importFromFile() throws Exception {

    }

    @Test
    public void importDescription() throws Exception {

    }

    private class TestMemberManager extends MemberManager {

        private TestMemberManager() {
            super();
        }

        @Override
        public DatabaseReference getDatabaseReference() {
            return mDatabase.child("test").child("members");
        }
    }

}