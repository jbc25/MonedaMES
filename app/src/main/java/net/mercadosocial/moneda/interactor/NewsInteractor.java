package net.mercadosocial.moneda.interactor;

import android.content.Context;

import net.mercadosocial.moneda.R;
import net.mercadosocial.moneda.api.NewsApi;
import net.mercadosocial.moneda.api.response.NewsResponse;
import net.mercadosocial.moneda.base.BaseInteractor;
import net.mercadosocial.moneda.base.BaseView;
import net.mercadosocial.moneda.model.News;
import net.mercadosocial.moneda.util.Util;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by julio on 14/02/16.
 */
public class NewsInteractor extends BaseInteractor {
    

    public NewsInteractor(Context context, BaseView baseView) {
        this.baseView = baseView;
        this.context = context;

    }


    public void getNews(final BaseApiGETListCallback<News> callback) {

        if (!Util.isConnected(context)) {
            baseView.toast(R.string.no_connection);
            return;
        }

        baseView.setRefresing(true);

        getApi().getNews()
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).doOnTerminate(actionTerminate)
                .subscribe(new Observer<NewsResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(NewsResponse response) {

                        baseView.setRefresing(false);

                        callback.onResponse(response.getNews());


                    }
                });


    }

    public void getNewsById(String id, final BaseApiCallback<News> callback) {

        if (!Util.isConnected(context)) {
            baseView.toast(R.string.no_connection);
            return;
        }

        baseView.setRefresing(true);

        getApi().getNewsById(id)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).doOnTerminate(actionTerminate)
                .subscribe(new Observer<News>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(News news) {

                        baseView.setRefresing(false);

                        callback.onResponse(news);


                    }
                });


    }

    private NewsApi getApi() {
        return getApi(NewsApi.class);
    }


}
