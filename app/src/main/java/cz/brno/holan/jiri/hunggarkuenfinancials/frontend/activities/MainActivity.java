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

package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.security.InvalidParameterException;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.Log;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.FileUtils;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.products.Product;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.PaymentManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.PaymentsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.ProductsAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.MemberDetailDialog;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.EntityTabsFragment;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers.EntityTabManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.provider.SearchProvider;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SIGNED_IN_AS = "signed_in_as";
    public static final int MEMBER_CONTEXT_GROUP_ID = 0;

    public static boolean calledFirebasePersistence = false;

    private BaseEntity mContextEntity;

    public MainActivity() {
        mContextEntity = null;
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> activity = getCreateNewActivityClass();
                SearchView searchView = (SearchView) findViewById(R.id.search);

                if (activity != null) {
                    searchView.setIconified(true);
                    startActivity(new Intent(v.getContext(), activity));
                } else {
                    Log.warning(getBaseContext(),
                            new InvalidParameterException("Cannot open activity for creation of new entity."));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!calledFirebasePersistence) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledFirebasePersistence = true;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            EntityTabsFragment fragment = new EntityTabsFragment();
            transaction.replace(R.id.slide_tabs_fragment, fragment);
            transaction.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initFloatingActionButton();
        initDrawer(toolbar);
        initNavigationView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!LoginActivity.isSignedIn) {
            LoginActivity.autoFinish = true;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, Constant.SIGN_IN_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        switch (requestCode) {
            case Constant.FILE_SELECT_CODE:
                if (resultCode != RESULT_OK) {
                    break;
                }
                Uri uri = data.getData();
                try {
                    if (viewPager.getCurrentItem() == Constant.MEMBER_LIST_INDEX) {
                        MemberManager.getInstance().importFromFile(this, uri);
                    } else if (viewPager.getCurrentItem() == Constant.PAYMENT_LIST_INDEX) {
                        PaymentManager.getInstance().importFromFile(this, uri);
                    } else if (viewPager.getCurrentItem() == Constant.PRODUCT_LIST_INDEX) {
                        ProductManager.getInstance().importFromFile(this, uri);
                    }
                } catch (IOException e) {
                    throw new NullPointerException("Cannot open file: " + uri.getPath());
                }
            case Constant.NEW_ENTITY_CODE:
                ListView memberList = getMemberListView();
                if (memberList != null)
                    memberList.setAdapter(new MembersAdapter(this, R.layout.layout_member, MemberManager.getInstance().getMembers()));
                else {
                    // todo create own exception
                    Log.warning(getBaseContext(), new Exception("Cannot refresh member list."));
                }
                break;
            case Constant.SIGN_IN_CODE:
                setSignedInAs(data);
                MemberManager.getInstance().load();
                ProductManager.getInstance().load();
                break;
            default:

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setSignedInAs(Intent data) {
        if (data == null)
            return;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        TextView textView = (TextView) view.findViewById(R.id.drawer_sign_in_info);
        textView.setText(data.getStringExtra(SIGNED_IN_AS));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            LoginActivity.autoFinish = false;
            startActivityForResult(new Intent(this, LoginActivity.class), Constant.SIGN_IN_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnCloseListener(new OnSearchCloseListener());
        searchView.setOnQueryTextListener(new OnSearchTextListener());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_import:
                FileUtils.showFileChooser(this, MemberManager.getInstance().importDescription());
                return true;
            case R.id.action_export:
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stats_members) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (SearchProvider.ON_SEARCH_SUGGESTION_CLICK.equals(intent.getAction())) {
            Uri detailUri = intent.getData();
            String suggestion = detailUri.getLastPathSegment();

            SearchView searchView = (SearchView) findViewById(R.id.search);
            String query = searchView.getQuery().toString();

            if (query.lastIndexOf(' ') == -1)
                query = suggestion;
            else {
                query = query.subSequence(0, query.lastIndexOf(' ') + 1) + suggestion;
            }

            searchView.setQuery(query + " ", false);

            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager.getCurrentItem() == Constant.MEMBER_LIST_INDEX) {
            Member member;
            mContextEntity = member = (Member) getMemberListView().getItemAtPosition(adapterMenuInfo.position);
            menu.setHeaderTitle(member.getName() + " " + member.getSurname());
        } else if (viewPager.getCurrentItem() == Constant.PAYMENT_LIST_INDEX) {
            Payment payment;
            mContextEntity = payment = (Payment) getPaymentListView().getItemAtPosition(adapterMenuInfo.position);
//            menu.setHeaderTitle();
        } else if (viewPager.getCurrentItem() == Constant.PRODUCT_LIST_INDEX) {
            Product product;
            mContextEntity = product = (Product) getProductListView().getItemAtPosition(adapterMenuInfo.position);
            menu.setHeaderTitle(product.getName());
        }

        menu.removeGroup(MemberDetailDialog.MEMBER_DETAIL_CONTEXT_GROUP_ID);
        menu.add(0, v.getId(), 0, R.string.edit);
        menu.add(0, v.getId(), 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.edit))) {
            Intent intent = new Intent(this, getCreateNewActivityClass());
            intent.putExtra(Constant.EDIT_ENTITY, mContextEntity.getId());
            startActivityForResult(intent, 1);
        } else if (item.getTitle().equals(getString(R.string.delete))) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

            if (viewPager.getCurrentItem() == Constant.MEMBER_LIST_INDEX) {
                showDeleteDialog(R.string.delete_member_title, R.string.sure_to_delete_member);
            } else if (viewPager.getCurrentItem() == Constant.PAYMENT_LIST_INDEX) {
                showDeleteDialog(R.string.delete_payment_title, R.string.sure_to_delete_payment);
            } else if (viewPager.getCurrentItem() == Constant.PRODUCT_LIST_INDEX) {
                showDeleteDialog(R.string.delete_product_title, R.string.sure_to_delete_product);
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    private Class<?> getCreateNewActivityClass() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager.getCurrentItem() == Constant.MEMBER_LIST_INDEX) {
            return CreateNewMemberActivity.class;
        } else if (viewPager.getCurrentItem() == Constant.PAYMENT_LIST_INDEX) {
            return null;
        } else if (viewPager.getCurrentItem() == Constant.PRODUCT_LIST_INDEX) {
            return CreateNewProductActivity.class;
        }

        return null;
    }

    private void showDeleteDialog(int titleResource, int messageResource) {
        new AlertDialog.Builder(this)
                .setTitle(titleResource)
                .setMessage(messageResource)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mContextEntity == null) {
                            return;
                        }

                        if (mContextEntity instanceof Member) {
                            MemberManager.getInstance().deleteMember((Member) mContextEntity);
                            ((MembersAdapter) getMemberListView().getAdapter()).notifyDataSetChanged();
                            mContextEntity = null;
                        } else if (mContextEntity instanceof Product) {
                            ProductManager.getInstance().deleteProduct((Product) mContextEntity);
                            ((ProductsAdapter) getProductListView().getAdapter()).notifyDataSetChanged();
                            mContextEntity = null;
                        } else if (mContextEntity instanceof Payment) {
                            PaymentManager.getInstance().deletePayment((Payment) mContextEntity);
                            ((PaymentsAdapter) getPaymentListView().getAdapter()).notifyDataSetChanged();
                            mContextEntity = null;
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public ListView getMemberListView() {
        return EntityTabManager.getInstance().getMemberList();
    }

    public ListView getPaymentListView() {
        return EntityTabManager.getInstance().getPaymentList();
    }

    public ListView getProductListView() {
        return EntityTabManager.getInstance().getProductList();
    }

    private void filterMemberList(String query) {
        String[] split = query.split(" ");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ListView listView = getMemberListView();
        if (listView != null)
            listView.setAdapter(
                    new MembersAdapter(
                            viewPager.getContext(),
                            R.layout.layout_member,
                            MemberManager.getInstance().getMembers(split[0], split.length > 1 ? split[1] : null)));
    }

    private class OnSearchTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            filterMemberList(newText);
            return true;
        }
    }

    private class OnSearchCloseListener implements SearchView.OnCloseListener {

        @Override
        public boolean onClose() {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            getMemberListView().setAdapter(
                    new MembersAdapter(
                            viewPager.getContext(),
                            R.layout.layout_member,
                            MemberManager.getInstance().getMembers()));

            SearchView searchView = (SearchView) findViewById(R.id.search);
            searchView.setSubmitButtonEnabled(false);

            return false;
        }
    }
}
