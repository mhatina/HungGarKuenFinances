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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Adult;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Child;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Junior;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;


public class MemberManager extends ExportManager {
    private ArrayList<Member> mMembers;

    private static MemberManager ourInstance = new MemberManager();

    public static MemberManager getInstance() {
        return ourInstance;
    }

    private MemberManager() {
        mMembers = new ArrayList<>();

        // TODO remove
        addMember(createMember(R.drawable.adult_icon, "Dominika", "Hodovska", new Date(System.currentTimeMillis())));
    }

    /**
     * Get all members
     * @return
     */
    public List<Member> getMembers() {
        return getMembers(null, null);
    }

    /**
     * Get all/filtered members
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
                else if (member.getFirstName().toUpperCase().startsWith(filter1.toUpperCase()))
                    filteredList.add(member);
            }

            if (filter2 != null) {
                for (int i = filteredList.size() - 1; i >= 0; i--) {
                    if (!filteredList.get(i).getSurname().toUpperCase().startsWith(filter2.toUpperCase())
                            && !filteredList.get(i).getFirstName().toUpperCase().startsWith(filter2.toUpperCase()))
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
     * @param type type of member
     * @param name first name of member
     * @param surname surname of member
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
     * @param newMember
     */
    public void addMember(Member newMember) {
        mMembers.add(newMember);
    }

    /**
     * Delete member
     * @param deleteMember
     */
    public void deleteMember(Member deleteMember) {
        mMembers.remove(deleteMember);
    }

    /**
     * Delete member
     * @param index
     */
    public void deleteMember(int index) {
        mMembers.remove(index);
    }

    /**
     * Replace oldMember with newMember, copying ID and contact manager of old
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
     * @param name
     * @return
     */
    public Member findMember(String name) {
        int size = mMembers.size();
        for (int i = 0; i < size; i++) {
            if (mMembers.get(i).getFirstName().equals(name)
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

    /**
     * Comparatar for showing all members
     */
    public class MemberComparator implements java.util.Comparator<Member> {

        @Override
        public int compare(Member lhs, Member rhs) {
            int statusComparation = lhs.getStatus().getValue() - rhs.getStatus().getValue();
            if (statusComparation == 0) {
                if (lhs.getSurname().equals(rhs.getSurname()))
                    return lhs.getFirstName().compareTo(rhs.getFirstName());

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
            return ((lhs.getSurname().length() * 2) + lhs.getFirstName().length())
                    - ((rhs.getSurname().length() * 2) + rhs.getFirstName().length());
        }
    }
}
