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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;

public class PaymentDetailDialog extends DialogFragment {

    private Payment payment = null;
    private View view;

    public PaymentDetailDialog() {
    }

    public void init(Payment payment) {
        this.payment = payment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if (payment == null) {
            dismiss();
            return null;
        }

        view = inflater.inflate(R.layout.layout_payment_detail, null);
        TextView members = (TextView) view.findViewById(R.id.payment_layout_members);
        TextView paidPrice = (TextView) view.findViewById(R.id.payment_layout_paid);
        TextView productView = (TextView) view.findViewById(R.id.payment_layout_product);
        TextView ownsView = (TextView) view.findViewById(R.id.payment_layout_owns);
        TextView created = (TextView) view.findViewById(R.id.payment_layout_created);
        TextView validUntil = (TextView) view.findViewById(R.id.payment_detail_valid_until);
        TextView discount = (TextView) view.findViewById(R.id.payment_detail_discount);

        String owns = getResources().getString(R.string.payment_fully_paid_for);
        SimpleDateFormat createdFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        SimpleDateFormat validFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Product product = ProductManager.getInstance().findProduct(payment.getProductId());

        if (payment.getPrice() != payment.getPaid()) {
            owns = getResources().getString(R.string.payment_still_owns) + " " + getResources().getString(R.string.currency, String.valueOf(payment.getPrice() - payment.getPaid()));
        }

        try {
            members.setText(MemberManager.getInstance().getMemberNamesByIds(payment.getMemberIds()));
        } catch (Exception e) {
            members.setText(getResources().getString(R.string.deleted));
        }
        paidPrice.setText(getResources().getString(R.string.currency, String.valueOf(payment.getPaid())));
        productView.setText(product == null ? getResources().getString(R.string.deleted) : product.getName());
        ownsView.setText(owns);
        created.setText(createdFormat.format(payment.getCreated()));
        if (payment.getValidUntil() != null)
            validUntil.setText(validFormat.format(payment.getValidUntil()));
        discount.setText(String.format("%s%%", String.valueOf(payment.getDiscount())));

        builder.setMessage(R.string.payment_details_title)
                .setView(view)
                .setNegativeButton(R.string.hide, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
