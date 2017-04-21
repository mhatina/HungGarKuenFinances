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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners;

import android.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.PaymentDetailDialog;

public class PaymentListOnItemClickListener implements AdapterView.OnItemClickListener {
    private final ListView paymentList;
    private final FragmentManager fragmentManager;

    public PaymentListOnItemClickListener(ListView paymentList, FragmentManager fragmentManager) {
        this.paymentList = paymentList;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PaymentDetailDialog newFragment = new PaymentDetailDialog();

        if (paymentList == null)
            return;

        newFragment.init((Payment) paymentList.getItemAtPosition(position));
        newFragment.show(fragmentManager, "payment_detail");
    }
}
