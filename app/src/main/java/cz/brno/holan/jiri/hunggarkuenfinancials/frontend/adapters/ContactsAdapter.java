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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.contacts.Contact;

/**
 * Created by mhatina on 06/09/16.
 */
public class ContactsAdapter  extends ArrayAdapter<Contact> {
    public ContactsAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ContactsAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());

            convertView = layoutInflater.inflate(R.layout.layout_contact, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact contact = getItem(position);

        if (contact != null) {
            viewHolder.contact.setText(contact.getContent());
            viewHolder.note.setText(contact.getNote());
            viewHolder.type.setImageResource(contact.getIconPath());
        }

        return convertView;
    }

    @Override
    public void sort(Comparator<? super Contact> comparator) {
        super.sort(comparator);
    }

    public class ViewHolder {
        public TextView contact;
        public TextView note;
        public ImageView type;

        ViewHolder(View view) {
            contact = (TextView) view.findViewById(R.id.contact);
            note = (TextView) view.findViewById(R.id.contact_note);
            type = (ImageView) view.findViewById(R.id.contact_type);
        }
    }
}
