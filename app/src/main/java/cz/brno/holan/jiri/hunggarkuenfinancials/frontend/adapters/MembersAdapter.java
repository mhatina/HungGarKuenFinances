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
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Comparator;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;

/**
 * Created by mhatina on 24/08/16.
 */
public class MembersAdapter extends ArrayAdapter<Member> {

    public MembersAdapter(Context context, int resource, List<Member> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());

            convertView = layoutInflater.inflate(R.layout.layout_member, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Member member = getItem(position);

        if (member != null) {
            viewHolder.firstName.setText(member.getName());
            viewHolder.surname.setText(member.getSurname());
            viewHolder.icon.setImageResource(member.getIconPath());

            setPaymentStatus(viewHolder, member);
        }

        return convertView;
    }

    private void setPaymentStatus(ViewHolder viewHolder, Member member) {
        member.updateStatus();

        switch (member.getStatus()) {
            case INACTIVE:
                setPaymentStatusIcons(viewHolder, Member.PAYMENT_INACTIVE, false);
                break;
            case DUE_WITH_PAYMENT:
                setPaymentStatusIcons(viewHolder, Member.PAYMENT_IMMINENT, true);
                break;
            case PAYMENT_IMMINENT:
                setPaymentStatusIcons(viewHolder, Member.PAYMENT_IMMINENT, false);
                break;
            case PAYMENT_OK:
                setPaymentStatusIcons(viewHolder, Member.PAYMENT_OK, false);
                break;
        }
    }

    private void setPaymentStatusIcons(ViewHolder viewHolder, int paymentStatus, boolean showWarning) {
        viewHolder.paymentWarning.setVisibility(showWarning ? View.VISIBLE : View.INVISIBLE);
        viewHolder.paymentStatus.setImageResource(paymentStatus);
    }

    @Override
    public void sort(Comparator<? super Member> comparator) {
        super.sort(comparator);
    }

    public class ViewHolder {
        public TextView firstName;
        public TextView surname;
        public ImageView icon;
        public ImageView paymentStatus;
        public ImageView paymentWarning;

        ViewHolder(View view) {
            firstName = (TextView) view.findViewById(R.id.member_layout_first_name);
            surname = (TextView) view.findViewById(R.id.member_layout_surname);
            icon = (ImageView) view.findViewById(R.id.member_layout_icon);
            paymentStatus = (ImageView) view.findViewById(R.id.member_layout_payment_status);
            paymentWarning = (ImageView) view.findViewById(R.id.member_layout_payment_warning);
        }
    }
}