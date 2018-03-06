package net.mercadosocial.moneda.interactor;

import android.content.Context;

import net.mercadosocial.moneda.R;
import net.mercadosocial.moneda.api.TransactionApi;
import net.mercadosocial.moneda.api.response.TransactionResponse;
import net.mercadosocial.moneda.base.BaseInteractor;
import net.mercadosocial.moneda.base.BaseView;
import net.mercadosocial.moneda.model.Transaction;
import net.mercadosocial.moneda.util.Util;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by julio on 14/02/16.
 */
public class TransactionInteractor extends BaseInteractor {
    

    public TransactionInteractor(Context context, BaseView baseView) {
        this.baseView = baseView;
        this.context = context;

    }


    public void getTransactions(final BaseApiGETListCallback<Transaction> callback) {

        if (!Util.isConnected(context)) {
            baseView.toast(R.string.no_connection);
            return;
        }

        getApi().getTransactions()
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(actionTerminate)
                .subscribe(new Observer<TransactionResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(TransactionResponse transactionsResponse) {

                        baseView.setRefresing(false);

                        callback.onResponse(transactionsResponse.getTransactions());


                    }
                });


    }


    private TransactionApi getApi() {
        return getApi(TransactionApi.class);
    }


}