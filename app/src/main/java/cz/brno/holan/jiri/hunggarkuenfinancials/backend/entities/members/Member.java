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

import java.util.Date;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.enums.MemberStatus;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ContactManager;

public class Member extends BaseEntity {

    public static final int ICON_PATH = R.drawable.lam_ga_hung_kuen_logo;
    public static final int PAYMENT_OK = android.R.drawable.presence_online;
    public static final int PAYMENT_IMMINENT = android.R.drawable.presence_away;
    public static final int PAYMENT_INACTIVE = android.R.drawable.presence_offline;
    public static final long BEFORE_END_OF_PAYMENT_PERIOD = 3 * 24 * 60 * 60 * 1000; // #days * 24h * 60min * 60sec * 1000msec
    public static final long INACTIVATION_PERIOD = 14 * 24 * 60 * 60 * 1000; // #days * 24h * 60min * 60sec * 1000msec

    private String name;
    private String surname;
    private Date birthDate;
    private Date joinedDate;
    private Date paidUntil;
    private MemberStatus status;
    private String note;
    private boolean beginner;

    private int updatePropertiesSwitch = 0;

    private ContactManager contactManager = new ContactManager();

    public Member() {
    }

    public Member(long id, String name, String surname) {
        this(id, name, surname, new Date(System.currentTimeMillis()));
    }

    public Member(long id, String name, String surname, Date birthDate) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.paidUntil = new Date(System.currentTimeMillis());
        this.birthDate = birthDate;
        this.joinedDate = new Date(System.currentTimeMillis());
        this.beginner = true;
        updateStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (name != null ? !name.equals(member.name) : member.name != null) return false;
        if (surname != null ? !surname.equals(member.surname) : member.surname != null)
            return false;
        return birthDate != null ? birthDate.equals(member.birthDate) : member.birthDate == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name == null || !this.name.equals(name)) {
            this.name = name;
            updatePropertiesSwitch |= Constant.NAME_SWITCH;
        }
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        if (this.surname == null || !this.surname.equals(surname)) {
            this.surname = surname;
            updatePropertiesSwitch |= Constant.SURNAME_SWITCH;
        }
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        if (this.birthDate == null || !this.birthDate.equals(birthDate)) {
            this.birthDate = birthDate;
            updatePropertiesSwitch |= Constant.BIRTH_DATE_SWITCH;
        }
    }

    public Date getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        if (this.joinedDate == null || !this.joinedDate.equals(joinedDate)) {
            this.joinedDate = joinedDate;
            updatePropertiesSwitch |= Constant.JOINED_DATE_SWITCH;
        }
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
        if (this.paidUntil != null && paidUntil.before(this.paidUntil))
            return;

        if (this.paidUntil == null || !this.paidUntil.equals(paidUntil)) {
            this.paidUntil = paidUntil;
            updatePropertiesSwitch |= Constant.PAID_UNTIL_SWITCH;
            updateStatus();
        }
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if (this.note == null || !this.note.equals(note)) {
            this.note = note;
            updatePropertiesSwitch |= Constant.NOTE_SWITCH;
        }
    }

    public boolean isBeginner() {
        return beginner;
    }

    public void setBeginner(boolean beginner) {
        this.beginner = beginner;
        updatePropertiesSwitch |= Constant.BEGINNER_SWITCH;
    }

    public int getUpdatePropertiesSwitch() {
        return updatePropertiesSwitch;
    }

    public void clearUpdatePropertiesSwitch() {
        this.updatePropertiesSwitch = 0;
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
        return "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate=" + birthDate +
                ", joinedDate=" + joinedDate +
                ", paidUntil=" + paidUntil +
                ", note='" + note + '\'' +
                ", contactManager=" + contactManager.toString();
    }
}
