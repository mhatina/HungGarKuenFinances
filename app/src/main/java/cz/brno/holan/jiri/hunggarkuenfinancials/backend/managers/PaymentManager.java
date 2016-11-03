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

package cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers;


import android.content.Context;
import android.net.Uri;

import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.ArrayList;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities.MainActivity;

public class PaymentManager extends BaseManager {
    private ArrayList<Payment> mPayments;
    private long newPaymentId;

    private static PaymentManager ourInstance = new PaymentManager();

    public static PaymentManager getInstance() {
        return ourInstance;
    }

    private PaymentManager() {
        mPayments = new ArrayList<>();
        getDatabaseReference().keepSynced(true);
    }

    public void deletePayment(Payment payment) {

    }

    @Override
    public void load(MainActivity activity) {

    }

    @Override
    public void upload(BaseEntity entity) {

    }

    @Override
    public void update(BaseEntity entity) {

    }

    @Override
    public void delete(BaseEntity entity) {

    }

    @Override
    public DatabaseReference getDatabaseReference() {
        return mDatabase.child("payments");
    }

    @Override
    public String exportDescription() {
        return null;
    }

    @Override
    public void importFromFile(Context context, Uri uri) throws IOException {

    }

    @Override
    public String importDescription() {
        return null;
    }
}
