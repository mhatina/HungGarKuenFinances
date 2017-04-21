/*
 * Copyright (C) 2016  Martin Hatina
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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Adult;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Child;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Junior;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Youngster;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.OneTimeOnly;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Periodic;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;

public class ProductDetailDialog extends DialogFragment {

    private Product product = null;

    public ProductDetailDialog() {
    }

    public void init(Product product) {
        this.product = product;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        if (product == null) {
            dismiss();
            return null;
        }

        View view = inflater.inflate(R.layout.layout_product_detail, null);
        TextView name = (TextView) view.findViewById(R.id.product_layout_name);
        TextView detail = (TextView) view.findViewById(R.id.product_layout_detail);
        TextView price = (TextView) view.findViewById(R.id.product_layout_price);
        TextView note = (TextView) view.findViewById(R.id.product_detail_note);
        TextView validFor = (TextView) view.findViewById(R.id.product_detail_valid_for);
        LinearLayout groups = (LinearLayout) view.findViewById(R.id.product_detail_groups);

        name.setText(product.getName());
        if (product instanceof Periodic) {
            String perWeek = String.valueOf(((Periodic) product).getPerWeek());
            perWeek += "/" + view.getContext().getString(R.string.week);
            detail.setText(perWeek);
        } else {
            String stock = view.getContext().getString(R.string.in_stock, String.valueOf(((OneTimeOnly) product).getStock()));
            detail.setText(stock);
        }
        price.setText(view.getContext().getString(R.string.currency, String.valueOf(product.getPrice())));
        note.setText(product.getNote());

        String[] validityPeriods = getResources().getStringArray(R.array.validity_periods);
        String validForText = product.getValidTime() + " " + validityPeriods[product.getValidGroup()];
        validFor.setText(product.getValidTime() == -1 ? getResources().getString(R.string.indefinite) : validForText);

        int group = Constant.ADULT_GROUP;
        for (int i = 0; i < Constant.NUMBER_OF_GROUPS; i++) {
            if ((product.getGroup() & group) != 0) {
                ImageView image = new ImageView(getActivity());
                image.setImageResource(getGroupResource(group));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                image.setLayoutParams(layoutParams);

                groups.addView(image);
            }

            group <<= 1;
        }

        builder.setMessage(R.string.product_details_title)
                .setView(view)
                .setNegativeButton(R.string.hide, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    // TODO move to utils
    private int getGroupResource(int group) {
        switch (group) {
            case Constant.ADULT_GROUP:
                return Adult.ICON_PATH;
            case Constant.YOUNGSTER_GROUP:
                return Youngster.ICON_PATH;
            case Constant.JUNIOR_GROUP:
                return Junior.ICON_PATH;
            case Constant.CHILD_GROUP:
                return Child.ICON_PATH;
            default:
                return 0;
        }
    }
}
