/*
 * Copyright (C) 2017  Martin Hatina
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;

public class CreatePaymentMembersAdapter extends ArrayAdapter<Member> {
    public CreatePaymentMembersAdapter(Context context, int resource, List<Member> objects) {
        super(context, resource, objects);
    }

    public CreatePaymentMembersAdapter(Context context, int resource, int textViewResourceId, List<Member> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());

            convertView = layoutInflater.inflate(R.layout.layout_payment_new_list_item, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Member member = getItem(position);

        if (member != null) {
            viewHolder.name.setText(member.getName() + " " + member.getSurname());

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date birthDate = member.getBirthDate();
            if (birthDate != null) {
                viewHolder.detail.setVisibility(View.VISIBLE);
                viewHolder.detail.setText(format.format(birthDate));
            } else
                viewHolder.detail.setVisibility(View.GONE);
        }

        return convertView;
    }

    public class ViewHolder {
        public TextView name;
        public TextView detail;

        ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.create_new_payment_list_name);
            detail = (TextView) view.findViewById(R.id.create_new_payment_list_detail);
        }
    }
}
