package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.lang.reflect.Field;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Contact;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ContactManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.CreateNewMemberActivity;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.widgets.TextInputLayout;

/**
 * Created by mhatina on 25/09/16.
 */
// todo refactor
public class NewContactFragment extends DialogFragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private ImageButton contact_type;
    private TextInputLayout contact_content;
    private TextInputLayout contact_note;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_contact_new, null);
        contact_content = (TextInputLayout) view.findViewById(R.id.new_contact_content);
        contact_note = (TextInputLayout) view.findViewById(R.id.new_contact_note);
        contact_type = (ImageButton) view.findViewById(R.id.new_contact_type);
        contact_type.setOnClickListener(this);

        CreateNewMemberActivity.setImageButtonResource(contact_type, R.drawable.home_black);

        builder.setMessage("New contact")
                .setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });


        // Create the AlertDialog object and return it
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
                CreateNewMemberActivity.setImageButtonResource(contact_type, R.drawable.home_black);
                contact_content.setHint("Address");
                return true;
            case R.id.type_mail:
                CreateNewMemberActivity.setImageButtonResource(contact_type, R.drawable.mail_black);
                contact_content.setHint("Mail");
                return true;
            case R.id.type_phone:
                CreateNewMemberActivity.setImageButtonResource(contact_type, R.drawable.phone_black);
                contact_content.setHint("Phone");
                return true;
            default:
                return false;
        }
    }

    public void showContactTypePopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.contact_type, popup.getMenu());

        try {
            Field field = popup.getClass().getDeclaredField("contactPopup");
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

    public boolean saveContact() {
        ListView contact_list = (ListView) getActivity().findViewById(R.id.create_new_contact_list);
        Contact contact;
        ArrayAdapter<Contact> adapter;
        String contact_note_string = contact_note.getEditText().getText().toString();

        if (contact_content.getEditText().getText().toString().isEmpty()) {
            contact_content.setError("This field is required.");
            return false;
        }

        contact = ContactManager.createContact((int) contact_type.getTag(), contact_content.getEditText().getText().toString(), contact_note_string);
        adapter = ((ArrayAdapter<Contact>) contact_list.getAdapter());
        adapter.add(contact);
        return true;
    }
}
