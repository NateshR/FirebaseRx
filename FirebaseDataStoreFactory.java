package com.curofy.data.firebase;

import android.util.Log;

import com.curofy.data.net.FirebaseDataException;
import com.curofy.domain.repository.FirebaseDataRepository;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by nateshrelhan on 27/10/16.
 */

/**
 * @param <T> to attach firebase node/child/singleValue listener based on their type and example class
 */
@Singleton
public class FirebaseDataStoreFactory<T> {
    private static final String TAG = FirebaseDataStoreFactory.class.getSimpleName();

    @Inject
    public FirebaseDataStoreFactory() {
    }

    /**
     * @param exampleClass  for parsing data into
     * @param queryDatabase to attach child listener
     * @return {@link Observable} of child listener
     */
    public Observable<T> data(Class<T> exampleClass, Query queryDatabase) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                Log.d(TAG, " --> attached child listener");
                ChildEventListener childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, " --> onChildAdded:" + dataSnapshot.getKey());
                        T childNodeObject = dataSnapshot.getValue(exampleClass);
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(childNodeObject);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, " --> onChildChanged:" + dataSnapshot.getKey());
                        T childNodeObject = dataSnapshot.getValue(exampleClass);
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(childNodeObject);
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Log.d(TAG, " --> onChildRemoved:" + dataSnapshot.getKey());
                        T childNodeObject = dataSnapshot.getValue(exampleClass);
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(childNodeObject);
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                        Log.d(TAG, " --> onChildMoved:" + dataSnapshot.getKey());
                        T childNodeObject = dataSnapshot.getValue(exampleClass);
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(childNodeObject);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onError(new FirebaseDataException(databaseError));
                    }
                };
                queryDatabase.addChildEventListener(childEventListener);
            }
        });
    }

    /**
     * @param listenerType  valueEvent or singleValue
     * @param exampleClass  for parsing data into
     * @param queryDatabase to attach listener base on their type
     * @return {@link Observable} of listener
     */
    public Observable<List<T>> dataList(FirebaseDataRepository.ListenerType listenerType, Class<T> exampleClass, Query queryDatabase) {
        switch (listenerType) {
            case NODE:
                return Observable.create(subscriber -> {
                    Log.d(TAG, " --> attached node listener");
                    ValueEventListener nodeEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, " --> onDataChanged:" + dataSnapshot.getKey() + " --- " + dataSnapshot.getChildrenCount());
                            List<T> listOfEntity = new ArrayList<T>();
                            for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                                T rootNodeObject = singleDataSnapshot.getValue(exampleClass);
                                listOfEntity.add(rootNodeObject);
                            }
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(listOfEntity);
                                //On complete is not called so that node listener lives throughout.
                                /*subscriber.onCompleted();*/
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (!subscriber.isUnsubscribed())
                                subscriber.onError(new FirebaseDataException(databaseError));
                        }
                    };
                    queryDatabase.addValueEventListener(nodeEventListener);
                });
            case SINGLE_NODE:
                return Observable.create(subscriber -> {
                    ValueEventListener nodeSingleEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, " --> onDataChanged Single:" + dataSnapshot.getKey() + " --- " + dataSnapshot.getChildrenCount());
                            List<T> listOfEntity = new ArrayList<T>();
                            for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {
                                T rootNodeObject = singleDataSnapshot.getValue(exampleClass);
                                listOfEntity.add(rootNodeObject);
                            }
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(listOfEntity);
                                subscriber.onCompleted();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, " --> onCancelled Single", databaseError.toException());
                            if (!subscriber.isUnsubscribed())
                                subscriber.onError(new FirebaseDataException(databaseError));

                        }
                    };
                    queryDatabase.addListenerForSingleValueEvent(nodeSingleEventListener);
                });
            default:
                //todo
                return null;
        }
    }

    /**
     * @param queryMap
     * @return parsed query based on the {@link java.util.LinkedHashMap} of query
     */
    public Query parseQuery(Query finalQuery, LinkedHashMap<FirebaseDataRepository.QueryType, String> queryMap) {
        try {
            if (queryMap == null) return finalQuery;
            for (Map.Entry<FirebaseDataRepository.QueryType, String> querySet : queryMap.entrySet()) {

                FirebaseDataRepository.QueryType querySetKey = querySet.getKey();
                String querySetValue = querySet.getValue();

                if (querySetKey.equals(FirebaseDataRepository.QueryType.ORDER_BY_CHILD)) {
                    finalQuery = finalQuery.orderByChild(querySetValue);
                }
                if (querySetKey.equals(FirebaseDataRepository.QueryType.ORDER_BY_KEY)) {
                    finalQuery = finalQuery.orderByKey();
                }
                if (querySetKey.equals(FirebaseDataRepository.QueryType.ORDER_BY_VALUE)) {
                    finalQuery = finalQuery.orderByValue();
                }
                if (querySetKey.equals(FirebaseDataRepository.QueryType.START_AT)) {
                    finalQuery = finalQuery.startAt(querySetValue);
                }
                if (querySetKey.equals(FirebaseDataRepository.QueryType.END_AT)) {
                    finalQuery = finalQuery.endAt(querySetValue);
                }
                if (querySetKey.equals(FirebaseDataRepository.QueryType.EQUAL_AT)) {
                    finalQuery = finalQuery.equalTo(querySetValue);
                }
                if (querySetKey.equals(FirebaseDataRepository.QueryType.LIMIT_TO_FIRST)) {
                    finalQuery = finalQuery.limitToFirst(Integer.parseInt(querySetValue));
                }
                if (querySetKey.equals(FirebaseDataRepository.QueryType.LIMIT_TO_LAST)) {
                    finalQuery = finalQuery.limitToLast(Integer.parseInt(querySetValue));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalQuery;
    }
}
