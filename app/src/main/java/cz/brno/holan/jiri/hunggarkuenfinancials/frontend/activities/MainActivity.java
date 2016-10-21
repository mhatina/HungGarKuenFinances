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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.URISyntaxException;

import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.FileUtils;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.BaseEntity;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.ProductManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.MemberDetailDialog;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.SlidingTabsFragment;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers.SlidingTabManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.provider.SearchProvider;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SIGNED_IN_AS = "signed_in_as";
    public static final int MEMBER_CONTEXT_GROUP_ID = 0;

    private String TAG = "MainActivity";

    private BaseEntity mContextEntity;

    public MainActivity() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> activity = null;
                ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
                SearchView searchView = (SearchView) findViewById(R.id.search);
                if (viewPager.getCurrentItem() == SlidingTabManager.MEMBER_LIST_INDEX) {
                    activity = CreateNewMemberActivity.class;
                } else if (viewPager.getCurrentItem() == SlidingTabManager.PAYMENT_LIST_INDEX) {

                } else if (viewPager.getCurrentItem() == SlidingTabManager.PRODUCT_LIST_INDEX) {

                }

                if (activity != null) {
                    searchView.setIconified(true);
                    startActivity(new Intent(v.getContext(), activity));
                } else {
                    // TODO report problem
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsFragment fragment = new SlidingTabsFragment();
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

        MemberManager.getInstance(this);
        ProductManager.getInstance();

        // TODO login
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.putExtra(LoginActivity.AUTO_FINISH, true);
//        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        switch (requestCode) {
            case Constant.FILE_SELECT_CODE:
                if (resultCode != RESULT_OK) {
                    break;
                }
                try {
                    Uri uri = data.getData();
                    if (viewPager.getCurrentItem() == SlidingTabManager.MEMBER_LIST_INDEX) {
                        MemberManager.getInstance().importFromFile(this, uri);
                    } else if (viewPager.getCurrentItem() == SlidingTabManager.PAYMENT_LIST_INDEX) {
//                    PaymentManager.getInstance().importFromFile(this, path);
                    } else if (viewPager.getCurrentItem() == SlidingTabManager.PRODUCT_LIST_INDEX) {
                        ProductManager.getInstance().importFromFile(this, uri);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // todo report error
                }

                break;
            case Constant.SIGN_IN_CODE:
                setSignedInAs(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

        ListView memberList = getMemberListView();
        if (memberList != null)
            memberList.setAdapter(new MembersAdapter(this, R.layout.layout_member, MemberManager.getInstance().getMembers()));
        else {
            // todo report error
        }
    }

    private void setSignedInAs(Intent data) {
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
            // TODO login
//            startActivityForResult(new Intent(this, LoginActivity.class), 1);
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
    public boolean onNavigationItemSelected(MenuItem item) {
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
        ListView memberList = getMemberListView();

        if (memberList == null) {
            return;
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager.getCurrentItem() == SlidingTabManager.MEMBER_LIST_INDEX) {
            Member member;
            mContextEntity = member = (Member) memberList.getItemAtPosition(adapterMenuInfo.position);
            menu.setHeaderTitle(member.getName() + " " + member.getSurname());
        } else if (viewPager.getCurrentItem() == SlidingTabManager.PAYMENT_LIST_INDEX) {

        } else if (viewPager.getCurrentItem() == SlidingTabManager.PRODUCT_LIST_INDEX) {

        }

        menu.removeGroup(MemberDetailDialog.MEMBER_DETAIL_CONTEXT_GROUP_ID);
        menu.add(0, v.getId(), 0, R.string.edit);
        menu.add(0, v.getId(), 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.edit))) {
            Intent intent = new Intent(this, CreateNewMemberActivity.class);
            intent.putExtra(CreateNewMemberActivity.EDIT_ENTITY, mContextEntity.getId());
            startActivityForResult(intent, 1);
        } else if (item.getTitle().equals(getString(R.string.delete))) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_member_title)
                    .setMessage(R.string.sure_to_delete_member)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (mContextEntity instanceof Member) {
                                MemberManager.getInstance().deleteMember((Member) mContextEntity);
                                ArrayAdapter<Member> arrayAdapter = (ArrayAdapter<Member>) getMemberListView().getAdapter();
                                arrayAdapter.remove((Member) mContextEntity);
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
        } else {
            return false;
        }

        return true;
    }

    public ListView getMemberListView() {
        return SlidingTabManager.createInstance().getMemberList();
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
