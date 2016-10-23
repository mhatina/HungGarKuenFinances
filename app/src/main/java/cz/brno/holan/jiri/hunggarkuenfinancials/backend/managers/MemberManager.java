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
import java.security.InvalidParameterException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.Log;
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

    public static MemberManager getInstance() {
        if (ourInstance == null)
            ourInstance = new MemberManager();
        return ourInstance;
    }

    private MemberManager() {
        mMembers = new ArrayList<>();
        mDatabase.child("members").keepSynced(true);
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
                        if (memberList != null) {
                            MembersAdapter adapter = (MembersAdapter) memberList.getAdapter();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                                adapter.sort(new MemberComparator());
                            }
                        }
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

    private int getTypeByBirthDate(Date birthDate) {
        if (birthDate == null)
            return Member.ICON_PATH;

        Calendar now = Calendar.getInstance();
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);

        int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        if (age < 10) {
            return Child.ICON_PATH;
        } else if (age >= 10 && age < 15) {
            return Junior.ICON_PATH;
        } else if (age >= 15 && age < 18) {
            return Youngster.ICON_PATH;
        } else if (age >= 18) {
            return Adult.ICON_PATH;
        }

        return Member.ICON_PATH;
    }

    @Override
    public void importFromFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = null;
        String mimeType = context.getContentResolver().getType(uri);

        if (mimeType == null || !mimeType.contains("text")) {
            Log.warning(context, new InvalidParameterException("File should be a csv or txt."));
            return;
        }

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
                String[] split = null;
                Date birthDate = null;
                Date paidUntil = null;
                boolean checkPaidUntil = false;

                line = reader.readLine();
                if (line == null)
                    break;
                else if (line.isEmpty())
                    continue;
                else if (line.contains(";"))
                    split = line.split(";");
                else if (line.contains("\t"))
                    split = line.split("\t");
                else
                    Log.warning(context, new InvalidParameterException("No tabulator or ';' found in file."));

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

                    // don't really like this
                    String[] formats = {"dd.M.yyyy", "dd.M.yy", "dd.MM.yyyy", "dd.MM.yy",
                            "dd/M/yyyy", "dd/M/yy", "dd/MM/yyyy", "dd/MM/yy",
                            "dd-M-yyyy", "dd-M-yy", "dd-MM-yyyy", "dd-MM-yy"};
                    if (split[i].endsWith("."))
                        split[i] += Calendar.getInstance().get(Calendar.YEAR);
                    else if (split[i].length() <= 5) {
                        split[i] += split[i].contains("/") ? "/" : "-" + Calendar.getInstance().get(Calendar.YEAR);
                    }
                    for (String format : formats) {
                        dateFormat = new SimpleDateFormat(format);
                        try {
                            if (!checkPaidUntil) {
                                paidUntil = dateFormat.parse(split[i]);
                            } else {
                                birthDate = dateFormat.parse(split[i]);
                            }
                            break;
                        } catch (ParseException e) {
                            // either bad format, or not a date
                        }
                    }
                    checkPaidUntil = true;
                }

                Member member = createMember(getTypeByBirthDate(birthDate), name, surname, birthDate);
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
                if (paidUntil != null) {
                    member.setPaidUntil(paidUntil);
                    member.updateStatus();
                }
                addMember(member);
            }
        } catch (NullPointerException e) {
            Log.warning(context, e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Override
    public String importDescription() {
        return "Import members";
    }
}
