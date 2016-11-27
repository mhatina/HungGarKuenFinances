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

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ProductsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners.MemberListOnItemClickListener;

public class EntityTabManager {
    private ListView mMemberList = null;
    private ListView mPaymentList = null;
    private ListView mProductList = null;

    private static EntityTabManager ourInstance = new EntityTabManager();

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
                return resources.getString(R.string.unknown_title);
        }
    }

    public Object createInstance(FragmentActivity context, ViewGroup container, int position) {
        final SwipeRefreshLayout swipeRefreshLayout;
        if (mMemberList == null) {
            mMemberList = prepareTabObject(context, container, new MembersAdapter(context, R.layout.layout_member, MemberManager.getInstance().getMembers()));
            mMemberList.setOnItemClickListener(new MemberListOnItemClickListener(mMemberList, context.getFragmentManager()));

            swipeRefreshLayout = (SwipeRefreshLayout) mMemberList.getParent();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    MemberManager.getInstance().load();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        if (mProductList == null) {
            mProductList = prepareTabObject(context, container, new ProductsAdapter(context, R.layout.layout_product, ProductManager.getInstance().getProducts()));
            // TODO setOnItemClickListener
        }

        switch (position) {
            case Constant.MEMBER_LIST_INDEX:
                return mMemberList.getParent();
            case Constant.PAYMENT_LIST_INDEX:
                return null;
            case Constant.PRODUCT_LIST_INDEX:
                return mProductList.getParent();
            default:
                View view = context.getLayoutInflater().inflate(R.layout.pager_item,
                        container, false);

                container.addView(view);

                TextView title = (TextView) view.findViewById(R.id.item_title);
                title.setText(String.valueOf(position + 1));

                return view;
        }
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
                mMemberList = null;
            case Constant.PAYMENT_LIST_INDEX:
            case Constant.PRODUCT_LIST_INDEX:
                mProductList = null;
        }
    }

    public ListView getMemberList() {
        return mMemberList;
    }

    public ListView getPaymentList() {
        return mPaymentList;
    }

    public ListView getProductList() {
        return mProductList;
    }
}
