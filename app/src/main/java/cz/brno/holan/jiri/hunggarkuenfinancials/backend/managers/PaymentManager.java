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
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.PaymentsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers.EntityTabManager;

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

    public List<Payment> getPayments() {
        return mPayments;
    }

    public List<Payment> getPayments(String filter) {
        String[] split = filter.split(" ");
        ArrayList<Payment> list = new ArrayList<>();

        for (Payment payment : mPayments) {
            List<Member> members = MemberManager.getInstance().getMembers(split[0], split.length > 1 ? split[1] : null);
            for (Member member : members) {
                if (payment.getMemberIds().indexOf(member.getId()) != -1)
                    list.add(payment);
            }

            if (list.indexOf(payment) == -1
                    && ProductManager.getInstance().getProducts(filter).indexOf(payment.getProductId()) != -1) {
                list.add(payment);
            }
        }
        return list;
    }

    public void addPayment(Payment payment) {
        newPaymentId++;
        getDatabaseReference().child("id").setValue(newPaymentId);
        payment.setId(newPaymentId);

        mPayments.add(payment);
        upload(payment);
    }

    public void deletePayment(Payment payment) {
        mPayments.remove(payment);
        delete(payment);
    }

    public Payment findPayment(long id) {
        for (Payment payment : mPayments) {
            if (payment.getId() == id)
                return payment;
        }

        return null;
    }

    public Payment createPayment(List<Long> memberIds, long productId) {
        newPaymentId++;
        getDatabaseReference().child("id").setValue(newPaymentId);

        return new Payment(newPaymentId, memberIds, productId);
    }

    @Override
    public void load() {
        mPayments.clear();

        getDatabaseReference().addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        newPaymentId = dataSnapshot.child("id").getValue(long.class);

                            for (DataSnapshot postSnapshot : dataSnapshot.child(Payment.class.getSimpleName()).getChildren()) {
                                Payment payment = postSnapshot.getValue(Payment.class);
                                mPayments.add(payment);
                            }

                        ListView paymentList = EntityTabManager.getInstance().getPaymentList();
                        if (paymentList != null) {
                            PaymentsAdapter adapter = (PaymentsAdapter) paymentList.getAdapter();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void upload(BaseEntity entity) {
        Payment payment = (Payment) entity;
        DatabaseReference reference = getDatabaseReference().child(payment.getClass().getSimpleName())
                .child(String.valueOf(payment.getId()));

        reference.child("id").setValue(payment.getId());
        reference.child("memberIds").setValue(payment.getMemberIds());
        reference.child("productId").setValue(payment.getProductId());
        reference.child("note").setValue(payment.getNote());
        reference.child("price").setValue(payment.getPrice());
        reference.child("paid").setValue(payment.getPaid());
        reference.child("discount").setValue(payment.getDiscount());
        reference.child("validUntil").setValue(payment.getValidUntil());
        reference.child("created").setValue(payment.getCreated());
    }

    @Override
    public void update(BaseEntity entity) {
        Payment payment = (Payment) entity;
        DatabaseReference reference = getDatabaseReference().child(payment.getClass().getSimpleName())
                .child(String.valueOf(payment.getId()));

        if ((payment.getUpdatePropertiesSwitch() & Constant.MEMBER_IDS_SWITCH) > 0)
            reference.child("memberIds").setValue(payment.getMemberIds());
        if ((payment.getUpdatePropertiesSwitch() & Constant.PRODUCT_ID_SWITCH) > 0)
            reference.child("productId").setValue(payment.getProductId());
        if ((payment.getUpdatePropertiesSwitch() & Constant.NOTE_SWITCH) > 0)
            reference.child("note").setValue(payment.getNote());
        if ((payment.getUpdatePropertiesSwitch() & Constant.PRICE_SWITCH) > 0)
            reference.child("price").setValue(payment.getPrice());
        if ((payment.getUpdatePropertiesSwitch() & Constant.PAID_SWITCH) > 0)
            reference.child("paid").setValue(payment.getPaid());
        if ((payment.getUpdatePropertiesSwitch() & Constant.DISCOUNT_SWITCH) > 0)
            reference.child("discount").setValue(payment.getDiscount());
        if ((payment.getUpdatePropertiesSwitch() & Constant.VALID_TIME_SWITCH) > 0)
            reference.child("validUntil").setValue(payment.getValidUntil());
        if ((payment.getUpdatePropertiesSwitch() & Constant.CREATED_SWITCH) > 0)
            reference.child("created").setValue(payment.getCreated());

        payment.clearUpdatePropertiesSwitch();
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
