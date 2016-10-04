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

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members;

import java.util.ArrayList;
import java.util.Date;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Address;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Mail;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Phone;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.enums.MemberStatus;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ContactManager;

/**
 * Created by mhatina on 3/8/16.
 */
public class Member {

    public static final int ICON_PATH = R.drawable.lam_ga_hung_kuen_logo;
    public static final int PAYMENT_OK = android.R.drawable.presence_online;
    public static final int PAYMENT_IMMINENT = android.R.drawable.presence_away;
    public static final int PAYMENT_INACTIVE = android.R.drawable.presence_offline;
    public static final long BEFORE_END_OF_PAYMENT_PERIOD = 3 * 24 * 60 * 60 * 1000; // #days * 24h * 60min * 60sec * 1000msec
    public static final long INACTIVATION_PERIOD = 14 * 24 * 60 * 60 * 1000; // #days * 24h * 60min * 60sec * 1000msec

    private Long       id;
    private String name;
    private String     surname;
    private Date       birthDate;
    private Date       joinedDate;
    private Date       paidUntil;
    private MemberStatus status;
    private String     note;

    private ArrayList<Payment> payments;
    private ContactManager contactManager = new ContactManager();

    public Member() {
    }

    public Member(long id, String name, String surname) {
        this(id, name, surname, new Date(System.currentTimeMillis()));
    }

    public Member(long id, String name, String surname, Date birthDate) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.paidUntil = new Date(System.currentTimeMillis());
        this.birthDate = birthDate;
        this.joinedDate = new Date(System.currentTimeMillis());
        updateStatus();

        // TODO remove
        contactManager.addContact(new Phone("+421 908 862 822", "Mobile"));
        contactManager.addContact(new Mail("mail@work.co", "Work mail"));
        contactManager.addContact(new Address("Purkynova 50, Brno", "Home address"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (!id.equals(member.id)) return false;
        if (!name.equals(member.name)) return false;
        return surname.equals(member.surname);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + surname.hashCode();
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Date getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        this.joinedDate = joinedDate;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void updateStatus() {
        Date today = new Date(System.currentTimeMillis());

        if (today.getTime() - getPaidUntil().getTime() >= Member.INACTIVATION_PERIOD) {
            setStatus(MemberStatus.INACTIVE);
        } else {
            if (getPaidUntil().before(today)) {
                setStatus(MemberStatus.DUE_WITH_PAYMENT);
            } else if (getPaidUntil().getTime() - today.getTime() <= Member.BEFORE_END_OF_PAYMENT_PERIOD) {
                setStatus(MemberStatus.PAYMENT_IMMINENT);
            } else {
                setStatus(MemberStatus.PAYMENT_OK);
            }
        }
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }


    public Date getPaidUntil() {
        return paidUntil;
    }

    public void setPaidUntil(Date paidUntil) {
        this.paidUntil = paidUntil;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIconPath() {
        return ICON_PATH;
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    public void setContactManager(ContactManager contactManager) {
        this.contactManager = contactManager;
    }

    @Override
    public String toString() {
        return  "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate=" + birthDate +
                ", joinedDate=" + joinedDate +
                ", paidUntil=" + paidUntil +
                ", note='" + note + '\'' +
                ", contactManager=" + contactManager.toString();
    }
}
