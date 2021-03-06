package net.mercadosocial.moneda.interactor;

import android.content.Context;

import net.mercadosocial.moneda.R;
import net.mercadosocial.moneda.api.UserApi;
import net.mercadosocial.moneda.api.model.InvitationRequest;
import net.mercadosocial.moneda.api.response.ApiError;
import net.mercadosocial.moneda.base.BaseInteractor;
import net.mercadosocial.moneda.base.BaseView;
import net.mercadosocial.moneda.model.Entity;
import net.mercadosocial.moneda.model.Person;
import net.mercadosocial.moneda.util.Util;

import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by julio on 14/02/16.
 */
public class UserInteractor extends BaseInteractor {



    public UserInteractor(Context context, BaseView baseView) {
        this.baseView = baseView;
        this.context = context;

    }

    public void getPersonProfile(BaseApiCallback<Person> callback) {

        if (!Util.isConnected(context)) {
            baseView.toast(R.string.no_connection);
            return;
        }

        getApi().getPersonProfile()
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                .doOnTerminate(actionTerminate)
                .subscribe(new Observer<Response<Person>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<Person> response) {

                        if (!response.isSuccessful()) {
                            ApiError apiError = ApiError.parse(response);
                            callback.onError(apiError.getMessage());
                            return;
                        }

                        callback.onResponse(response.body());

                    }
                });
    }

    public void getEntityProfile(BaseApiCallback<Entity> callback) {

        if (!Util.isConnected(context)) {
            baseView.toast(R.string.no_connection);
            return;
        }

        getApi().getEntityProfile()
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                .doOnTerminate(actionTerminate)
                .subscribe(new Observer<Response<Entity>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<Entity> response) {

                        if (!response.isSuccessful()) {
                            ApiError apiError = ApiError.parse(response);
                            callback.onError(apiError.getMessage());
                            return;
                        }

                        callback.onResponse(response.body());

                    }
                });
    }

    public void updateProfile(Person profile, final BaseApiPOSTCallback callback) {

        if (!Util.isConnected(context)) {
            baseView.toast(R.string.no_connection);
            return;
        }

        getApi().updateFavourites(profile)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                .doOnTerminate(actionTerminate)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<Void> response) {

                        if (!response.isSuccessful()) {
                            ApiError apiError = ApiError.parse(response);
                            callback.onError(apiError.getMessage());
                            return;
                        }

                        callback.onSuccess(null);

                    }
                });


    }

    public void sendInvitation(String email, final BaseApiPOSTCallback callback) {

        if (!Util.isConnected(context)) {
            baseView.toast(R.string.no_connection);
            return;
        }

        InvitationRequest invitationRequest = new InvitationRequest(email);

        getApi().sendInvitation(invitationRequest)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
//                .doOnTerminate(actionTerminate)
                .subscribe(new Observer<Response<Void>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(Response<Void> response) {

                        if (!response.isSuccessful()) {
                            ApiError apiError = ApiError.parse(response);
                            callback.onError(apiError.getMessage());
                            return;
                        }

                        callback.onSuccess(null);

                    }
                });


    }

    private UserApi getApi() {
        return getApi(UserApi.class);
    }


}
