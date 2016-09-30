package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments;

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
import android.widget.Toast;

import java.text.SimpleDateFormat;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Contact;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ContactsAdapter;

/**
 * Created by mhatina on 29/09/16.
 */
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

        contactList.setAdapter(new ContactsAdapter(getActivity(), R.layout.layout_contact, member.getContactManager().getContacts()));
        type.setImageResource(member.getIconPath());
        name.setText(member.getFirstName());
        surname.setText(member.getSurname());
        setPaymentStatus(member);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        birthDate.setText(formatter.format(member.getBirthDate()));
        joinDate.setText(formatter.format(member.getJoinedDate()));
        note.setText(member.getNote());

        registerForContextMenu(contactList);
        contactList.setOnCreateContextMenuListener(this);

        builder.setMessage("Member details")
                .setView(view)
                .setNegativeButton("Hide", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ListView contactList = (ListView) view.findViewById(R.id.member_detail_contact_list);;

        contextContact = (Contact) contactList.getItemAtPosition(adapterMenuInfo.position);
        menu.setHeaderTitle(contextContact.getNote());
        menu.removeGroup(MainActivity.MEMBER_CONTEXT_GROUP_ID);
        menu.add(1, v.getId(), 0, contextContact.getRunDescription());
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
