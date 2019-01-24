package net.mercadosocial.moneda.ui.auth.register_web;

import android.os.Bundle;
import android.view.View;

import net.mercadosocial.moneda.R;
import net.mercadosocial.moneda.api.common.ApiClient;
import net.mercadosocial.moneda.base.BaseActivity;
import net.mercadosocial.moneda.ui.info.WebViewActivity;

public class RegisterWebActivity extends BaseActivity implements View.OnClickListener {

    private View btnRegisterPerson;
    private View btnRegisterEntity;

    private void findViews() {
        btnRegisterPerson = findViewById(R.id.btn_register_person);
        btnRegisterEntity = findViewById(R.id.btn_register_entity);

        btnRegisterPerson.setOnClickListener(this);
        btnRegisterEntity.setOnClickListener(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_web);
        configureSecondLevelActivity();
        findViews();
    }

    @Override
    public void onClick(View v) {

        String type = "";
        switch (v.getId()) {
            case R.id.btn_register_person:
                type = "consumer";
                break;

            case R.id.btn_register_entity:
                type = "provider";
                break;
        }

        String url = ApiClient.BASE_URL_TOOL + "accounts/signup/" + type + "/";
        WebViewActivity.startRemoteUrl(this, getString(R.string.new_provider), url);
        finish();
    }
}