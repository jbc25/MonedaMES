package net.mercadosocial.moneda.ui.entities;

import android.content.Context;
import android.util.Log;

import net.mercadosocial.moneda.App;
import net.mercadosocial.moneda.api.response.Data;
import net.mercadosocial.moneda.base.BaseInteractor;
import net.mercadosocial.moneda.base.BasePresenter;
import net.mercadosocial.moneda.interactor.EntityInteractor;
import net.mercadosocial.moneda.interactor.UserInteractor;
import net.mercadosocial.moneda.model.Entity;
import net.mercadosocial.moneda.model.FilterEntities;
import net.mercadosocial.moneda.model.Person;
import net.mercadosocial.moneda.ui.entity_info.EntityInfoPresenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by julio on 11/10/17.
 */


public class EntitiesPresenter extends BasePresenter {

    private final EntitiesView view;
    private final EntityInteractor entityInteractor;
    private final UserInteractor userInteractor;
    private List<Entity> entities = new ArrayList<>();
    private boolean hasMore;
    private int currentApiPage;
    private FilterEntities filterEntities;
    private boolean refreshFavouritesAtEnd;

    public static final int SCREEN_ENTITIES_TYPE_LIST = 0;
    public static final int SCREEN_ENTITIES_TYPE_MAP = 1;

    private int currentScreen = SCREEN_ENTITIES_TYPE_LIST;
    private boolean refreshing;
    private EntitiesRefreshListener entitiesRefreshListener;


    public static EntitiesPresenter newInstance(EntitiesView view, Context context) {

        return new EntitiesPresenter(view, context);

    }

    private EntitiesPresenter(EntitiesView view, Context context) {
        super(context, view);

        this.view = view;

        entityInteractor = new EntityInteractor(context, view);
        userInteractor = new UserInteractor(context, view);

    }

    public void onCreate() {
        view.showScreenType(currentScreen);
        refreshData();
    }

    public void onResume() {

        if (entities != null && !entities.isEmpty()) {
            processFavs();
            if (entitiesRefreshListener != null) {
                entitiesRefreshListener.updateData();
            }
        }
    }

    public void onPause() {
        if (refreshFavouritesAtEnd) {
            updateFavouritesOnServer();
            refreshFavouritesAtEnd = false;
        }

    }

    private void updateFavouritesLocally() {

        Data data = App.getUserData(context);

        if (data == null) {
            return;
        }

        List<String> favEntitiesUpdated = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.isFavourite()) {
                favEntitiesUpdated.add(entity.getId());
            }
        }

        data.setFav_entities(favEntitiesUpdated);
        App.saveUserData(context, data);
    }

    private void updateFavouritesOnServer() {
        Data data = App.getUserData(context);

        if (data == null) {
            return;
        }

        List<String> favEntitiesUpdated = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.isFavourite()) {
                favEntitiesUpdated.add(entity.getId());
            }
        }

        if (!data.isEntity()) {
            Person profile = Person.createPersonProfileFavourites(favEntitiesUpdated);
            userInteractor.updateProfile(profile, new BaseInteractor.BaseApiPOSTCallback() {
                @Override
                public void onSuccess(Integer id) {

                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "onError: updateFavouritesOnServer. " + message);
                }
            });
        }

    }

    public void refreshData() {

        entities.clear();
        currentApiPage = 0;

//        view.setRefreshing(true);
        refreshEntities();

    }

    public void loadNextPage() {

        currentApiPage++;
        refreshEntities();

    }

    private void refreshEntities() {

        refreshing = true;
        if (entitiesRefreshListener != null) {
            entitiesRefreshListener.updateData();
        }
        entityInteractor.getEntities(currentApiPage, filterEntities, new EntityInteractor.Callback() {


            @Override
            public void onResponse(List<Entity> entitiesApi, boolean hasMore) {
                refreshing = false;
                entities.addAll(entitiesApi);
                processFavs();

                EntitiesPresenter.this.hasMore = hasMore;

                if (entitiesRefreshListener != null) {
                    entitiesRefreshListener.updateData();
                }
            }

            @Override
            public void onError(String error) {
                refreshing = false;
                view.toast(error);
            }
        });
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public boolean hasMore() {
        return hasMore;
    }

    private void processFavs() {

        Data data = App.getUserData(context);
        if (data == null) {
            return;
        }

        List<Entity> entitiesToRemove = new ArrayList<>();

        for (Entity entity : entities) {
            if (data.getFav_entities().contains(entity.getId())) {
                entity.setFavourite(true);
            } else if (filterEntities != null && filterEntities.isOnlyFavourites()) {
                entitiesToRemove.add(entity);
            }
        }

        entities.removeAll(entitiesToRemove);
    }


    public void onEntityClicked(int position, String id) {

        Entity entity = null;
        if (position == -1) {
            entity = findEntityById(id);
        } else {
            entity = entities.get(position);
        }

        EntityInfoPresenter.startEntityInfoActivity(context, entity);
    }

    private Entity findEntityById(String id) {
        for (Entity entity : entities) {
            if (entity.getId().equals(id)) {
                return entity;
            }
        }
        return null;
    }

    public void onEntityFavouriteClicked(int position, boolean isFavourite) {

        entities.get(position).setFavourite(isFavourite);
        refreshFavouritesAtEnd = true;
        updateFavouritesLocally();

    }

    public void setFilterEntities(FilterEntities filterEntities) {
        this.filterEntities = filterEntities;
        refreshData();
    }

    public void onMapListButtonClick() {
        currentScreen = currentScreen == SCREEN_ENTITIES_TYPE_LIST ? SCREEN_ENTITIES_TYPE_MAP : SCREEN_ENTITIES_TYPE_LIST;
        view.showScreenType(currentScreen);
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public void setEntitiesRefreshListener(EntitiesRefreshListener entitiesRefreshListener) {
        this.entitiesRefreshListener = entitiesRefreshListener;
    }

    public void removeEntitiesRefreshListener(EntitiesRefreshListener entitiesRefreshListener) {
        if (this.entitiesRefreshListener == entitiesRefreshListener) {
            this.entitiesRefreshListener = null;
        }
    }
}
