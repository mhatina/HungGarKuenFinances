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
import android.view.WindowManager;
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
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.EntitySelectionView;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.view.TextInputLayout;

public class CreateNewPaymentActivity extends CreateNewEntityActivity implements View.OnClickListener {

    private Date validUntil;
    private final ArrayList<Long> memberIds = new ArrayList<>();
    private long productId = -1;

    public CreateNewPaymentActivity() {
        super(R.layout.layout_payment_new);
    }

    @Override
    public void init() {
        TextInputLayout discountLayout = (TextInputLayout) findViewById(R.id.create_new_payment_discount);
        setEditTextContent(discountLayout, String.valueOf(0 + "%"));

        setTitle(getString(R.string.new_payment_title));

        Member member;
        if (getIntent().hasExtra(Constant.PREFILLED_ENTITY)) {
            TextInputLayout membersLayout = (TextInputLayout) findViewById(R.id.create_new_payment_member);

            member = MemberManager.getInstance().findMember(getIntent().getLongExtra(Constant.PREFILLED_ENTITY, 0));
            memberIds.add(member.getId());
            setEditTextContent(membersLayout, member.getName() + " " + member.getSurname());
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
            members = getResources().getString(R.string.deleted);

        setTitle(getString(R.string.edit_payment_title));

        setEditTextContent(membersLayout, members);
        setEditTextContent(productLayout, product == null ? getResources().getString(R.string.deleted) : product.getName());

        float productPrice = product != null ? product.getPrice() : 1;
        setEditTextContent(priceLayout, String.valueOf(productPrice));

        float discount = (1 - (payment.getPrice() / productPrice)) * 100;
        setEditTextContent(discountLayout, String.valueOf(discount + "%"));

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        dateButton.setText(format.format(payment.getValidUntil()));

        setEditTextContent(paidLayout, String.valueOf(payment.getPaid()));
        setEditTextContent(ownsLayout, String.valueOf(payment.getPrice() - payment.getPaid()));
        setEditTextContent(noteLayout, payment.getNote());
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

        getEditText(membersLayout).addTextChangedListener(
                new CreatePaymentOnMemberTextChangedListener(membersLayout));
        getEditText(productLayout).addTextChangedListener(
                new CreatePaymentOnProductTextChangedListener(
                        productLayout,
                        priceLayout.getEditText(),
                        dateButton,
                        priceListener,
                        discountListener));
        getEditText(discountLayout).addTextChangedListener(discountListener);
        getEditText(priceLayout).addTextChangedListener(priceListener);
        getEditText(payedLayout).addTextChangedListener(
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

        List<Long> memberIds = Utils.getMemberIdsFromString(getEditTextContent(membersLayout), this.memberIds);
        if (memberIds.isEmpty()) {
            membersLayout.setError(getString(R.string.required_error));
            return false;
        }

        Product product = ProductManager.getInstance().findProduct(productId);
        String productStr = getEditTextContent(productLayout);
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
        String note = getEditTextContent(noteLayout);
        Date valid = null;

        if (price == -1 || paid == -1 || discount == -1)
            return false;

        Payment payment;
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
        String str = getEditTextContent(layout);

        layout.setError(null);

        // in case of discount, which contains '%'
        str = str.replace("%", "");
        str = str.replace(",", ".");
        if (str.isEmpty()) {
            layout.setError(getString(R.string.required_error));
            return -1;
        }

        try {
            f = Float.valueOf(str);
        } catch (NumberFormatException ex) {
            layout.setError(getString(R.string.incorrect_format));
            return -1;
        }
        return f;
    }

    @Override
    public void onClick(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EntitySelectionView selection = new EntitySelectionView(builder.getContext());

        switch (view.getId()) {
            case R.id.create_new_payment_member_list: {
                List<Member> members = MemberManager.getInstance().getMembers();
                Collections.sort(members, new MemberAlphabeticComparator());
                CreatePaymentMembersAdapter adapter = new CreatePaymentMembersAdapter(this, members);

                selection.setAdapter(adapter);

                builder.setMessage(R.string.member_details_title).setView(selection.getView());

                final AlertDialog dialog = builder.create();
                if (dialog.getWindow() != null)
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                selection.setOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextInputLayout memberLayout = (TextInputLayout) findViewById(R.id.create_new_payment_member);
                        SparseBooleanArray checked = selection.getListView().getCheckedItemPositions();
                        String text = "";

                        memberIds.clear();
                        for (int index = 0; index < checked.size(); index++)
                            if (checked.valueAt(index)) {
                                if (!text.isEmpty())
                                    text += ", ";

                                Member member = (Member) selection.getListView().getItemAtPosition(checked.keyAt(index));
                                text += member.getName() + " " + member.getSurname();
                                memberIds.add(member.getId());
                            }
                        setEditTextContent(memberLayout, text);
                        getEditText(memberLayout).setSelection(0);
                        dialog.dismiss();
                    }
                });
                selection.setOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.create_new_payment_product_list: {
                TextInputLayout memberLayout = (TextInputLayout) findViewById(R.id.create_new_payment_member);
                int groups = Utils.getGroupsFromMemberString(getEditTextContent(memberLayout), memberIds);

                final CreatePaymentProductsAdapter adapter = new CreatePaymentProductsAdapter(this,
                        ProductManager.getInstance().getProducts(groups));

                selection.setAdapter(adapter);
                selection.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                builder.setMessage(R.string.member_details_title).setView(selection.getView());

                final AlertDialog dialog = builder.create();
                if (dialog.getWindow() != null)
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                builder.setMessage(R.string.product_details_title).setView(selection.getView());

                selection.setOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                selection.setOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextInputLayout productLayout = (TextInputLayout) findViewById(R.id.create_new_payment_product);
                        Product product = (Product) selection.getListView()
                                .getItemAtPosition(selection.getListView().getCheckedItemPosition());
                        if (product == null) {
                            dialog.dismiss();
                            return;
                        }
                        productId = product.getId();

                        setEditTextContent(productLayout, product.getName());
                        getEditText(productLayout).setSelection(0);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            }
            case R.id.create_new_payment_date:
                final View datePicker = View.inflate(this, R.layout.date_picker, null);
                final MaterialCalendarView calendarView = (MaterialCalendarView) datePicker.findViewById(R.id.calendar_view);
                if (validUntil != null)
                    calendarView.setSelectedDate(validUntil);

                builder.setMessage(R.string.choose_date)
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
