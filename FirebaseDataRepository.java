package com.curofy.domain.repository;

import java.util.LinkedHashMap;
import java.util.List;

import rx.Observable;

/**
 * Created by nateshrelhan on 07/11/16.
 */

public interface FirebaseDataRepository<T> {
    /**
     * @return {@link Observable} of child data.
     */
    Observable<T> childData(LinkedHashMap<QueryType, String> query);

    /**
     * @return {@link Observable} of data list which is of type node.
     */
    Observable<List<T>> nodeChildList(LinkedHashMap<QueryType, String> query);

    /**
     * @return {@link Observable} of data list which is of type single_node.
     */
    Observable<List<T>> singleNodeChildList(LinkedHashMap<QueryType, String> query);


    //Used to parse query type
    enum QueryType {
        ORDER_BY_CHILD,
        ORDER_BY_KEY,
        ORDER_BY_VALUE,
        START_AT,
        END_AT,
        EQUAL_AT,
        LIMIT_TO_FIRST,
        LIMIT_TO_LAST,
        DISCUSSION_ID,
        TIME_TO_LIVE,
        FILTER_ID,
        DIALOG_ID
    }

    //Listener is attached to query based on this
    enum ListenerType {
        NODE,
        CHILD,
        SINGLE_NODE
    }
}
