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
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Contact;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ContactManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ContactsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.NewContactFragment;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.widgets.TextInputLayout;

public class CreateNewMemberActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    public static final String TAG = "CreateNewMemberActivity";
    public static final String CREATE_EDIT_ENTITY = "CREATE_EDIT_ENTITY";

    private ContactManager contactManager;
    private Contact contextContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        addViewToActivity(R.layout.layout_member_new);

        ListView contact_list = (ListView) findViewById(R.id.create_new_contact_list);
        ImageButton type = (ImageButton) findViewById(R.id.create_new_member_type);
        Button new_contact = (Button) findViewById(R.id.new_contact_button);
        new_contact.setOnClickListener(this);
        registerForContextMenu(contact_list);

        if (getIntent().hasExtra(CREATE_EDIT_ENTITY)) {
            setTitle("Edit member");

            TextInputLayout name = (TextInputLayout) findViewById(R.id.create_new_member_name);
            TextInputLayout surname = (TextInputLayout) findViewById(R.id.create_new_member_surname);
            TextInputLayout note = (TextInputLayout) findViewById(R.id.create_new_note);

            Member member = MemberManager.getInstance().findMember(getIntent().getLongExtra(CREATE_EDIT_ENTITY, 0));

            contactManager = member.getContactManager();
            setImageButtonResource(type, member.getIconPath());
            setEditTextContent(name, member.getFirstName());
            setEditTextContent(surname, member.getSurname());
            setDate(R.id.create_new_day_of_birth, R.id.create_new_month_of_birth, R.id.create_new_year_of_birth, member.getBirthDate());
            setDate(R.id.create_new_day_of_joining, R.id.create_new_month_of_joining, R.id.create_new_year_of_joining, member.getJoinedDate());
            setEditTextContent(note, member.getNote());
            contact_list.setAdapter(new ContactsAdapter(this, R.layout.layout_contact, member.getContactManager().getContacts()));
        } else {
            setTitle("New member");
            setImageButtonResource(type, R.drawable.adult_icon);
            contactManager = new ContactManager();
            contact_list.setAdapter(new ContactsAdapter(this, R.layout.layout_contact_new, contactManager.getContacts()));
            setDate(R.id.create_new_day_of_joining, R.id.create_new_month_of_joining, R.id.create_new_year_of_joining, new Date(System.currentTimeMillis()));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ListView contact_list = (ListView) findViewById(R.id.create_new_contact_list);

        contextContact = (Contact) contact_list.getItemAtPosition(adapterMenuInfo.position);
        menu.setHeaderTitle(contextContact.getContent());

        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final ListView contact_list = (ListView) findViewById(R.id.create_new_contact_list);

        if (item.getTitle().equals("Delete")) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete contact " + contextContact.getContent())
                    .setMessage("Are you sure you want to delete contact?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayAdapter<Contact> arrayAdapter = (ArrayAdapter<Contact>) contact_list.getAdapter();
                            arrayAdapter.remove(contextContact);
                            contextContact = null;
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
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            save();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_contact_button:
                NewContactFragment newFragment = new NewContactFragment();
                newFragment.show(getFragmentManager(), "new_contact");
                break;
            case R.id.create_new_member_type:
                view.showContextMenu();
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        ImageButton button = (ImageButton) findViewById(R.id.create_new_member_type);

        switch (item.getItemId()) {
            case R.id.type_adult:
                setImageButtonResource(button, R.drawable.adult_icon);
                return true;
            case R.id.type_junior:
                setImageButtonResource(button, R.drawable.junior_icon);
                return true;
            case R.id.type_child:
                setImageButtonResource(button, R.drawable.child_icon);
                return true;
            default:
                return false;
        }
    }

    public void showMemberTypePopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.member_type, popup.getMenu());

        try {
            Field field = popup.getClass().getDeclaredField("memberPopup");
            field.setAccessible(true);
            MenuPopupHelper popupHelper = (MenuPopupHelper) field.get(popup);
            popupHelper.setForceShowIcon(true);
        } catch (IllegalAccessException e) {
            return;
        } catch (NoSuchFieldException e) {
            return;
        }
        popup.show();
    }

    private void addViewToActivity(int layoutId) {
        View view = getLayoutInflater().inflate(layoutId, null);
        ScrollView parent = (ScrollView) findViewById(R.id.create_new_scroll_view);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        parent.addView(view);
    }

    private boolean save() {
        Member member;
        MemberManager manager = MemberManager.getInstance();

        ImageButton type = (ImageButton) findViewById(R.id.create_new_member_type);
        TextInputLayout note = (TextInputLayout) findViewById(R.id.create_new_note);

        String name = verifyName(R.id.create_new_member_name);
        String surname = verifyName(R.id.create_new_member_surname);

        Date birth_date = verifyDate(R.id.create_new_day_of_birth, R.id.create_new_month_of_birth, R.id.create_new_year_of_birth);
        Date joined_date = verifyDate(R.id.create_new_day_of_joining, R.id.create_new_month_of_joining, R.id.create_new_year_of_joining);

        if (name == null || surname == null || birth_date == null || joined_date == null) {
            return false;
        } else if (getIntent().hasExtra(CREATE_EDIT_ENTITY)) {
            member = manager.findMember(getIntent().getLongExtra(CREATE_EDIT_ENTITY, 0));
            if (member.getIconPath() != type.getTag()) {
                Member newMember = manager.createMember((int) type.getTag(), name, surname, birth_date);
                manager.replaceMember(member, newMember);
                member = newMember;
            } else {
                member.setFirstName(name);
                member.setSurname(surname);
                member.setBirthDate(birth_date);
            }
        } else {
            member = manager.createMember((int) type.getTag(), name, surname, birth_date);
            member.setContactManager(contactManager);
            manager.addMember(member);
        }
        member.setJoinedDate(joined_date);
        member.setNote(getEditTextContent(note));

        setResult(0);
        finish();
        return true;
    }

    public static void setImageButtonResource(ImageButton button, int resource) {
        button.setImageResource(resource);
        button.setTag(resource);
    }

    private Date verifyDate(int dayResource, int monthResource, int yearResource) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        TextInputLayout dayText = (TextInputLayout) findViewById(dayResource);
        TextInputLayout monthText = (TextInputLayout) findViewById(monthResource);
        TextInputLayout yearText = (TextInputLayout) findViewById(yearResource);
        Date date;
        int day;
        int month;
        int year;

        yearText.setError(null);

        if (getEditTextContent(dayText).isEmpty()
                || getEditTextContent(monthText).isEmpty()
                || getEditTextContent(yearText).isEmpty()) {
            yearText.setError("Date is required.");
            return null;
        }

        try {
            day = Integer.parseInt(getEditTextContent(dayText));
            month = Integer.parseInt(getEditTextContent(monthText));
            year = Integer.parseInt(getEditTextContent(yearText));

            format.setLenient(false);
            date = format.parse(year + "-" + month + "-" + day);
        } catch (ParseException | NumberFormatException e) {
            yearText.setError("Wrong format of date");
            return null;
        }

        if (date.after(new Date(System.currentTimeMillis()))) {
            yearText.setError("Date cannot be in future");
            return null;
        }

        return date;
    }

    private String verifyName(int nameResource) {
        TextInputLayout name = (TextInputLayout) findViewById(nameResource);
        if (getEditTextContent(name).isEmpty()) {
            name.setError("This field is required.");
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

        setEditTextContent(dayLayout, String.format("%td", date));
        setEditTextContent(monthLayout, String.format("%tm", date));
        setEditTextContent(yearLayout, String.format("%tY", date));
    }

    @NonNull
    private void setEditTextContent(TextInputLayout layout, String content) {
        getEditText(layout).setText(content);
    }

    @NonNull
    private String getEditTextContent(TextInputLayout note) {
        return getEditText(note).getText().toString();
    }

    @NonNull
    private EditText getEditText(TextInputLayout layout) {
        EditText editText = layout.getEditText();
        if (editText == null) {
            throw new NullPointerException(layout.toString());
        }
        return editText;
    }
}