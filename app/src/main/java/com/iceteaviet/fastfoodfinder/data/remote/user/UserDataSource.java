package com.iceteaviet.fastfoodfinder.data.remote.user;

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by tom on 7/15/18.
 */
public interface UserDataSource {
    void insertOrUpdate(String name, String email, String photoUrl, String uid, List<UserStoreList> storeLists);

    void insertOrUpdate(User user);

    void updateStoreListForUser(String uid, List<UserStoreList> storeLists);

    Single<User> getUser(String uid);

    Single<Boolean> isUserExists(String uid);

    Observable<UserStoreEvent> subscribeFavouriteStoresOfUser(String uid);
}
