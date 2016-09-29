package com.apps.jorge.monkeybisfire;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apps.jorge.monkeybisfire.adapter.FriendsListAdapter;
import com.apps.jorge.monkeybisfire.data.Tools;
import com.apps.jorge.monkeybisfire.fragment.ChatsFragment;
import com.apps.jorge.monkeybisfire.fragment.FragmentAdapter;
import com.apps.jorge.monkeybisfire.fragment.FriendsFragment;
import com.apps.jorge.monkeybisfire.fragment.GroupsFragment;
import com.apps.jorge.monkeybisfire.model.Friend;
import com.apps.jorge.monkeybisfire.util.Database;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityMain extends AppCompatActivity {


    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private DrawerLayout drawerLayout;
    public FloatingActionButton fab;
    private Toolbar searchToolbar;
    private ViewPager viewPager;

    private boolean isSearch = false;
    private FriendsFragment f_friends;
    private ChatsFragment f_chats;
    private GroupsFragment f_groups;
    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Database.getDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent_view = findViewById(R.id.main_content);
        setupDrawerLayout();
        initComponent();

        prepareActionBar(toolbar);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        initAction();

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

    private void initAction() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        Intent intent = new Intent(getApplicationContext(), ActivityFriendAdd.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent i = new Intent(getApplicationContext(), ActivitySelectFriend.class);
                        startActivity(i);
                        break;
                    case 2:
                        Snackbar.make(parent_view, "Add Group Clicked", Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                closeSearch();
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        fab.setImageResource(R.drawable.ic_add_friend);
                        break;
                    case 1:
                        fab.setImageResource(R.drawable.ic_create);
                        break;
                    case 2:
                        fab.setImageResource(R.drawable.ic_add_group);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.setCurrentItem(1);
    }

    private void initComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_viewpager);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        searchToolbar = (Toolbar) findViewById(R.id.toolbar_search);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
    }


    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        if (f_friends == null) {
            f_friends = new FriendsFragment();
        }
        if (f_chats == null) {
            f_chats = new ChatsFragment();
        }
        if (f_groups == null) {
            f_groups = new GroupsFragment();
        }

        adapter.addFragment(f_friends, getString(R.string.tab_friends));
        adapter.addFragment(f_chats, getString(R.string.tab_chats));
        adapter.addFragment(f_groups, getString(R.string.tab_groups));

        viewPager.setAdapter(adapter);
    }

    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        if (!isSearch) {
            settingDrawer();
        }
    }

    public void setVisibilityAppBar(boolean visible){
        CoordinatorLayout.LayoutParams layout_visible = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        CoordinatorLayout.LayoutParams layout_invisible = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        if(visible){
            appBarLayout.setLayoutParams(layout_visible);
            fab.show();
        }else{
            appBarLayout.setLayoutParams(layout_invisible);
            fab.hide();
        }
    }

    private void settingDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setupDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                Snackbar.make(parent_view, menuItem.getTitle()+" Clicked ", Snackbar.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isSearch) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(isSearch ? R.menu.menu_search_toolbar : R.menu.menu_main, menu);
        if (isSearch) {
            //Toast.makeText(getApplicationContext(), "Search " + isSearch, Toast.LENGTH_SHORT).show();
            final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
            search.setIconified(false);
            switch (viewPager.getCurrentItem()) {
                case 0:
                    search.setQueryHint("Search friends...");
                    break;
                case 1:
                    search.setQueryHint("Search chats...");
                    break;
                case 2:
                    search.setQueryHint("Search groups...");
                    break;
            }
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    switch (viewPager.getCurrentItem()) {
                        case 0:
                            f_friends.mAdapter.getFilter().filter(s);
                            break;
                        case 1:
                            f_chats.mAdapter.getFilter().filter(s);
                            break;
                        case 2:
                            f_groups.mAdapter.getFilter().filter(s);
                            break;
                    }
                    return true;
                }
            });
            search.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    closeSearch();
                    return true;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search: {
                isSearch = true;
                searchToolbar.setVisibility(View.VISIBLE);
                prepareActionBar(searchToolbar);
                supportInvalidateOptionsMenu();
                return true;
            }
            case android.R.id.home:
                closeSearch();
                return true;
            case R.id.action_notif: {
                Snackbar.make(parent_view, "Notifications Clicked", Snackbar.LENGTH_SHORT).show();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeSearch() {
        if (isSearch) {
            isSearch = false;
            if (viewPager.getCurrentItem() == 0) {
                //f_message.mAdapter.getFilter().filter("");
            } else {
                //f_contact.mAdapter.getFilter().filter("");
            }
            prepareActionBar(toolbar);
            searchToolbar.setVisibility(View.GONE);
            supportInvalidateOptionsMenu();
        }
    }


    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

}
