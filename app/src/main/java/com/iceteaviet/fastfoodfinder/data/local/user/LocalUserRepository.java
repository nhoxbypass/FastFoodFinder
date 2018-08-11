package com.iceteaviet.fastfoodfinder.data.local.user;

import android.util.Pair;

import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource;
import com.iceteaviet.fastfoodfinder.data.local.user.model.UserEntity;
import com.iceteaviet.fastfoodfinder.data.local.user.model.UserStoreListEntity;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList;
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyParamsException;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by tom on 7/25/18.
 */
public class LocalUserRepository implements UserDataSource {
    private static final String PARAM_UID = "uid";

    @Override
    public void insertOrUpdate(String name, String email, String photoUrl, String uid, List<UserStoreList> storeLists) {
        insertOrUpdate(new User(name, email, photoUrl, uid, storeLists));
    }

    @Override
    public void insertOrUpdate(final User user) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.where(UserEntity.class)
                        .findAll()
                        .deleteAllFromRealm();

                UserEntity userEntity = realm.createObject(UserEntity.class);
                userEntity.map(user);
            }
        });

        realm.close();
    }

    @Override
    public void updateStoreListForUser(String uid, final List<UserStoreList> storeLists) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                UserEntity entity = realm.where(UserEntity.class)
                        .findFirst();
                if (entity != null) {
                    RealmList<UserStoreListEntity> userStoreListEntities = new RealmList<>();
                    for (int i = 0; i < storeLists.size(); i++) {
                        userStoreListEntities.add(new UserStoreListEntity(storeLists.get(i)));
                    }
                    entity.setUserStoreLists(userStoreListEntities);
                }
            }
        });

        realm.close();
    }

    @Override
    public Single<User> getUser(final String uid) {
        return Single.create(new SingleOnSubscribe<User>() {
            @Override
            public void subscribe(SingleEmitter<User> emitter) {
                if (uid == null)
                    emitter.onError(new EmptyParamsException());

                Realm realm = Realm.getDefaultInstance();

                UserEntity entity = realm.where(UserEntity.class)
                        .findFirst();
                if (entity != null)
                    emitter.onSuccess(new User(entity));
                else
                    emitter.onSuccess(null);

                realm.close();
            }
        });
    }

    @Override
    public Single<Boolean> isUserExists(final String uid) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> emitter) {
                Realm realm = Realm.getDefaultInstance();

                long count = realm.where(UserEntity.class)
                        .equalTo(PARAM_UID, uid)
                        .count();
                if (count > 0)
                    emitter.onSuccess(true);
                else
                    emitter.onSuccess(false);

                realm.close();
            }
        });
    }

    @Deprecated
    @Override
    public Observable<Pair<Integer, Integer>> subscribeFavouriteStoresOfUser(final String uid) {
        return Observable.create(new ObservableOnSubscribe<Pair<Integer, Integer>>() {
            @Override
            public void subscribe(ObservableEmitter<Pair<Integer, Integer>> emitter) {
                Realm realm = Realm.getDefaultInstance();

                realm.where(UserEntity.class)
                        .equalTo(PARAM_UID, uid)
                        .findAll()
                        .addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<UserEntity>>() {
                            @Override
                            public void onChange(@NonNull RealmResults<UserEntity> userEntities, @NonNull OrderedCollectionChangeSet changeSet) {

                            }
                        });

                realm.close();
            }
        });
    }
}
