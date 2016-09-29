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
import android.widget.Toast;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.managers.MemberManager;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters.MembersAdapter;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.SlidingTabsFragment;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.provider.SearchProvider;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , View.OnClickListener{

    public static final String SIGNED_IN_AS = "signed_in_as";

    private String TAG = "MainActivity";

    private MemberManager mMembersManager;
    private Member mContextMenuMember;

    public MainActivity() {
        mMembersManager = MemberManager.getInstance();
        mContextMenuMember = null;
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
                startActivity(new Intent(v.getContext(), CreateNewMemberActivity.class));
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

        getMemberListView().setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO login
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.putExtra(LoginActivity.AUTO_FINISH, true);
//        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ListView memberList;
        if (data != null && data.hasExtra(SIGNED_IN_AS))
            setSignedInAs(data);
        else if ((memberList = getMemberListView()) != null)
            memberList.setAdapter(new MembersAdapter(this, R.layout.layout_member, MemberManager.getInstance().getMembers()));
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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        mContextMenuMember = (Member) memberList.getItemAtPosition(adapterMenuInfo.position);
        menu.setHeaderTitle(mContextMenuMember.getFirstName() + " " + mContextMenuMember.getSurname());

        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Edit")) {
            Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CreateNewMemberActivity.class);
            intent.putExtra(CreateNewMemberActivity.CREATE_EDIT_ENTITY, mContextMenuMember.getId());
            startActivityForResult(intent, 1);
        } else if (item.getTitle().equals("Delete")) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete member")
                    .setMessage("Are you sure you want to delete member?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ArrayAdapter<Member> arrayAdapter = (ArrayAdapter<Member>) getMemberListView().getAdapter();
                            arrayAdapter.remove(mContextMenuMember);
                            mContextMenuMember = null;
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

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    private ListView getMemberListView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        View view = viewPager.getChildAt(0);
        if (view instanceof ListView)
            return (ListView) view;

        return null;
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.member_item_list_row) {

        }
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
