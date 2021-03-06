package net.mercadosocial.moneda.ui.get_boniatos;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;

import net.mercadosocial.moneda.R;
import net.mercadosocial.moneda.base.BaseInteractor;
import net.mercadosocial.moneda.base.BasePresenter;
import net.mercadosocial.moneda.interactor.WalletInteractor;
import net.mercadosocial.moneda.util.Util;

import es.dmoral.toasty.Toasty;

/**
 * Created by julio on 3/03/18.
 */


public class GetBoniatosPresenter extends BasePresenter {

    private final GetBoniatosView view;
    private Float amount;

    public static Intent newGetBoniatosActivity(Context context) {

        Intent intent = new Intent(context, GetBoniatosActivity.class);

        return intent;
    }

    public static GetBoniatosPresenter newInstance(GetBoniatosView view, Context context) {

        return new GetBoniatosPresenter(view, context);

    }

    private GetBoniatosPresenter(GetBoniatosView view, Context context) {
        super(context, view);

        this.view = view;

    }

    public void onCreate() {

    }

    public void onResume() {

        refreshData();
    }

    public void refreshData() {


    }

    public void onPurchaseClick(String amountStr) {

        try {
            Float amount = Float.parseFloat(amountStr);
            purchase(amount);
        } catch (NumberFormatException e) {
            Toasty.error(context, context.getString(R.string.invalid_number)).show();
        }
    }

    private void purchase(final Float amount) {

        this.amount = amount;

        view.setRefreshing(true);
        new WalletInteractor(context, view).purchaseCurrency(amount, new BaseInteractor.BaseApiCallback<String>() {

            @Override
            public void onResponse(String url) {

                view.showUrlPayment(url.replace("http://", "https://") + "?from_app=true");
//                showPurchaseSuccessDialog(amount);
            }

            @Override
            public void onError(String message) {

                Toasty.error(context, message).show();
            }
        });
    }

    public void showPurchaseSuccessDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.congratulations)
//                .setIcon(R.mipmap.img_happy_face)
                .setMessage(String.format(context.getString(R.string.purchase_success_message),
                        Util.getDecimalFormatted(amount, false)))
                .setNegativeButton(R.string.back, (dialog, which) -> finish())
                .show();
    }
}
