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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.PaymentManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.PaymentsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ProductsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.MemberListOnItemClickListener;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.PaymentListOnItemClickListener;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.ProductListOnItemClickListener;

public class EntityTabManager {
    private int memberListId;
    private int productListId;
    private int paymentListId;

    private static final EntityTabManager ourInstance = new EntityTabManager();

    public static EntityTabManager getInstance() {
        return ourInstance;
    }

    public String getTabTitle(Context context, int position) {
        Resources resources = context.getResources();
        switch (position) {
            case Constant.MEMBER_LIST_INDEX:
                return resources.getString(R.string.members_title);
            case Constant.PAYMENT_LIST_INDEX:
                return resources.getString(R.string.payments_title);
            case Constant.PRODUCT_LIST_INDEX:
                return resources.getString(R.string.products_title);
            default:
                return resources.getString(R.string.unknown);
        }
    }

    public Object createInstance(final FragmentActivity context, ViewGroup container, int position) {
        ListView list;
        switch (position) {
            case Constant.MEMBER_LIST_INDEX: {
                list = prepareTabObject(context, container, new MembersAdapter(context, MemberManager.getInstance().getMembers()));
                list.setOnItemClickListener(new MemberListOnItemClickListener(list, context.getFragmentManager()));

                final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) list.getParent();
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        MemberManager.getInstance().load(context);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                memberListId = View.generateViewId();
                list.setId(memberListId);
                break;
            } case Constant.PRODUCT_LIST_INDEX: {
                list = prepareTabObject(context, container, new ProductsAdapter(context, ProductManager.getInstance().getProducts()));
                list.setOnItemClickListener(new ProductListOnItemClickListener(list, context.getFragmentManager()));

                final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) list.getParent();
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        ProductManager.getInstance().load(context);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                productListId = View.generateViewId();
                list.setId(productListId);
                break;
            } case Constant.PAYMENT_LIST_INDEX: {
                list = prepareTabObject(context, container, new PaymentsAdapter(context, PaymentManager.getInstance().getPayments()));
                list.setOnItemClickListener(new PaymentListOnItemClickListener(list, context.getFragmentManager()));

                final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) list.getParent();
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        PaymentManager.getInstance().load(context);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                paymentListId = View.generateViewId();
                list.setId(paymentListId);
                break;
            } default:
                return null;
        }

        return list.getParent();
    }

    private ListView prepareTabObject(FragmentActivity context, ViewGroup container, ListAdapter adapter) {
        ListView listView;

        View view = context.getLayoutInflater().inflate(R.layout.layout_tab_list, container, false);
        listView = (ListView) view.findViewById(R.id.tab_list);

        listView.setAdapter(adapter);
        context.registerForContextMenu(listView);

        container.addView(view);
        return listView;
    }

    public void destroyList(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        switch (position) {
            case Constant.MEMBER_LIST_INDEX:
                memberListId = -1;
                break;
            case Constant.PAYMENT_LIST_INDEX:
                paymentListId = -1;
                break;
            case Constant.PRODUCT_LIST_INDEX:
                productListId = -1;
                break;
        }
    }

    public ListView getMemberList(Activity activity) {
        return (ListView) activity.findViewById(memberListId);
    }

    public ListView getPaymentList(Activity activity) {
        return (ListView) activity.findViewById(paymentListId);
    }

    public ListView getProductList(Activity activity) {
        return (ListView) activity.findViewById(productListId);
    }
}
