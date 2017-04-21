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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Address;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Contact;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Mail;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Phone;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ContactManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.CreateNewMemberActivity;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

import static cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.CreateNewEntityActivity.getEditText;
import static cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.CreateNewEntityActivity.getEditTextContent;
import static cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity.setForceShowIcon;

public class NewContactFragment extends DialogFragment implements ImageButton.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private ImageButton contact_type;
    private TextInputLayout contact_content;
    private TextInputLayout contact_note;
    private ContactManager contactManager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.layout_contact_new, null);
        contact_content = (TextInputLayout) view.findViewById(R.id.new_contact_content);
        contact_note = (TextInputLayout) view.findViewById(R.id.new_contact_note);
        contact_type = (ImageButton) view.findViewById(R.id.new_contact_type);
        contact_type.setOnClickListener(this);

        CreateNewMemberActivity.setImageButtonResource(contact_type, Address.ICON_PATH);

        builder.setMessage(R.string.new_contact_title)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (saveContact())
                            alertDialog.dismiss();
                    }
                });
            }
        });

        return alertDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_contact_type:
                showContactTypePopup(v);
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.type_address:
                CreateNewMemberActivity.setImageButtonResource(contact_type, Address.ICON_PATH);
                contact_content.setHint(getActivity().getString(R.string.address));
                getEditText(contact_content).setInputType(InputType.TYPE_CLASS_TEXT);
                return true;
            case R.id.type_mail:
                CreateNewMemberActivity.setImageButtonResource(contact_type, Mail.ICON_PATH);
                contact_content.setHint(getActivity().getString(R.string.mail));
                getEditText(contact_content).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                return true;
            case R.id.type_phone:
                CreateNewMemberActivity.setImageButtonResource(contact_type, Phone.ICON_PATH);
                contact_content.setHint(getActivity().getString(R.string.phone));
                getEditText(contact_content).setInputType(InputType.TYPE_CLASS_PHONE);
                return true;
            default:
                return false;
        }
    }

    private void showContactTypePopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.contact_type, popup.getMenu());

        setForceShowIcon(popup);
        popup.show();
    }

    private boolean saveContact() {
        ListView contact_list = (ListView) getActivity().findViewById(R.id.create_new_contact_list);
        Contact contact;
        ArrayAdapter<Contact> adapter;
        String contact_note_string = getEditTextContent(contact_note);

        if (getEditTextContent(contact_content).isEmpty()) {
            contact_content.setError(getActivity().getString(R.string.required_error));
            return false;
        }

        contact = contactManager.createContact(getActivity(), (int) contact_type.getTag(), getEditTextContent(contact_content), contact_note_string);
        if (contact_list.getAdapter() instanceof ArrayAdapter) {
            //noinspection unchecked
            adapter = (ArrayAdapter<Contact>) contact_list.getAdapter();
            adapter.add(contact);
            return true;
        }

        return false;
    }

    public void setContactManager(ContactManager contactManager) {
        this.contactManager = contactManager;
    }
}
