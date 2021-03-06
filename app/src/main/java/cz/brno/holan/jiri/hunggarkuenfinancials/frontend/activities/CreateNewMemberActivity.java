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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Contact;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Adult;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Child;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Junior;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Youngster;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ContactManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ContactsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.NewContactFragment;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

import static cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity.setForceShowIcon;

public class CreateNewMemberActivity extends CreateNewEntityActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private ContactManager contactManager;
    private Contact contextContact;
    private boolean beginner = true;

    public CreateNewMemberActivity() {
        super(R.layout.layout_member_new);
    }

    @Override
    public void init() {
        ImageButton type = (ImageButton) findViewById(R.id.create_new_member_type);
        ListView contact_list = (ListView) findViewById(R.id.create_new_contact_list);

        setTitle(getString(R.string.new_member_title));

        setImageButtonResource(type, Adult.ICON_PATH);
        contactManager = new ContactManager();
        contact_list.setAdapter(new ContactsAdapter(this, R.layout.layout_contact_new, contactManager.getContacts()));
        setDate(R.id.create_new_day_of_joining, R.id.create_new_month_of_joining, R.id.create_new_year_of_joining, new Date(System.currentTimeMillis()));
    }

    @Override
    public void initForEdit() {
        ImageButton type = (ImageButton) findViewById(R.id.create_new_member_type);
        TextInputLayout name = (TextInputLayout) findViewById(R.id.create_new_member_name);
        TextInputLayout surname = (TextInputLayout) findViewById(R.id.create_new_member_surname);
        TextInputLayout note = (TextInputLayout) findViewById(R.id.create_new_note);
        ListView contactList = (ListView) findViewById(R.id.create_new_contact_list);
        ImageView beginner = (ImageView) findViewById(R.id.create_new_member_beginner);
        Member member = MemberManager.getInstance().findMember(getIntent().getLongExtra(Constant.EDIT_ENTITY, 0));

        setTitle(getString(R.string.edit_member_title));

        contactManager = member.getContactManager();
        setImageButtonResource(type, member.getIconPath());
        this.beginner = member.isBeginner();
        beginner.setVisibility(this.beginner ? View.VISIBLE : View.GONE);
        setEditTextContent(name, member.getName());
        setEditTextContent(surname, member.getSurname());
        if (member.getBirthDate() != null)
            setDate(R.id.create_new_day_of_birth, R.id.create_new_month_of_birth, R.id.create_new_year_of_birth, member.getBirthDate());
        setDate(R.id.create_new_day_of_joining, R.id.create_new_month_of_joining, R.id.create_new_year_of_joining, member.getJoinedDate());
        setEditTextContent(note, member.getNote());
        contactList.setAdapter(new ContactsAdapter(this, R.layout.layout_contact, member.getContactManager().getContacts()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextInputLayout name = (TextInputLayout) findViewById(R.id.create_new_member_name);
        ListView contact_list = (ListView) findViewById(R.id.create_new_contact_list);
        Button new_contact = (Button) findViewById(R.id.new_contact_button);
        ImageButton type = (ImageButton) findViewById(R.id.create_new_member_type);

        new_contact.setOnClickListener(this);
        type.setOnClickListener(this);
        registerForContextMenu(contact_list);
        name.requestFocus();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ListView contact_list = (ListView) findViewById(R.id.create_new_contact_list);

        contextContact = (Contact) contact_list.getItemAtPosition(adapterMenuInfo.position);
        menu.setHeaderTitle(contextContact.getContent());

        menu.add(0, v.getId(), 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final ListView contact_list = (ListView) findViewById(R.id.create_new_contact_list);

        if (item.getTitle().equals(getString(R.string.delete))) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_contact_title) + " " + contextContact.getContent())
                    .setMessage(R.string.sure_to_delete_contact)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (contact_list.getAdapter() instanceof ArrayAdapter) {
                                @SuppressWarnings("unchecked") ArrayAdapter<Contact> arrayAdapter = (ArrayAdapter<Contact>) contact_list.getAdapter();
                                arrayAdapter.remove(contextContact);
                                contextContact = null;
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_contact_button:
                NewContactFragment newFragment = new NewContactFragment();
                newFragment.setContactManager(contactManager);
                newFragment.show(getFragmentManager(), "new_contact");
                break;
            case R.id.create_new_member_type:
                showMemberTypePopup(view);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        ImageButton button = (ImageButton) findViewById(R.id.create_new_member_type);
        ImageView beginnerView = (ImageView) findViewById(R.id.create_new_member_beginner);

        switch (item.getItemId()) {
            case R.id.type_adult:
                setImageButtonResource(button, Adult.ICON_PATH);
                return true;
            case R.id.type_youngster:
                setImageButtonResource(button, Youngster.ICON_PATH);
                return true;
            case R.id.type_junior:
                setImageButtonResource(button, Junior.ICON_PATH);
                return true;
            case R.id.type_child:
                setImageButtonResource(button, Child.ICON_PATH);
                return true;
            case R.id.type_beginner:
                beginner = !item.isChecked();
                item.setChecked(beginner);
                beginnerView.setVisibility(beginner ? View.VISIBLE : View.GONE);
            default:
                return false;
        }
    }

    private void showMemberTypePopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();

        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.member_type, popup.getMenu());

        setForceShowIcon(popup);

        MenuItem item = popup.getMenu().findItem(R.id.type_beginner);
        item.setChecked(beginner);

        popup.show();
    }

    public boolean save() {
        Member member;
        MemberManager manager = MemberManager.getInstance();

        ImageButton type = (ImageButton) findViewById(R.id.create_new_member_type);
        TextInputLayout note = (TextInputLayout) findViewById(R.id.create_new_note);

        String name = verifyName(R.id.create_new_member_name);
        String surname = verifyName(R.id.create_new_member_surname);

        Date birth_date = verifyDate(R.id.create_new_day_of_birth, R.id.create_new_month_of_birth, R.id.create_new_year_of_birth, false);
        Date joined_date = verifyDate(R.id.create_new_day_of_joining, R.id.create_new_month_of_joining, R.id.create_new_year_of_joining, true);

        TextInputLayout year = (TextInputLayout) findViewById(R.id.create_new_year_of_birth);
        if (name == null || surname == null || joined_date == null || year.getError() != null) {
            return false;
        } else if (getIntent().hasExtra(Constant.EDIT_ENTITY)) {
            member = manager.findMember(getIntent().getLongExtra(Constant.EDIT_ENTITY, 0));
            if (member.getIconPath() != (int) type.getTag()) {
                Member newMember = manager.createMember((int) type.getTag(), name, surname, birth_date);
                manager.replaceMember(member, newMember);
                member = newMember;
            } else {
                member.setName(name);
                member.setSurname(surname);
                member.setBirthDate(birth_date);
            }
            member.setJoinedDate(joined_date);
            member.setNote(getEditTextContent(note));
            member.setBeginner(beginner);

            manager.update(member);
        } else {
            member = manager.createMember((int) type.getTag(), name, surname, birth_date);
            member.setContactManager(contactManager);
            member.setJoinedDate(joined_date);
            member.setNote(getEditTextContent(note));
            member.setBeginner(beginner);

            manager.addMember(member);
        }

        setResult(0);
        finish();
        return true;
    }

    public static void setImageButtonResource(ImageButton button, int resource) {
        button.setImageResource(resource);
        button.setTag(resource);
    }

    private Date verifyDate(int dayResource, int monthResource, int yearResource, boolean required) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        TextInputLayout dayText = (TextInputLayout) findViewById(dayResource);
        TextInputLayout monthText = (TextInputLayout) findViewById(monthResource);
        TextInputLayout yearText = (TextInputLayout) findViewById(yearResource);
        Date date;
        int day;
        int month;
        int year;

        yearText.setError(null);

        if (getEditTextContent(dayText).isEmpty()
                && getEditTextContent(monthText).isEmpty()
                && getEditTextContent(yearText).isEmpty()) {
            if (required)
                yearText.setError(getString(R.string.date_required_error));
            return null;
        }

        try {
            day = Integer.parseInt(getEditTextContent(dayText));
            month = Integer.parseInt(getEditTextContent(monthText));
            year = Integer.parseInt(getEditTextContent(yearText));

            format.setLenient(false);
            date = format.parse(year + "-" + month + "-" + day);
        } catch (ParseException | NumberFormatException e) {
            yearText.setError(getString(R.string.wrong_date_format_error));
            return null;
        }

        if (date.after(new Date(System.currentTimeMillis()))) {
            yearText.setError(getString(R.string.date_in_future_error));
            return null;
        }

        return date;
    }

    private String verifyName(int nameResource) {
        TextInputLayout name = (TextInputLayout) findViewById(nameResource);
        if (getEditTextContent(name).isEmpty()) {
            name.setError(getString(R.string.required_error));
            return null;
        } else {
            name.setError(null);
        }
        return capitalize(getEditTextContent(name));
    }

    private String capitalize(String str) {
        String[] split = str.split(" ");
        String result = "";
        for (String str_part : split)
            result += str_part.substring(0, 1).toUpperCase() + str_part.substring(1).toLowerCase() + " ";

        return result.charAt(result.length() - 1) == ' ' ? result.substring(0, result.length() - 1) : result;
    }

    private void setDate(int day, int month, int year, Date date) {
        TextInputLayout dayLayout = (TextInputLayout) findViewById(day);
        TextInputLayout monthLayout = (TextInputLayout) findViewById(month);
        TextInputLayout yearLayout = (TextInputLayout) findViewById(year);

        setEditTextContent(dayLayout, String.format(Locale.getDefault(), "%td", date));
        setEditTextContent(monthLayout, String.format(Locale.getDefault(), "%tm", date));
        setEditTextContent(yearLayout, String.format(Locale.getDefault(), "%tY", date));
    }
}