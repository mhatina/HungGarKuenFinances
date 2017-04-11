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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.Utils;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.PaymentManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.comparators.MemberAlphabeticComparator;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.CreatePaymentMembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.CreatePaymentProductsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.CreatePaymentOnDiscountChangedListener;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.CreatePaymentOnMemberTextChangedListener;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.CreatePaymentOnPaidTextChangedListener;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.CreatePaymentOnPriceTextChangedListener;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.CreatePaymentOnProductTextChangedListener;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

public class CreateNewPaymentActivity extends CreateNewEntityActivity implements View.OnClickListener {

    private Date validUntil;
    private ArrayList<Long> memberIds = new ArrayList<>();
    private long productId = -1;

    public CreateNewPaymentActivity() {
        super(R.layout.layout_payment_new);
    }

    @Override
    public void init() {
        TextInputLayout discountLayout = (TextInputLayout) findViewById(R.id.create_new_payment_discount);
        discountLayout.getEditText().setText(String.valueOf(0 + "%"));

        setTitle("New payment");

        Member member;
        if (getIntent().hasExtra(Constant.PREFILLED_ENTITY)) {
            TextInputLayout membersLayout = (TextInputLayout) findViewById(R.id.create_new_payment_member);

            member = MemberManager.getInstance().findMember(getIntent().getLongExtra(Constant.PREFILLED_ENTITY, 0));
            memberIds.add(member.getId());
            membersLayout.getEditText().setText(member.getName() + " " + member.getSurname());
        }
    }

    @Override
    public void initForEdit() {
        TextInputLayout membersLayout = (TextInputLayout) findViewById(R.id.create_new_payment_member);
        TextInputLayout productLayout = (TextInputLayout) findViewById(R.id.create_new_payment_product);
        TextInputLayout priceLayout = (TextInputLayout) findViewById(R.id.create_new_payment_price);
        TextInputLayout discountLayout = (TextInputLayout) findViewById(R.id.create_new_payment_discount);
        TextInputLayout paidLayout = (TextInputLayout) findViewById(R.id.create_new_payment_paid);
        TextInputLayout ownsLayout = (TextInputLayout) findViewById(R.id.create_new_payment_owns);
        TextInputLayout noteLayout = (TextInputLayout) findViewById(R.id.create_new_note);
        Button dateButton = (Button) findViewById(R.id.create_new_payment_date);

        Payment payment = PaymentManager.getInstance().findPayment(getIntent().getLongExtra(Constant.EDIT_ENTITY, 0));
        Product product = ProductManager.getInstance().findProduct(payment.getProductId());
        String members = "";

        List<Long> ids = payment.getMemberIds();
        for (long id : ids) {
            Member member = MemberManager.getInstance().findMember(id);
            if (member == null)
                continue;
            members += member.getName() + " " + member.getSurname();

            if (ids.size() != 1 || id != ids.get(ids.size() - 1))
                members += ", ";
        }

        if (members.isEmpty())
            members = getResources().getString(R.string.payment_id_deleted);

        setTitle("Edit payment");

        membersLayout.getEditText().setText(members);
        productLayout.getEditText().setText(product == null
                ? getResources().getString(R.string.payment_id_deleted)
                : product.getName());
        priceLayout.getEditText().setText(String.valueOf(product.getPrice()));

        float discount = (1 - (payment.getPrice() / product.getPrice())) * 100;
        discountLayout.getEditText().setText(String.valueOf(discount + "%"));

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateButton.setText(format.format(payment.getValidUntil()));

        paidLayout.getEditText().setText(String.valueOf(payment.getPaid()));
        ownsLayout.getEditText().setText(String.valueOf(payment.getPrice() - payment.getPaid()));
        noteLayout.getEditText().setText(payment.getNote());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextInputLayout membersLayout = (TextInputLayout) findViewById(R.id.create_new_payment_member);
        TextInputLayout productLayout = (TextInputLayout) findViewById(R.id.create_new_payment_product);
        TextInputLayout priceLayout = (TextInputLayout) findViewById(R.id.create_new_payment_price);
        TextInputLayout discountLayout = (TextInputLayout) findViewById(R.id.create_new_payment_discount);
        TextInputLayout payedLayout = (TextInputLayout) findViewById(R.id.create_new_payment_paid);
        TextInputLayout ownsLayout = (TextInputLayout) findViewById(R.id.create_new_payment_owns);
        ImageButton memberButton = (ImageButton) findViewById(R.id.create_new_payment_member_list);
        ImageButton productButton = (ImageButton) findViewById(R.id.create_new_payment_product_list);
        Button dateButton = (Button) findViewById(R.id.create_new_payment_date);

        CreatePaymentOnPriceTextChangedListener priceListener =
                new CreatePaymentOnPriceTextChangedListener(priceLayout, discountLayout.getEditText());
        CreatePaymentOnDiscountChangedListener discountListener =
                new CreatePaymentOnDiscountChangedListener(priceLayout.getEditText(), discountLayout);

        membersLayout.getEditText().addTextChangedListener(
                new CreatePaymentOnMemberTextChangedListener(membersLayout));
        productLayout.getEditText().addTextChangedListener(
                new CreatePaymentOnProductTextChangedListener(
                        productLayout,
                        priceLayout.getEditText(),
                        dateButton,
                        priceListener,
                        discountListener));
        discountLayout.getEditText().addTextChangedListener(discountListener);
        priceLayout.getEditText().addTextChangedListener(priceListener);
        payedLayout.getEditText().addTextChangedListener(
                new CreatePaymentOnPaidTextChangedListener(ownsLayout, priceLayout.getEditText()));

        memberButton.setOnClickListener(this);
        productButton.setOnClickListener(this);
        dateButton.setOnClickListener(this);
    }

    @Override
    public boolean save() {
        TextInputLayout membersLayout = (TextInputLayout) findViewById(R.id.create_new_payment_member);
        TextInputLayout productLayout = (TextInputLayout) findViewById(R.id.create_new_payment_product);
        TextInputLayout noteLayout = (TextInputLayout) findViewById(R.id.create_new_note);
        Button dateButton = (Button) findViewById(R.id.create_new_payment_date);

        List<Long> memberIds = Utils.getMemberIdsFromString(membersLayout.getEditText().getText().toString(), this.memberIds);
        if (memberIds.isEmpty()) {
            membersLayout.setError(getString(R.string.required_error));
            return false;
        }

        Product product = ProductManager.getInstance().findProduct(productId);
        String productStr = productLayout.getEditText().getText().toString();
        if (product == null || !productStr.equals(product.getName())) {
            List<Product> list = ProductManager.getInstance().getProducts(productStr);
            if (list.isEmpty()) {
                productLayout.setError(getString(R.string.required_error));
                return false;
            }
            product = list.get(0);
        }

        float price = verifyFloat(R.id.create_new_payment_price);
        float paid = verifyFloat(R.id.create_new_payment_paid);
        float discount = verifyFloat(R.id.create_new_payment_discount);
        String note = noteLayout.getEditText().getText().toString();
        Date valid = null;

        if (price == -1 || paid == -1)
            return false;

        Payment payment = null;
        if (getIntent().hasExtra(Constant.EDIT_ENTITY)) {
            payment = PaymentManager.getInstance().findPayment(getIntent().getLongExtra(Constant.EDIT_ENTITY, 0));
            payment.setMemberIds(memberIds);
            payment.setProductId(product.getId());
        } else
            payment = PaymentManager.getInstance().createPayment(memberIds, product.getId());

        payment.setPrice(price);
        payment.setPaid(paid > price ? price : paid);
        payment.setDiscount(discount);
        payment.setNote(note);
        payment.setCreated(new Date(System.currentTimeMillis()));
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            valid = format.parse(dateButton.getText().toString());
            payment.setValidUntil(valid);
        } catch (ParseException ex) {
            // valid-until date is not necessary
        }

        if (!getIntent().hasExtra(Constant.EDIT_ENTITY))
            PaymentManager.getInstance().addPayment(payment);

        if (valid != null) {
            for (long id : memberIds) {
                MemberManager manager = MemberManager.getInstance();
                Member member = manager.findMember(id);
                member.setPaidUntil(valid);
                manager.update(member);
            }
        }

        setResult(0);
        finish();

        return true;
    }

    private float verifyFloat(int resource) {
        float f;
        TextInputLayout layout = (TextInputLayout) findViewById(resource);
        String str = layout.getEditText().getText().toString();

        layout.setError(null);

        // in case of discount, which contains '%'
        str = str.replace("%", "");
        if (str.isEmpty()) {
            layout.setError(getString(R.string.required_error));
            return -1;
        }

        try {
            f = Float.valueOf(str);
        } catch (NumberFormatException ex) {
            layout.setError("Incorrect format");
            return -1;
        }
        return f;
    }

    @Override
    public void onClick(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (view.getId()) {
            case R.id.create_new_payment_member_list:
                List<Member> members = MemberManager.getInstance().getMembers();
                Collections.sort(members, new MemberAlphabeticComparator());
                CreatePaymentMembersAdapter adapter = new CreatePaymentMembersAdapter(this,
                        R.layout.layout_payment_new_list_item,
                        members);


                final ListView memberList = new ListView(this);
                memberList.setAdapter(adapter);
                memberList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                builder.setMessage(R.string.member_details_title)
                        .setView(memberList)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextInputLayout memberLayout = (TextInputLayout) findViewById(R.id.create_new_payment_member);
                                SparseBooleanArray checked = memberList.getCheckedItemPositions();
                                String text = "";

                                memberIds.clear();
                                for (int index = 0; index < checked.size(); index++)
                                    if (checked.valueAt(index)) {
                                        if (!text.isEmpty())
                                            text += ", ";

                                        Member member = (Member) memberList.getItemAtPosition(checked.keyAt(index));
                                        text += member.getName() + " " + member.getSurname();
                                        memberIds.add(member.getId());
                                    }
                                memberLayout.getEditText().setText(text);
                                memberLayout.getEditText().setSelection(0);
                            }
                        });
                builder.show();
                break;
            case R.id.create_new_payment_product_list:
                TextInputLayout memberLayout = (TextInputLayout) findViewById(R.id.create_new_payment_member);
                int groups = Utils.getGroupsFromMemberString(memberLayout.getEditText().getText().toString(), memberIds);

                final CreatePaymentProductsAdapter productsAdapter = new CreatePaymentProductsAdapter(this,
                        R.layout.layout_payment_new_list_item,
                        ProductManager.getInstance().getProducts(groups));

                final ListView productList = new ListView(this);
                productList.setAdapter(productsAdapter);
                productList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                builder.setMessage(R.string.product_details_title)
                        .setView(productList)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextInputLayout productLayout = (TextInputLayout) findViewById(R.id.create_new_payment_product);
                                Product product = (Product) productList.getItemAtPosition(productList.getCheckedItemPosition());
                                if (product == null) {
                                    dialogInterface.dismiss();
                                    return;
                                }
                                productId = product.getId();

                                productLayout.getEditText().setText(product.getName());
                                productLayout.getEditText().setSelection(0);
                            }
                        });
                builder.show();
                break;
            case R.id.create_new_payment_date:
                final View datePicker = View.inflate(this, R.layout.date_picker, null);
                final MaterialCalendarView calendarView = (MaterialCalendarView) datePicker.findViewById(R.id.calendar_view);
                if (validUntil != null)
                    calendarView.setSelectedDate(validUntil);

                builder.setMessage("Choose a date")
                        .setView(datePicker)
                        .setNegativeButton(R.string.hide, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Button date = (Button) findViewById(R.id.create_new_payment_date);
                                if (calendarView.getSelectedDate() != null) {
                                    validUntil = calendarView.getSelectedDate().getDate();
                                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                                    date.setText(format.format(validUntil));
                                }
                            }
                        });
                builder.show();
                break;
            default:
                break;
        }
    }
}