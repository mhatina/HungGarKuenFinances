/*
 * Copyright (C) 2016  Martin Hatina
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers;

import android.content.Context;
import android.net.Uri;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Address;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Mail;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Phone;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Adult;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Child;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Junior;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Youngster;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.comparators.FilterComparator;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.comparators.MemberComparator;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers.SlidingTabManager;

public class MemberManager extends BaseManager {
    private ArrayList<Member> mMembers;
    private long newMemberId;

    private static MemberManager ourInstance = null;

    public static MemberManager getInstance(MainActivity activity) {
        if (ourInstance == null)
            ourInstance = new MemberManager(activity);
        return ourInstance;
    }

    public static MemberManager getInstance() {
        return ourInstance;
    }

    private MemberManager(MainActivity activity) {
        mMembers = new ArrayList<>();
        mDatabase.child("members").keepSynced(true);

        load(activity);
    }

    public long getNewMemberId() {
        return newMemberId;
    }

    public void setNewMemberId(long newMemberId) {
        this.newMemberId = newMemberId;
    }

    /**
     * Get all members
     *
     * @return
     */
    public List<Member> getMembers() {
        return getMembers(null, null);
    }

    /**
     * Get all/filtered members
     *
     * @param filter1 first name/surname filter
     * @param filter2 second name/surname filter, aplied after the filter1 was aplied
     * @return all or filtered members
     */
    public List<Member> getMembers(String filter1, String filter2) {
        if (filter1 != null) {
            ArrayList<Member> filteredList = new ArrayList<>();

            for (Member member : mMembers) {
                if (member.getSurname().toUpperCase().startsWith(filter1.toUpperCase()))
                    filteredList.add(member);
                else if (member.getName().toUpperCase().startsWith(filter1.toUpperCase()))
                    filteredList.add(member);
            }

            if (filter2 != null) {
                for (int i = filteredList.size() - 1; i >= 0; i--) {
                    if (!filteredList.get(i).getSurname().toUpperCase().startsWith(filter2.toUpperCase())
                            && !filteredList.get(i).getName().toUpperCase().startsWith(filter2.toUpperCase()))
                        filteredList.remove(i);
                }
            }

            Collections.sort(filteredList, new FilterComparator());

            return filteredList;
        } else {
            Collections.sort(mMembers, new MemberComparator());
            return mMembers;
        }
    }

    /**
     * Create member
     *
     * @param type       type of member
     * @param name       first name of member
     * @param surname    surname of member
     * @param birth_date date of birth of member
     * @return newly created member
     */
    public Member createMember(int type, String name, String surname, Date birth_date) {
        newMemberId++;
        mDatabase.child("members").child("id").setValue(newMemberId);

        switch (type) {
            case Adult.ICON_PATH:
                return new Adult(newMemberId, name, surname, birth_date);
            case Youngster.ICON_PATH:
                return new Youngster(newMemberId, name, surname, birth_date);
            case Junior.ICON_PATH:
                return new Junior(newMemberId, name, surname, birth_date);
            case Child.ICON_PATH:
                return new Child(newMemberId, name, surname, birth_date);
            default:
                return new Member(newMemberId, name, surname, birth_date);
        }
    }

    /**
     * Add new member
     *
     * @param member
     */
    public void addMember(Member member) {
        mMembers.add(member);
        upload(member);
    }


    /**
     * Delete member
     *
     * @param member
     */
    public void deleteMember(Member member) {
        mMembers.remove(member);
        delete(member);
    }


    /**
     * Replace oldMember with newMember, copying ID and contact manager of old
     *
     * @param oldMember
     * @param newMember
     */
    public void replaceMember(Member oldMember, Member newMember) {
        newMember.setId(oldMember.getId());
        newMember.setContactManager(oldMember.getContactManager());
        newMember.setPaidUntil(oldMember.getPaidUntil());
        deleteMember(oldMember);
        addMember(newMember);
    }

    /**
     * Find member by id
     *
     * @param id
     * @return
     */
    public Member findMember(long id) {
        int size = mMembers.size();
        for (int i = 0; i < size; i++) {
            if (mMembers.get(i).getId() == id)
                return mMembers.get(i);
        }

        return null;
    }

    /**
     * Find member by name or surname, but not both
     *
     * @param name
     * @return
     */
    public Member findMember(String name) {
        int size = mMembers.size();
        for (int i = 0; i < size; i++) {
            if (mMembers.get(i).getName().equals(name)
                    || mMembers.get(i).getSurname().equals(name))
                return mMembers.get(i);
        }

        return null;
    }

    @Override
    public String toString() {
        String exportText = "";
        for (Member member : mMembers) {
            exportText += member.toString() + "\n";
        }

        return exportText;
    }

    @Override
    public String exportDescription() {
        return null;
    }

    @Override
    public void load(final MainActivity activity) {
        mDatabase.child("members").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        newMemberId = dataSnapshot.child("id").getValue(long.class);

                        for (DataSnapshot postSnapshot : dataSnapshot.child("Adult").getChildren()) {
                            loadMember(postSnapshot, Adult.class);
                        }
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Junior").getChildren()) {
                            loadMember(postSnapshot, Junior.class);
                        }
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Child").getChildren()) {
                            loadMember(postSnapshot, Child.class);
                        }
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Member").getChildren()) {
                            loadMember(postSnapshot, Member.class);
                        }

                        ListView memberList = SlidingTabManager.createInstance().getMemberList();
                        ((BaseAdapter) memberList.getAdapter()).notifyDataSetChanged();
                        ((MembersAdapter) memberList.getAdapter()).sort(new MemberComparator());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void loadMember(DataSnapshot postSnapshot, Class<?> memberClass) {
        Member member = (Member) postSnapshot.getValue(memberClass);
        if (member.getContactManager() == null)
            member.setContactManager(new ContactManager());
        postSnapshot = postSnapshot.child("contacts");
        member.getContactManager().load(postSnapshot);

        member.updateStatus();
        mMembers.add(member);
    }

    @Override
    public void upload(BaseEntity entity) {
        Member member = (Member) entity;
        DatabaseReference reference = mDatabase.child("members").child(member.getClass().getSimpleName())
                .child(String.valueOf(member.getId()));

        reference.child("id").setValue(member.getId());
        reference.child("name").setValue(member.getName());
        reference.child("surname").setValue(member.getSurname());
        if (member.getBirthDate() != null)
            reference.child("birthDate").setValue(member.getBirthDate());
        reference.child("joinedDate").setValue(member.getJoinedDate());
        reference.child("paidUntil").setValue(member.getPaidUntil());
        reference.child("note").setValue(member.getNote());

        member.getContactManager().uploadContacts(reference);
    }

    @Override
    public void update(BaseEntity entity) {
        Member member = (Member) entity;
        DatabaseReference reference = mDatabase.child("members").child(member.getClass().getSimpleName())
                .child(String.valueOf(member.getId()));

        if ((member.getUpdatePropertiesSwitch() & Member.NAME_) > 0)
            reference.child("name").setValue(member.getName());
        if ((member.getUpdatePropertiesSwitch() & Member.SURNAME_) > 0)
            reference.child("surname").setValue(member.getSurname());
        if ((member.getUpdatePropertiesSwitch() & Member.BIRTH_DATE_) > 0)
            reference.child("birthDate").setValue(member.getBirthDate());
        if ((member.getUpdatePropertiesSwitch() & Member.JOINED_DATE_) > 0)
            reference.child("joinedDate").setValue(member.getJoinedDate());
        if ((member.getUpdatePropertiesSwitch() & Member.PAID_UNTIL_) > 0)
            reference.child("paidUntil").setValue(member.getPaidUntil());
        if ((member.getUpdatePropertiesSwitch() & Member.NOTE_) > 0)
            reference.child("note").setValue(member.getNote());

        member.clearUpdatePropertiesSwitch();
    }

    @Override
    public void delete(BaseEntity entity) {
        Member member = (Member) entity;
        mDatabase.child("members").child(member.getClass().getSimpleName())
                .child(String.valueOf(member.getId()))
                .removeValue();
    }

    @Override
    public void importFromFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = null;
        String mimeType = context.getContentResolver().getType(uri);

        if (mimeType == null || !mimeType.contains("text"))
            // todo report error
            return;

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = null;
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));
            } else {
                throw new NullPointerException("Cannot open file: " + uri.getPath());
            }

            SimpleDateFormat dateFormat;
            String line = reader.readLine();
            while (true) {
                String name = "";
                String surname = "";
                String address = "";
                String phone = "";
                String mail = "";
                Date birthDate = null;

                line = reader.readLine();
                if (line == null)
                    break;
                else if (line.isEmpty() || !line.contains(";"))
                    continue;

                String[] split = line.split(";");

                if (split.length == 0)
                    continue;

                for (int i = 0; i < split.length; i++) {
                    if (split[i].isEmpty())
                        continue;

                    String withoutAccent = split[i];
                    withoutAccent = Normalizer.normalize(withoutAccent, Normalizer.Form.NFD);
                    withoutAccent = withoutAccent.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                    if (withoutAccent.contains("@")) {
                        mail = split[i];
                        i++;
                        continue;
                    } else if (withoutAccent.matches(Constant.PHONE_REGEX)) {
                        phone = split[i];
                        continue;
                    } else if (withoutAccent.matches(Constant.SIMPLE_ADDRESS_REGEX)) {
                        address = split[i];
                        continue;
                    } else if (withoutAccent.matches(Constant.NAME_REGEX)) {
                        if (name.isEmpty())
                            name = split[i];
                        else
                            surname = split[i];
                        continue;
                    }

                    // todo change date format
                    // don't really like this
                    String[] formats = {"dd/MMM/yy", "MM/dd/yy", "dd/MM/yy", "dd/MMM/yyyy",
                            "MM/dd/yyyy", "dd/MM/yyyy", "dd-MMM-yy", "MM-dd-yy", "dd-MM-yy",
                            "dd-MMM-yyyy", "MM-dd-yyyy", "dd-MM-yyyy"};
                    for (String format : formats) {
                        dateFormat = new SimpleDateFormat(format);
                        try {
                            birthDate = dateFormat.parse(split[i]);
                            break;
                        } catch (ParseException e) {
                            // either bad format, or not a date
                        }
                    }
                }

                Member member = createMember(Member.ICON_PATH, name, surname, birthDate);
                if (!phone.isEmpty())
                    member.getContactManager().addContact(
                            member.getContactManager().createContact(context, Phone.ICON_PATH, phone, "")
                    );
                if (!mail.isEmpty())
                    member.getContactManager().addContact(
                            member.getContactManager().createContact(context, Mail.ICON_PATH, mail, "")
                    );
                if (!address.isEmpty())
                    member.getContactManager().addContact(
                            member.getContactManager().createContact(context, Address.ICON_PATH, address, "")
                    );
                addMember(member);
            }
        } catch (NullPointerException e) {
            // todo report error
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Override
    public String importDescription() {
        return null;
    }
}
