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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Contact;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ContactsAdapter;

public class MemberDetailDialog extends DialogFragment implements MenuItem.OnMenuItemClickListener {

    public static final int MEMBER_DETAIL_CONTEXT_GROUP_ID = 1;

    private Member member = null;
    private View view;
    private Contact contextContact;

    public MemberDetailDialog() {
    }

    public void init(Member member) {
        this.member = member;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if (member == null) {
            dismiss();
            return null;
        }

        view = inflater.inflate(R.layout.layout_member_detail, null);
        ListView contactList = (ListView) view.findViewById(R.id.member_detail_contact_list);
        ImageView type = (ImageView) view.findViewById(R.id.member_layout_icon);
        TextView name = (TextView) view.findViewById(R.id.member_layout_first_name);
        TextView surname = (TextView) view.findViewById(R.id.member_layout_surname);
        TextView birthDate = (TextView) view.findViewById(R.id.member_detail_date_of_birth);
        TextView joinDate = (TextView) view.findViewById(R.id.member_detail_date_of_joining);
        TextView note = (TextView) view.findViewById(R.id.member_detail_note);

        contactList.setAdapter(
                new ContactsAdapter(
                        getActivity(),
                        R.layout.layout_contact,
                        member.getContactManager().getContacts()
                )
        );

        type.setImageResource(member.getIconPath());
        name.setText(member.getName());
        surname.setText(member.getSurname());
        setPaymentStatus(member);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (member.getBirthDate() != null)
            birthDate.setText(formatter.format(member.getBirthDate()));
        joinDate.setText(formatter.format(member.getJoinedDate()));
        note.setText(member.getNote());

        registerForContextMenu(contactList);
        contactList.setOnCreateContextMenuListener(this);

        builder.setMessage(R.string.member_details_title)
                .setView(view)
                .setNegativeButton(R.string.hide, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.pay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO open new payment activity
                    }
                });

        return builder.create();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ListView contactList = (ListView) view.findViewById(R.id.member_detail_contact_list);
        ;

        contextContact = (Contact) contactList.getItemAtPosition(adapterMenuInfo.position);
        menu.setHeaderTitle(contextContact.getNote());
        menu.removeGroup(MainActivity.MEMBER_CONTEXT_GROUP_ID);
        menu.add(1, v.getId(), 0, contextContact.getRunDescription(getActivity()));
        menu.getItem(0).setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        contextContact.run(getActivity());
        return false;
    }

    private void setPaymentStatus(Member member) {
        member.updateStatus();

        switch (member.getStatus()) {
            case INACTIVE:
                setPaymentStatusIcons(Member.PAYMENT_INACTIVE, false);
                break;
            case DUE_WITH_PAYMENT:
                setPaymentStatusIcons(Member.PAYMENT_IMMINENT, true);
                break;
            case PAYMENT_IMMINENT:
                setPaymentStatusIcons(Member.PAYMENT_IMMINENT, false);
                break;
            case PAYMENT_OK:
                setPaymentStatusIcons(Member.PAYMENT_OK, false);
                break;
        }
    }

    private void setPaymentStatusIcons(int paymentStatus, boolean showWarning) {
        ImageView paymentStatusView = (ImageView) view.findViewById(R.id.member_layout_payment_status);
        ImageView paymentWarning = (ImageView) view.findViewById(R.id.member_layout_payment_warning);

        paymentWarning.setVisibility(showWarning ? View.VISIBLE : View.INVISIBLE);
        paymentStatusView.setImageResource(paymentStatus);
    }
}
