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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.brno.holan.jiri.hunggarkuenfinancials.BuildConfig;
import cz.brno.holan.jiri.hunggarkuenfinancials.Constant;
import cz.brno.holan.jiri.hunggarkuenfinancials.Log;
import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.FileUtils;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.UpdateChecker;
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
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.EntityTabsFragment;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.managers.EntityTabManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static boolean calledFirebasePersistence = false;

    private UpdateChecker updateChecker;
    private ProgressDialog mProgressDialog;
    private BaseEntity mContextEntity;
    private boolean init = false;

    public MainActivity() {
        mContextEntity = null;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
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
                    startActivityForResult(new Intent(v.getContext(), activity), Constant.NEW_ENTITY_CODE);
                } else {
                    Log.warning(getBaseContext(),
                            new InvalidParameterException(getString(R.string.activity_error)));
                }
            }
        });
    }

    private void initFirebaseConfig() {
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);

        Map<String, Object> remoteConfigDefaults = new HashMap<>();
        remoteConfigDefaults.put(Constant.KEY_FIREBASE_CONFIG_FORCE_UPDATE, false);
        remoteConfigDefaults.put(Constant.KEY_FIREBASE_CONFIG_CURRENT_VERSION, "1");
        remoteConfigDefaults.put(Constant.KEY_FIREBASE_CONFIG_UPDATE_URL,
                String.format(Constant.FIREBASE_CONFIG_UPDATE_URL, BuildConfig.VERSION_NAME, BuildConfig.VERSION_NAME));

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseRemoteConfig.activateFetched();
                    updateChecker.check();
                }
            }
        });
    }

    public void moveFloatingButton() {
        boolean empty = false;
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (viewPager.getCurrentItem() == Constant.MEMBER_LIST_INDEX) {
            empty = MemberManager.getInstance().isShownMembersEmpty();
        } else if (viewPager.getCurrentItem() == Constant.PAYMENT_LIST_INDEX) {
            empty = PaymentManager.getInstance().isShownPaymentsEmpty();
        } else if (viewPager.getCurrentItem() == Constant.PRODUCT_LIST_INDEX) {
            empty = ProductManager.getInstance().isShownMembersEmpty();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x / 2;
        int height = size.y / 2;
        int margin = (int) getResources().getDimension(R.dimen.fab_margin);

        if (empty && fab.getHeight() != 0) {
            fab.animate()
                    .translationY(-height + margin + fab.getHeight())
                    .translationX(-width + margin + fab.getWidth() / 2)
                    .start();
        } else {
            fab.animate()
                    .translationY(0)
                    .translationX(0)
                    .start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!calledFirebasePersistence) {
            updateChecker = new UpdateChecker(MainActivity.this);
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
        initFirebaseConfig();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!LoginActivity.isSignedIn) {
            LoginActivity.autoFinish = true;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, Constant.SIGN_IN_CODE);
        }

        if (!init) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    moveFloatingButton();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            init = true;
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
                        showProgressDialog();
                        MemberManager.getInstance().importFromFile(this, uri);
                        hideProgressDialog();
                    }
                } catch (IOException e) {
                    throw new NullPointerException(getResources().getString(R.string.cannot_open_file, uri.getPath()));
                }
            case Constant.EDIT_ENTITY_CODE:
            case Constant.NEW_ENTITY_CODE:
                if (viewPager.getCurrentItem() == Constant.MEMBER_LIST_INDEX) {
                    refreshMembers();
                } else if (viewPager.getCurrentItem() == Constant.PAYMENT_LIST_INDEX) {
                    refreshMembers();
                    refreshPayments();
                } else if (viewPager.getCurrentItem() == Constant.PRODUCT_LIST_INDEX) {
                    refreshProducts();
                } else {
                    // todo create own exception
                    Log.warning(getBaseContext(), new Exception(getString(R.string.refresh_list_error)));
                }
                break;
            case Constant.SIGN_IN_CODE:
                setSignedInAs(data);
                startEntityLoading();

                initGroupFilters();
                break;
            default:

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initGroupFilters() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("AdultFilter", false);
        editor.putBoolean("YoungsterFilter", false);
        editor.putBoolean("JuniorFilter", false);
        editor.putBoolean("ChildFilter", false);
        editor.putBoolean("BeginnerFilter", false);
        editor.apply();
    }

    public void endEntityLoading() {
        hideProgressDialog();
        moveFloatingButton();
    }

    private void startEntityLoading() {
        showProgressDialog();
        MemberManager.getInstance().load(this);
        ProductManager.getInstance().load(this);
        PaymentManager.getInstance().load(this);
    }

    private void setSignedInAs(Intent data) {
        if (data == null)
            return;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        TextView textView = (TextView) view.findViewById(R.id.drawer_sign_in_info);
        textView.setText(data.getStringExtra(Constant.SIGNED_IN_AS));
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
        if (searchView != null) {
            searchView.setOnCloseListener(new OnSearchCloseListener());
            searchView.setOnQueryTextListener(new OnSearchTextListener());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_import:
                FileUtils.showFileChooser(this, getResources().getString(MemberManager.getInstance().importDescription()));
                return true;
            case R.id.action_export:
                return true;
            case R.id.action_about:
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(getString(R.string.version, BuildConfig.VERSION_NAME) + "\n" +
                                getString(R.string.author, Constant.AUTHOR))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            case R.id.action_filter:
                View menuItem = findViewById(R.id.action_filter);
                PopupMenu popup = new PopupMenu(this, menuItem);
                MenuInflater inflater = popup.getMenuInflater();

                inflater.inflate(R.menu.filter, popup.getMenu());
                setForceShowIcon(popup);

                final MenuItem adult = popup.getMenu().findItem(R.id.type_adult);
                final MenuItem youngster = popup.getMenu().findItem(R.id.type_youngster);
                final MenuItem junior = popup.getMenu().findItem(R.id.type_junior);
                final MenuItem child = popup.getMenu().findItem(R.id.type_child);
                final MenuItem beginner = popup.getMenu().findItem(R.id.type_beginner);

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

                adult.setChecked(sharedPref.getBoolean("AdultFilter", false));
                youngster.setChecked(sharedPref.getBoolean("YoungsterFilter", false));
                junior.setChecked(sharedPref.getBoolean("JuniorFilter", false));
                child.setChecked(sharedPref.getBoolean("ChildFilter", false));
                beginner.setChecked(sharedPref.getBoolean("BeginnerFilter", false));


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        item.setChecked(!item.isChecked());

                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                        item.setActionView(new View(getBaseContext()));
                        return false;
                    }
                });
                popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("AdultFilter", adult.isChecked());
                        editor.putBoolean("YoungsterFilter", youngster.isChecked());
                        editor.putBoolean("JuniorFilter", junior.isChecked());
                        editor.putBoolean("ChildFilter", child.isChecked());
                        editor.putBoolean("BeginnerFilter", beginner.isChecked());
                        editor.apply();

                        filterEntities(null);
                    }
                });

                popup.show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        Field[] fields = popupMenu.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ("mPopup".equals(field.getName())) {
                field.setAccessible(true);
                try {
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                    return;
                }
                break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
            case R.id.nav_stats_members:
            case R.id.nav_stats_payments:
            case R.id.nav_stats_products:
                Log.info(this, R.string.not_implemented);
                break;
            case R.id.nav_bug_report:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.BUG_REPORT_URL));
                startActivity(browserIntent);
                break;
            case R.id.nav_bug_report_mail:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:" + Constant.BUG_REPORT_MAIL);
                intent.setData(data);
                intent.putExtra(Intent.EXTRA_SUBJECT, Constant.BUG_REPORT_MAIL_SUBJECT);
                intent.putExtra(Intent.EXTRA_TEXT, String.format(Constant.BUG_REPORT_MAIL_BODY, BuildConfig.VERSION_NAME));
                startActivity(intent);
                break;
            case R.id.nav_update:
                if (updateChecker != null && !updateChecker.check())
                    Log.info(this, R.string.no_update);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ListView list;

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager.getCurrentItem() == Constant.MEMBER_LIST_INDEX) {
            Member member;
            list = getMemberListView();
            if (list != null) {
                mContextEntity = member = (Member) list.getItemAtPosition(adapterMenuInfo.position);
                menu.setHeaderTitle(member.getName() + " " + member.getSurname());
            }
        } else if (viewPager.getCurrentItem() == Constant.PAYMENT_LIST_INDEX) {
            Payment payment;
            list = getPaymentListView();
            if (list != null) {
                mContextEntity = payment = (Payment) list.getItemAtPosition(adapterMenuInfo.position);
                Product product = ProductManager.getInstance().findProduct(payment.getProductId());
                String header = product != null ? product.getName() : getResources().getString(R.string.deleted);
                menu.setHeaderTitle(header);
            }
        } else if (viewPager.getCurrentItem() == Constant.PRODUCT_LIST_INDEX) {
            Product product;
            list = getProductListView();
            if (list != null) {
                mContextEntity = product = (Product) list.getItemAtPosition(adapterMenuInfo.position);
                menu.setHeaderTitle(product.getName());
            }
        }

        menu.removeGroup(Constant.MEMBER_DETAIL_CONTEXT_GROUP_ID);
        menu.add(0, v.getId(), 0, R.string.edit);
        menu.add(0, v.getId(), 0, R.string.delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.edit))) {
            Intent intent = new Intent(this, getCreateNewActivityClass());
            intent.putExtra(Constant.EDIT_ENTITY, mContextEntity.getId());
            startActivityForResult(intent, Constant.EDIT_ENTITY_CODE);
        } else if (item.getTitle().equals(getString(R.string.delete))) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

            if (viewPager.getCurrentItem() == Constant.MEMBER_LIST_INDEX) {
                showDeleteDialog(R.string.delete_member_title, R.string.sure_to_delete_member);
            } else if (viewPager.getCurrentItem() == Constant.PAYMENT_LIST_INDEX) {
                showDeleteDialog(R.string.delete_payment_title, R.string.sure_to_delete_payment);
            } else if (viewPager.getCurrentItem() == Constant.PRODUCT_LIST_INDEX) {
                showDeleteDialog(R.string.delete_product_title, R.string.sure_to_delete_product);
            }
            moveFloatingButton();
        } else {
            return false;
        }

        return true;
    }

    private Class<?> getCreateNewActivityClass() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager.getCurrentItem() == Constant.MEMBER_LIST_INDEX) {
            return CreateNewMemberActivity.class;
        } else if (viewPager.getCurrentItem() == Constant.PAYMENT_LIST_INDEX) {
            return CreateNewPaymentActivity.class;
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
                        } else if (mContextEntity instanceof Product) {
                            ProductManager.getInstance().deleteProduct((Product) mContextEntity);
                        } else if (mContextEntity instanceof Payment) {
                            PaymentManager.getInstance().deletePayment((Payment) mContextEntity);
                        }

                        refreshEntities();
                        mContextEntity = null;
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private ListView getMemberListView() {
        return EntityTabManager.getInstance().getMemberList(this);
    }

    private ListView getPaymentListView() {
        return EntityTabManager.getInstance().getPaymentList(this);
    }

    private ListView getProductListView() {
        return EntityTabManager.getInstance().getProductList(this);
    }

    private void refreshEntities() {
        refreshIndividually(mContextEntity);
    }

    private void refreshMembers() {
        filterMemberList(null);
        moveFloatingButton();
    }

    private void refreshProducts() {
        filterProductList(null);
        moveFloatingButton();
    }

    private void refreshPayments() {
        filterPaymentList(null);
        moveFloatingButton();
    }

    private void refreshIndividually(BaseEntity entity) {
        if (entity instanceof Member)
            filterMemberList(null);
        else if (entity instanceof Product)
            filterProductList(null);
        else if (entity instanceof Payment)
            filterPaymentList(null);

        moveFloatingButton();
    }

    private void filterEntities(String newText) {
        applyAllGroupFilters();

        filterMemberList(newText);
        filterProductList(newText);
        filterPaymentList(newText);

        moveFloatingButton();
    }

    private void applyAllGroupFilters() {
        resetGroupFilters();

        applyGroupFilterOnManager("AdultFilter", Constant.ADULT_GROUP);
        applyGroupFilterOnManager("YoungsterFilter", Constant.YOUNGSTER_GROUP);
        applyGroupFilterOnManager("JuniorFilter", Constant.JUNIOR_GROUP);
        applyGroupFilterOnManager("ChildFilter", Constant.CHILD_GROUP);
        applyGroupFilterOnManager("BeginnerFilter", Constant.BEGINNER_GROUP);
    }

    private void resetGroupFilters() {
        MemberManager.getInstance().resetGroupFilter();
        ProductManager.getInstance().resetGroupFilter();
        PaymentManager.getInstance().resetGroupFilter();
    }

    private void applyGroupFilterOnManager(String preference, int groupCode) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.getBoolean(preference, false)) {
            MemberManager.getInstance().toggleGroupFilter(groupCode);
            ProductManager.getInstance().toggleGroupFilter(groupCode);
            PaymentManager.getInstance().toggleGroupFilter(groupCode);
        }
    }

    private void filterMemberList(String query) {
        List<Member> members = null;
        if (query != null) {
            String[] split = query.split(" ");
            members = MemberManager.getInstance().getMembers(split);
        }

        if (query == null || (members != null && members.isEmpty())) {
            members = MemberManager.getInstance().getMembers();
        }

        ListView listView = getMemberListView();
        if (listView != null)
            listView.setAdapter(
                    new MembersAdapter(
                            this,
                            members));
    }

    private void filterProductList(String query) {
        ListView listView = getProductListView();
        List<Product> products = ProductManager.getInstance().getProducts(query);
        if (products.isEmpty())
            products = ProductManager.getInstance().getProducts();
        if (listView != null)
            listView.setAdapter(
                    new ProductsAdapter(
                            this,
                            products));
    }

    private void filterPaymentList(String query) {
        ListView listView = getPaymentListView();
        List<Payment> payments;
        if (query == null)
            payments = PaymentManager.getInstance().getPayments();
        else
            payments = PaymentManager.getInstance().getPayments(query.split(" "));

        if (listView != null) {
            listView.setAdapter(
                    new PaymentsAdapter(
                            this,
                            payments
                    )
            );
        }
    }

    private class OnSearchTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            filterEntities(newText);
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
                            MemberManager.getInstance().getMembers()));

            SearchView searchView = (SearchView) findViewById(R.id.search);
            searchView.setSubmitButtonEnabled(false);

            return false;
        }
    }
}
