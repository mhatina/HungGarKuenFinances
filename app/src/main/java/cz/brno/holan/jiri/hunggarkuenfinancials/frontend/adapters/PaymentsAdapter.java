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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;

public class PaymentsAdapter extends ArrayAdapter<Payment> {
    public PaymentsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PaymentsAdapter(Context context, int resource, List<Payment> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());

            convertView = layoutInflater.inflate(R.layout.layout_payment, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Payment payment = getItem(position);

        if (payment != null) {
            String members = "";
            List<Long> memberIds = payment.getMemberIds();
            String owns = convertView.getResources().getString(R.string.payment_fully_paid_for);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm");
            for (long id : memberIds) {
                MemberManager manager = MemberManager.getInstance();
                Member member = manager.findMember(id);
                members += member.getName() + " " + member.getSurname();

                if (id != memberIds.get(memberIds.size() - 1))
                    members += ", ";
            }

            if (payment.getPrice() != payment.getPaid()) {
                owns = convertView.getResources().getString(R.string.payment_still_owns)
                        + " "
                        + convertView.getResources().getString(R.string.currency, String.valueOf(payment.getPrice() - payment.getPaid()));
            }

            viewHolder.members.setText(members);
            viewHolder.paidPrice.setText(convertView.getResources().getString(R.string.currency, String.valueOf(payment.getPaid())));
            viewHolder.product.setText(ProductManager.getInstance().findProduct(payment.getProductId()).getName());
            viewHolder.owns.setText(owns);
            viewHolder.created.setText(format.format(payment.getCreated()));
        }

        return convertView;
    }

    public class ViewHolder {
        public TextView members;
        public TextView paidPrice;
        public TextView product;
        public TextView owns;
        public TextView created;

        ViewHolder(View view) {
            members = (TextView) view.findViewById(R.id.payment_layout_members);
            paidPrice = (TextView) view.findViewById(R.id.payment_layout_paid);
            product = (TextView) view.findViewById(R.id.payment_layout_product);
            owns = (TextView) view.findViewById(R.id.payment_layout_owns);
            created = (TextView) view.findViewById(R.id.payment_layout_created);

        }
    }
}
