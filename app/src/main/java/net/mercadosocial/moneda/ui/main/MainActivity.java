package net.mercadosocial.moneda.ui.main;


import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.mercadosocial.moneda.App;
import net.mercadosocial.moneda.DebugHelper;
import net.mercadosocial.moneda.R;
import net.mercadosocial.moneda.api.response.Data;
import net.mercadosocial.moneda.base.BaseActivity;
import net.mercadosocial.moneda.base.BaseFragment;
import net.mercadosocial.moneda.model.FilterEntities;
import net.mercadosocial.moneda.model.MES;
import net.mercadosocial.moneda.model.MESData;
import net.mercadosocial.moneda.model.Notification;
import net.mercadosocial.moneda.ui.auth.login.LoginActivity;
import net.mercadosocial.moneda.ui.auth.register_web.RegisterWebActivity;
import net.mercadosocial.moneda.ui.entities.EntitiesFragment;
import net.mercadosocial.moneda.ui.entities.EntitiesPresenter;
import net.mercadosocial.moneda.ui.get_boniatos.GetBoniatosPresenter;
import net.mercadosocial.moneda.ui.info.WebViewActivity;
import net.mercadosocial.moneda.ui.intro.IntroActivity;
import net.mercadosocial.moneda.ui.invitations.InvitationsPresenter;
import net.mercadosocial.moneda.ui.novelties.list.NoveltiesFragment;
import net.mercadosocial.moneda.ui.profile.ProfileActivity;
import net.mercadosocial.moneda.ui.wallet.WalletFragment;
import net.mercadosocial.moneda.util.DateUtils;
import net.mercadosocial.moneda.views.CircleTransform;
import net.mercadosocial.moneda.views.DialogSelectMES;
import net.mercadosocial.moneda.views.custom_dialog.NewPaymentDialog;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, MainView {


    private static final int REQ_CODE_INTRO = 11;
    private static final int REQ_CODE_PROFILE = 22;

    private DrawerLayout drawerLayout;
    private TextView btnLogin;
    private TextView btnSignup;
    private BottomNavigationView bottomNavView;
    private TextView tvUserName;
    private View viewEnterButtons;
    private View btnLogout;
    private View viewUserInfo;
    private MainPresenter presenter;
    private ImageView imgAvatar;
    private TextView tvPendingPaymentsBadge;

    private ArrayList<BaseFragment> sections = new ArrayList<>();
    private EntitiesFragment entitiesFragment;
    private WalletFragment walletFragment;
    private NoveltiesFragment noveltiesFragment;
    private int currentSection = -1;
    private TextView btnGoToProfile;
    private NavigationView navigationView;
    private MenuItem menuItemInvitations;
    private TextView tvMES;
    private TextView tvGuestInfo;

    private void findViews() {


        bottomNavView = (BottomNavigationView) findViewById(R.id.navigation_bottom_view);
        bottomNavView.setOnNavigationItemSelectedListener(this);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnLogin = (TextView) navigationView.getHeaderView(0).findViewById(R.id.btn_login);
        btnSignup = (TextView) navigationView.getHeaderView(0).findViewById(R.id.btn_singup);
        tvUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_user_name);
        tvMES = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_mes);
        tvGuestInfo = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_guest_info);
        viewEnterButtons = navigationView.getHeaderView(0).findViewById(R.id.view_enter_buttons);
        viewUserInfo = navigationView.getHeaderView(0).findViewById(R.id.view_user_info);
        btnLogout = navigationView.getHeaderView(0).findViewById(R.id.btn_logout);
        imgAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        btnGoToProfile = (TextView) navigationView.getHeaderView(0).findViewById(R.id.btn_go_to_profile);

        menuItemInvitations = navigationView.getMenu().findItem(R.id.menuItem_invitations);

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnGoToProfile.setOnClickListener(this);
        viewUserInfo.setOnClickListener(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        presenter = MainPresenter.newInstance(this, this);
        setBasePresenter(presenter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        configureToolbar();
        configureDrawerLayout();
        configureToolbarBackArrowBehaviour();
        configureBottomNavView();
        configureFragments();

        updateMenuViewsByMES();


        if (DebugHelper.SHORTCUT_ACTIVITY != null && DebugHelper.SHORTCUT_ACTIVITY != MainActivity.class) {
            startActivity(new Intent(this, DebugHelper.SHORTCUT_ACTIVITY));
            return;
        }

//        if (App.getUserData(this) == null) {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//            return;
//        }

        if (!App.getPrefs(this).getBoolean(App.SHARED_INTRO_SEEN, false)) {
//        if(true) {
            startActivityForResult(new Intent(this, IntroActivity.class), REQ_CODE_INTRO);
            getPrefs().edit().putBoolean(App.SHARED_INTRO_SEEN, true).commit();
        } else {
            showFragment(0);
        }


        presenter.onCreate(getIntent());

//        showMockNotificationDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DebugHelper.SHORTCUT_ACTIVITY != null) {
            DebugHelper.SHORTCUT_ACTIVITY = null;
            return;
        }

        presenter.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
//        getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_INTRO:
                showFragment(0);
                break;

            case REQ_CODE_PROFILE:
                if (resultCode == RESULT_OK) {
                    presenter.onLogoutClick();
                }
                break;

        }
    }

    private void configureFragments() {

        entitiesFragment = new EntitiesFragment();
        walletFragment = new WalletFragment();
        noveltiesFragment = new NoveltiesFragment();

        sections.add(entitiesFragment);
        sections.add(walletFragment);
        sections.add(noveltiesFragment);
    }


    private void showMockNotificationDialog() {

        Notification notification = new Notification();
        notification.setAmount(10f);
        notification.setSender("Pepa");
        notification.setTotal_amount(20f);
//        BonusDialog bonusDialog = BonusDialog.newInstance(notification);
//        bonusDialog.show(getSupportFragmentManager(), null);

        NewPaymentDialog.newInstance(notification).show(getSupportFragmentManager(), null);
    }


    private void configureBottomNavView() {

        View v = bottomNavView.findViewById(R.id.navigation_wallet);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;

        View view = LayoutInflater.from(this)
                .inflate(R.layout.view_pending_payments_badge, bottomNavView, false);

        tvPendingPaymentsBadge = (TextView) view.findViewById(R.id.tv_number_pending_payments_main);
        itemView.addView(view);

//        showNewNewsMessage();
    }

    private void configureDrawerLayout() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
//        toolbar.setNavigationIcon(null);
    }

//    public void toggleDrawerRight() {
//        if (drawerLayout.isDrawerOpen(Gravity.END)) {
//            drawerLayout.closeDrawer(Gravity.END);
//        } else {
//            drawerLayout.openDrawer(Gravity.END);
//        }
//    }

    private void configureToolbarBackArrowBehaviour() {

        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> {

            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {

                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }

            }
        });
    }

    @Override
    public void refreshData() {

        for (BaseFragment baseFragment : sections) {
            if (baseFragment.isAdded()) {
                baseFragment.refreshData();
            }
        }
//        if (fragmentShowing != null) {
//            fragmentShowing.refreshData();
//        }

        presenter.refreshData();
    }

    public void onMESCityChanged() {

        for (BaseFragment baseFragment : sections) {
            if (baseFragment.isAdded()) {
                baseFragment.onMESCityChanged();
            }
        }
    }


    public void setFilterEntities(FilterEntities filterEntities) {
        if (currentSection == 0) {
            ((EntitiesPresenter) entitiesFragment.getBasePresenter()).setFilterEntities(filterEntities);
            if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        }
    }


    // INTERACTIONS
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (bottomNavView.getSelectedItemId() == item.getItemId()) {
            return false;
        }

        String mesCode = getPrefs().getString(App.SHARED_MES_CODE_SAVED, null);
        MESData mesData = MES.getMESbyCode(mesCode).getMesData();

        try {
            switch (item.getItemId()) {
                case R.id.navigation_entities:
                    setToolbarTitle(R.string.entities);
                    showFragment(0);
                    return true;
                case R.id.navigation_wallet:
                    setToolbarTitle(R.string.wallet);
                    showFragment(1);
                    return true;
                case R.id.navigation_profile:
                    setToolbarTitle(R.string.highlighted);
                    showFragment(2);
                    return true;

                case R.id.menuItem_the_social_market:
                    WebViewActivity.startLocalHtml(this, getString(R.string.the_social_market), WebViewActivity.FILENAME_QUE_ES_MES);
                    break;

                case R.id.menuItem_change_social_market:
                    DialogSelectMES.with(this)
                            .setOnSelectMESListener(mes -> {
//                            toast("MES seleccionado: " + mes.getName());
                                getPrefs().edit()
                                        .putString(App.SHARED_MES_CODE_SAVED, mes.getCode())
                                        .remove(App.SHARED_ENTITIES_CACHE)
                                        .commit();
                                MES.setCityCode(mes.getCode());
                                updateMenuViewsByMES();
                                onMESCityChanged();
                                presenter.onLogoutClick();
                            })
                            .show();
                    break;

                case R.id.menuItem_invitations:
                    InvitationsPresenter.launchInvitationsActivity(this);
                    break;

                case R.id.menuItem_how_boniato_works:
                    WebViewActivity.startLocalHtml(this, getString(R.string.how_it_works), WebViewActivity.FILENAME_COMO_FUNCIONA_BONIATO);
//                String url2 = "https://madrid.mercadosocial.net/reboniato/";
//                WebViewActivity.startRemoteUrl(this, getString(R.string.how_it_works), url2);
                    break;

                case R.id.menuItem_get_boniatos:

//                Bundle bundle = new Bundle();
//                bundle.putString("title", "Noticia de prueba");
//                bundle.putString("message", "Texto de noticia");
//
//                bundle.putString("type", Notification.TYPE_NEWS);
//                bundle.putString("id", "87938556-10e1-41fb-bd1c-0fb854df72b1");
//
//                Intent intent = new Intent(App.ACTION_NOTIFICATION_RECEIVED);
//                intent.putExtras(bundle);
//                sendBroadcast(intent);

                    if (App.getUserData(this) != null) {
                        startActivity(GetBoniatosPresenter.newGetBoniatosActivity(this));
                    } else {
                        Toasty.info(this, getString(R.string.enter_with_your_account)).show();
                        bottomNavView.findViewById(R.id.navigation_wallet).performClick();
                    }


//                Notification notification = new Notification();
//                notification.setId("");
//                notification.setSender("Pepa");
//                notification.setAmount(15.3f);
//                NewPaymentDialog.newInstance(notification)
//                        .setOnCloseListener(new NewPaymentDialog.OnCloseListener() {
//                            @Override
//                            public void onClose() {
//                                refreshData();
//                            }
//                        }).show(getSupportFragmentManager(), null);

                    break;

                case R.id.nav_contact_email:
                    String emailMES = mesData.getEmailContact();

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", emailMES, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_contact_subject));
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(emailIntent);
                    break;

                case R.id.nav_contact_web:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mesData.getWeb())));
                    break;

                case R.id.nav_contact_facebook:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mesData.getFacebook())));
                    break;

                case R.id.nav_contact_twitter:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mesData.getTwitter())));
                    break;

                case R.id.nav_contact_vimeo:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mesData.getVimeo())));
                    break;
            }
        } catch (ActivityNotFoundException e) {
            // ignore
        }

//        toast("Pulsado: " + item.getTitle());
        drawerLayout.closeDrawer(Gravity.LEFT);
        return false;
    }


    private void updateMenuViewsByMES() {

        String mesCode = getPrefs().getString(App.SHARED_MES_CODE_SAVED, null);
        MESData mesData = MES.getMESbyCode(mesCode).getMesData();

        boolean isMadrid = TextUtils.equals(getPrefs().getString(App.SHARED_MES_CODE_SAVED, null), MES.CODE_MADRID);

        bottomNavView.getMenu().findItem(R.id.navigation_wallet).setVisible(isMadrid);

        navigationView.getHeaderView(0).setVisibility(isMadrid ? View.VISIBLE : View.GONE);

        Menu leftMenu = navigationView.getMenu();
        leftMenu.findItem(R.id.menuItem_get_boniatos).setVisible(isMadrid);
        leftMenu.findItem(R.id.menuItem_how_boniato_works).setVisible(isMadrid);
        leftMenu.findItem(R.id.menuItem_the_social_market).setVisible(isMadrid);

        leftMenu.findItem(R.id.nav_contact_web).setVisible(mesData.getWeb() != null);
        leftMenu.findItem(R.id.nav_contact_facebook).setVisible(mesData.getFacebook() != null);
        leftMenu.findItem(R.id.nav_contact_twitter).setVisible(mesData.getTwitter() != null);
        leftMenu.findItem(R.id.nav_contact_vimeo).setVisible(mesData.getVimeo() != null);
        leftMenu.findItem(R.id.nav_contact_instagram).setVisible(mesData.getInstagram() != null);


    }


    private void showFragment(int sectionNumber) {

        BaseFragment fragmentToShow = sections.get(sectionNumber);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);

        fragmentTransaction.replace(R.id.content, fragmentToShow);

//        if (!fragmentToShow.isAdded()) {
//            fragmentTransaction.add(R.id.content, fragmentToShow);
//        } else if (fragmentToShow.isHidden()) {
//            fragmentTransaction.show(fragmentToShow);
//        } else {
//            throw new IllegalStateException("WTF happen with this fragment: " + fragmentToShow.toString());
//        }
//
//
//        if (currentSection >= 0) {
//            android.support.v4.app.Fragment fragmentToHide = sections.get(currentSection);
//            fragmentTransaction.hide(fragmentToHide);
//        }
        fragmentTransaction.commit();

        currentSection = sectionNumber;

    }

    private void openIPDialog() {
        final EditText editText = new EditText(this);
        editText.setText("192.168.43.42:8000");

        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton("Establecer IP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPrefs().edit().putString("baseUrl", "http://" + editText.getText().toString()).commit();
                    }
                }).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

//                LoginDialog loginDialog = LoginDialog.newInstance();
//                loginDialog.configure(getString(R.string.enter), new LoginDialog.LoginDialogListener() {
//                    @Override
//                    public void onAccept(String username, String password) {
//                        toast("Entrando con: " + username + "...");
//                    }
//                });
//
//                loginDialog.setAvoidDismiss(true);
//                loginDialog.show(getSupportFragmentManager(), null);

                startActivity(new Intent(this, LoginActivity.class));

                break;

            case R.id.btn_singup:
                startActivity(new Intent(this, RegisterWebActivity.class));
                break;

            case R.id.btn_logout:
                presenter.onLogoutClick();
                break;

            case R.id.btn_go_to_profile:
            case R.id.view_user_info:
                startActivityForResult(new Intent(this, ProfileActivity.class), REQ_CODE_PROFILE);
                break;
        }

        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
        }
    }



    public void onMenuFilterClick() {

        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {

            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }

            drawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    // PRESENTER CALLBACKS

    @Override
    public void showUserData(Data userData) {

        viewEnterButtons.setVisibility(userData == null ? View.VISIBLE : View.GONE);
        viewUserInfo.setVisibility(userData == null ? View.GONE : View.VISIBLE);

        if (userData != null) {
            tvUserName.setText(userData.getName(true));
            String logoUrl = userData.getLogoThumbnail();
            Picasso.with(this)
                    .load(logoUrl)
                    .placeholder(R.mipmap.ic_avatar_2)
                    .transform(new CircleTransform())
                    .error(R.mipmap.ic_avatar_2)
                    .into(imgAvatar);

            showInvitationMenuItem(userData.getPerson() != null && !userData.getPerson().is_guest_account());

            tvMES.setText(String.format(getString(R.string.mes_format), userData.getCity()));
            if (!userData.isEntity()) {
                if (userData.getPerson().is_guest_account()) {
                    String dateFormatted = DateUtils.convertDateApiToUserFormat(userData.getPerson().getExpiration_date());
                    tvGuestInfo.setText(String.format(getString(R.string.guest_account_info_format), dateFormatted));
                }
            }

        } else {
            imgAvatar.setImageResource(R.mipmap.ic_avatar_2);

            for (BaseFragment baseFragment : sections) {
                if (baseFragment.isAdded()) {
                    baseFragment.refreshData();
                }
            }

            showInvitationMenuItem(false);
        }

    }

    private void showInvitationMenuItem(boolean show) {
        menuItemInvitations.setVisible(show);
    }

    @Override
    public void showPendingPaymentsNumber(int numberPendingPayments) {
        tvPendingPaymentsBadge.setText(String.valueOf(numberPendingPayments));
        tvPendingPaymentsBadge.setVisibility(numberPendingPayments > 0 ? View.VISIBLE : View.GONE);
    }

}
