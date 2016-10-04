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
import android.support.v4.app.FragmentActivity;
import android.widget.BaseAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Adult;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Child;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Junior;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity;


public class MemberManager extends ExportManager implements LoadManager {
    private ArrayList<Member> mMembers;

    private static MemberManager ourInstance;

    public static MemberManager getInstance(MainActivity activity) {
        ourInstance = new MemberManager(activity);
        return ourInstance;
    }

    public static MemberManager getInstance() {
        return ourInstance;
    }

    private MemberManager(MainActivity activity) {
        mMembers = new ArrayList<>();

        load(activity);
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
        long id = 0;
        if (!mMembers.isEmpty())
            id = mMembers.get(mMembers.size() - 1).getId() + 1;
        switch (type) {
            case R.drawable.adult_icon:
                return new Adult(id, name, surname, birth_date);
            case R.drawable.junior_icon:
                return new Junior(id, name, surname, birth_date);
            case R.drawable.child_icon:
                return new Child(id, name, surname, birth_date);
            default:
                return new Member(id, name, surname, birth_date);
        }
    }

    /**
     * Add new member
     *
     * @param member
     */
    public void addMember(Member member) {
        mMembers.add(member);
        uploadMember(member);
    }

    private void uploadMember(Member member) {
        DatabaseReference reference = mDatabase.child(member.getClass().getSimpleName())
                .child(String.valueOf(member.getId()));

        reference.child("id").setValue(member.getId());
        reference.child("name").setValue(member.getName());
        reference.child("surname").setValue(member.getSurname());
        reference.child("birthDate").setValue(member.getBirthDate());
        reference.child("joinedDate").setValue(member.getJoinedDate());
        reference.child("paidUntil").setValue(member.getPaidUntil());
        reference.child("note").setValue(member.getNote());

        member.getContactManager().uploadContacts(reference);
    }

    /**
     * Delete member
     *
     * @param member
     */
    public void deleteMember(Member member) {
        mMembers.remove(member);
        mDatabase.child(member.getClass().getSimpleName())
                .child(String.valueOf(member.getId()))
                .removeValue();
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
    public void load(final MainActivity activity) {
        mDatabase.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.child("Adult").getChildren()) {
                            Adult adult = postSnapshot.getValue(Adult.class);
                            if (adult.getContactManager() == null)
                                adult.setContactManager(new ContactManager());
                            mMembers.add(adult);
                        }
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Junior").getChildren()) {
                            Junior junior = postSnapshot.getValue(Junior.class);
                            mMembers.add(junior);
                        }
                        for (DataSnapshot postSnapshot : dataSnapshot.child("Child").getChildren()) {
                            Child child = postSnapshot.getValue(Child.class);
                            mMembers.add(child);
                        }

                        ((BaseAdapter) activity.getMemberListView().getAdapter()).notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Comparator for showing all members
     */
    public class MemberComparator implements java.util.Comparator<Member> {

        @Override
        public int compare(Member lhs, Member rhs) {
            int statusComparation = lhs.getStatus().getValue() - rhs.getStatus().getValue();
            if (statusComparation == 0) {
                if (lhs.getSurname().equals(rhs.getSurname()))
                    return lhs.getName().compareTo(rhs.getName());

                return lhs.getSurname().compareTo(rhs.getSurname());
            }

            return statusComparation;
        }
    }

    /**
     * Comparator used when searching for member
     */
    public class FilterComparator implements java.util.Comparator<Member> {

        @Override
        public int compare(Member lhs, Member rhs) {
            return ((lhs.getSurname().length() * 2) + lhs.getName().length())
                    - ((rhs.getSurname().length() * 2) + rhs.getName().length());
        }
    }
}
