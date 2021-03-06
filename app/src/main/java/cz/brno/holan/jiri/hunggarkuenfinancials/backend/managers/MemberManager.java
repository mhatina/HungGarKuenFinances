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

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
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
import java.util.Locale;

import cz.brno.holan.jiri.hunggarkuenfinancials.BuildConfig;
import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.Log;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.Utils;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Address;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Mail;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Phone;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Adult;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Child;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Junior;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Youngster;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.comparators.MemberComparator;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers.EntityTabManager;

public class MemberManager extends BaseManager {
    private final ArrayList<Member> mMembers;
    private final ArrayList<Member> mShownMembers;
    private long newMemberId;

    private static MemberManager ourInstance = null;

    public static MemberManager getInstance() {
        if (ourInstance == null)
            ourInstance = new MemberManager();
        return ourInstance;
    }

    MemberManager() {
        mMembers = new ArrayList<>();
        mShownMembers = new ArrayList<>();
        getDatabaseReference().keepSynced(true);
    }

    public String getMemberNamesByIds(List<Long> memberIds) throws Exception {
        String members = "";
        for (long id : memberIds) {
            MemberManager manager = MemberManager.getInstance();
            Member member = manager.findMember(id);

            if (member == null)
                continue;

            members += member.getName() + " " + member.getSurname();

            if (id != memberIds.get(memberIds.size() - 1))
                members += ", ";
        }

        if (members.isEmpty())
            // TODO own exception
            throw new Exception();

        return members;
    }

    public List<Member> getMembers(String... filters) {
        Utils.copyContent(mShownMembers, mMembers);

        for (int i = mMembers.size() - 1; i >= 0; i--) {
            Member member = mShownMembers.get(i);
            if (isMemberGroupFilteredOut(member)) {
                mShownMembers.remove(member);
                continue;
            } else if (filters == null || filters.length == 0)
                continue;

            for (String filter : filters) {
                if (filter.isEmpty())
                    mShownMembers.remove(member);
                else if (!member.getSurname().toUpperCase().startsWith(filter.toUpperCase())
                        && !member.getName().toUpperCase().startsWith(filter.toUpperCase()))
                    mShownMembers.remove(member);
            }
        }

        Collections.sort(mShownMembers, new MemberComparator());
        return mShownMembers;
    }

    private boolean isMemberGroupFilteredOut(Member member) {
        if (groupFilter == 0) {
            return false;
        } else if (isMemberInDesiredGroup(member)) {
            return isBeginnerFilterOn() && !member.isBeginner();
        } else if (isBeginnerFilterOn() && member.isBeginner())
            return false;

        return true;
    }

    private boolean isBeginnerFilterOn() {
        return ((groupFilter & Constant.BEGINNER_GROUP) != 0);
    }

    private boolean isMemberInDesiredGroup(Member member) {
        return (filterStrippedOfBeginnerGroup() & Utils.mapMemberClassToCode(member)) != 0;
    }

    private int filterStrippedOfBeginnerGroup() {
        return groupFilter & (Constant.BEGINNER_GROUP - 1);
    }

    public boolean isShownMembersEmpty() {
        return mShownMembers.isEmpty();
    }

    public Member createMember(int type, String name, String surname, Date birthDate) {
        newMemberId++;
        getDatabaseReference().child("id").setValue(newMemberId);

        switch (type) {
            case Adult.ICON_PATH:
                return new Adult(newMemberId, name, surname, birthDate);
            case Youngster.ICON_PATH:
                return new Youngster(newMemberId, name, surname, birthDate);
            case Junior.ICON_PATH:
                return new Junior(newMemberId, name, surname, birthDate);
            case Child.ICON_PATH:
                return new Child(newMemberId, name, surname, birthDate);
            default:
                return new Member(newMemberId, name, surname, birthDate);
        }
    }

    public void addMember(Member member) {
        mMembers.add(member);
        upload(member);
    }

    public void deleteMember(Member member) {
        mMembers.remove(member);
        delete(member);
    }

    public void replaceMember(Member oldMember, Member newMember) {
        newMember.setId(oldMember.getId());
        newMember.setContactManager(oldMember.getContactManager());
        newMember.setPaidUntil(oldMember.getPaidUntil());
        deleteMember(oldMember);
        addMember(newMember);
    }

    public Member findMember(long id) {
        if (id < 0)
            return null;

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
    public void load(final Activity activity) {
        mMembers.clear();
        mShownMembers.clear();

        getDatabaseReference().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        newMemberId = dataSnapshot.child("id").getValue(long.class);

                        Class<?>[] classes = new Class[]{Adult.class, Youngster.class, Junior.class, Child.class, Member.class};

                        for (Class<?> aClass : classes) {
                            for (DataSnapshot postSnapshot : dataSnapshot.child(aClass.getSimpleName()).getChildren()) {
                                loadMember(postSnapshot, aClass);
                            }
                        }

                        ListView memberList = EntityTabManager.getInstance().getMemberList(activity);
                        if (memberList != null) {
                            MembersAdapter adapter = (MembersAdapter) memberList.getAdapter();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                                adapter.sort(new MemberComparator());
                            }
                        }

                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).moveFloatingButton();
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
        mShownMembers.add(member);
    }

    @Override
    public void upload(BaseEntity entity) {
        Member member = (Member) entity;
        DatabaseReference reference = getDatabaseReference().child(member.getClass().getSimpleName())
                .child(String.valueOf(member.getId()));

        reference.child("id").setValue(member.getId());
        reference.child("name").setValue(member.getName());
        reference.child("surname").setValue(member.getSurname());
        if (member.getBirthDate() != null)
            reference.child("birthDate").setValue(member.getBirthDate());
        reference.child("joinedDate").setValue(member.getJoinedDate());
        reference.child("paidUntil").setValue(member.getPaidUntil());
        reference.child("note").setValue(member.getNote());
        reference.child("beginner").setValue(member.isBeginner());

        member.getContactManager().uploadContacts(reference);
    }

    @Override
    public void update(BaseEntity entity) {
        Member member = (Member) entity;
        DatabaseReference reference = getDatabaseReference().child(member.getClass().getSimpleName())
                .child(String.valueOf(member.getId()));

        if ((member.getUpdatePropertiesSwitch() & Constant.NAME_SWITCH) > 0)
            reference.child("name").setValue(member.getName());
        if ((member.getUpdatePropertiesSwitch() & Constant.SURNAME_SWITCH) > 0)
            reference.child("surname").setValue(member.getSurname());
        if ((member.getUpdatePropertiesSwitch() & Constant.BIRTH_DATE_SWITCH) > 0)
            reference.child("birthDate").setValue(member.getBirthDate());
        if ((member.getUpdatePropertiesSwitch() & Constant.JOINED_DATE_SWITCH) > 0)
            reference.child("joinedDate").setValue(member.getJoinedDate());
        if ((member.getUpdatePropertiesSwitch() & Constant.PAID_UNTIL_SWITCH) > 0)
            reference.child("paidUntil").setValue(member.getPaidUntil());
        if ((member.getUpdatePropertiesSwitch() & Constant.NOTE_SWITCH) > 0)
            reference.child("note").setValue(member.getNote());
        if ((member.getUpdatePropertiesSwitch() & Constant.BEGINNER_SWITCH) > 0)
            reference.child("beginner").setValue(member.isBeginner());

        member.getContactManager().uploadContacts(reference);

        member.clearUpdatePropertiesSwitch();
    }

    @Override
    public DatabaseReference getDatabaseReference() {
        if (BuildConfig.DEBUG)
            return mDatabase.child("debug").child("members");
        return mDatabase.child("members");
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
    public int importDescription() {
        return R.string.import_members;
    }

    @Override
    public void importFromFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = null;
        String mimeType = context.getContentResolver().getType(uri);

        if (mimeType == null || !mimeType.contains("text")) {
            Log.warning(context, new InvalidParameterException(context.getString(R.string.csv_txt_error)));
            return;
        }

        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader;
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));
            } else {
                throw new NullPointerException(context.getString(R.string.cannot_open_file, uri.getPath()));
            }

            String line;
            reader.readLine();
            while (true) {
                String[] split = null;

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
                    Log.warning(context, new InvalidParameterException(context.getString(R.string.no_delimeter_found)));

                if (split != null && split.length == 0) {
                    continue;
                }

                addMember(parseLine(context, split));
            }
        } catch (NullPointerException e) {
            Log.warning(context, e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private Member parseLine(Context context, String[] lineSplit) {
        String name = "";
        String surname = "";
        String address = "";
        String phone = "";
        String mail = "";
        Date birthDate = null;
        Date paidUntil = null;
        boolean filledPaidUntil = false;

        for (int i = 0; i < lineSplit.length; i++) {
            if (lineSplit[i].isEmpty())
                continue;

            String withoutAccent = normalizeString(lineSplit[i]);

            if (withoutAccent.contains("@")) {
                mail = lineSplit[i];
                i++;
                continue;
            } else if (withoutAccent.matches(Constant.PHONE_REGEX)) {
                phone = lineSplit[i];
                continue;
            } else if (withoutAccent.matches(Constant.SIMPLE_ADDRESS_REGEX)) {
                address = lineSplit[i];
                continue;
            } else if (withoutAccent.matches(Constant.NAME_REGEX)) {
                if (name.isEmpty())
                    name = lineSplit[i];
                else
                    surname = lineSplit[i];
                continue;
            }

            if (!filledPaidUntil && paidUntil == null) {
                paidUntil = parseDates(lineSplit[i]);
            } else if (birthDate == null) {
                birthDate = parseDates(lineSplit[i]);
            }
            filledPaidUntil = true;
        }

        return createMember(context, name, surname, address, phone, mail, birthDate, paidUntil);
    }

    private Member createMember(@Nullable Context context, @Nullable String name, @Nullable String surname,
                                String address, String phone, String mail, Date birthDate, Date paidUntil) {
        Member member = createMember(getTypeByBirthDate(birthDate), name, surname, birthDate);
        if (mMembers.indexOf(member) != -1)
            return null;
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

        return member;
    }

    private Date parseDates(String s) {
        // don't really like this
        String[] formats = {"dd.M.yyyy", "dd.MM.yyyy", "dd/M/yyyy", "dd/MM/yyyy",
                "dd-M-yyyy", "dd-MM-yyyy"};
        s = addYearToDate(s);
        s = extendYearToFullLength(s);
        for (String format : formats) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());

            try {
                return dateFormat.parse(s);
            } catch (ParseException e) {
                // either bad format, or not a date
            }
        }

        return null;
    }

    private String extendYearToFullLength(String s) {
        int lastIndexOfDot;
        if (s.contains("."))
            lastIndexOfDot = s.lastIndexOf('.');
        else if (s.contains("/"))
            lastIndexOfDot = s.lastIndexOf('/');
        else
            lastIndexOfDot = s.lastIndexOf('-');

        if (s.substring(lastIndexOfDot + 1).length() <= 2) {
            int year = Integer.valueOf(s.substring(lastIndexOfDot + 1));
            int currentShortYear = Integer.valueOf(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2));
            year += year > currentShortYear ? 1900 : 2000;
            s = s.substring(0, lastIndexOfDot + 1) + year;
        }

        return s;
    }

    private String addYearToDate(String s) {
        if (s.endsWith("."))
            s += Calendar.getInstance().get(Calendar.YEAR);
        else if (s.length() <= 5) {
            s += s.contains("/") ? "/" : "-" + Calendar.getInstance().get(Calendar.YEAR);
        }

        return s;
    }

    private String normalizeString(String s) {
        String withoutAccent = s;
        withoutAccent = Normalizer.normalize(withoutAccent, Normalizer.Form.NFD);
        withoutAccent = withoutAccent.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return withoutAccent;
    }
}
