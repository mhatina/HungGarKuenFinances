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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;

public class PaymentsAdapter extends ArrayAdapter<Payment> {

    public PaymentsAdapter(Context context, List<Payment> items) {
        super(context, R.layout.layout_payment, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
            String members;
            String owns = convertView.getResources().getString(R.string.payment_fully_paid_for);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
            Product product = ProductManager.getInstance().findProduct(payment.getProductId());

            try {
                members = MemberManager.getInstance().getMemberNamesByIds(payment.getMemberIds());
            } catch (Exception e) {
                members = convertView.getResources().getString(R.string.deleted);
            }
            if (payment.getPrice() != payment.getPaid()) {
                owns = convertView.getResources().getString(R.string.payment_still_owns) + " " + convertView.getResources().getString(R.string.currency, String.valueOf(payment.getPrice() - payment.getPaid()));
            }

            viewHolder.members.setText(members);
            viewHolder.paidPrice.setText(convertView.getResources().getString(R.string.currency, String.valueOf(payment.getPaid())));
            viewHolder.product.setText(product == null ? convertView.getResources().getString(R.string.deleted) : product.getName());
            viewHolder.owns.setText(owns);
            viewHolder.created.setText(format.format(payment.getCreated()));
        }

        return convertView;
    }

    private class ViewHolder {
        public final TextView members;
        public final TextView paidPrice;
        public final TextView product;
        public final TextView owns;
        public final TextView created;

        ViewHolder(View view) {
            members = (TextView) view.findViewById(R.id.payment_layout_members);
            paidPrice = (TextView) view.findViewById(R.id.payment_layout_paid);
            product = (TextView) view.findViewById(R.id.payment_layout_product);
            owns = (TextView) view.findViewById(R.id.payment_layout_owns);
            created = (TextView) view.findViewById(R.id.payment_layout_created);

        }
    }
}
