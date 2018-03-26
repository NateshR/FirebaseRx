package com.curofy.data.repository

import android.content.Context
import com.android.volley.Request
import com.crashlytics.android.Crashlytics
import com.curofy.data.R
import com.curofy.data.entity.common.AccessCredentialsEntity
import com.curofy.data.entity.common.NetworkResponse
import com.curofy.data.entity.mapper.AccessCredentialsEntityMapper
import com.curofy.data.firebase.FirebaseDataStoreFactory
import com.curofy.data.net.AsyncVolley
import com.curofy.data.net.NetworkException
import com.curofy.data.preference.Sessions
import com.curofy.data.util.ImageCompress
import com.curofy.domain.content.common.AccessCredentialsContent
import com.curofy.domain.repository.AccessCredentialsRepository
import com.curofy.domain.repository.FirebaseDataRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.reflect.TypeToken
import rx.Observable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by nateshrelhan on 1/15/18.
 */
@Singleton
class AccessCredentialsDataRepository @Inject constructor(val context: Context,
                                                          val firebaseDataStoreFactory: FirebaseDataStoreFactory<AccessCredentialsEntity>,
                                                          val accessCredentialsEntityMapper: AccessCredentialsEntityMapper) : AccessCredentialsRepository {
    companion object {
        var dataBaseReference: DatabaseReference? = null
        val KEY_USER: String = "users"
        val KEY_ACCESS_DETAILS: String = "access_details"
    }

    override fun childData(query: LinkedHashMap<FirebaseDataRepository.QueryType, String>?): Observable<AccessCredentialsContent> {
        return firebaseDataStoreFactory.data(AccessCredentialsEntity::class.java, firebaseDataStoreFactory.parseQuery(getDatabaseReference(), query)).map { accessCredentialsEntityMapper.transform(it) }
    }

    override fun nodeChildList(query: LinkedHashMap<FirebaseDataRepository.QueryType, String>?): Observable<MutableList<AccessCredentialsContent>>? {
        return null
    }

    override fun singleNodeChildList(query: LinkedHashMap<FirebaseDataRepository.QueryType, String>?): Observable<MutableList<AccessCredentialsContent>>? {
        return null
    }

    private fun getDatabaseReference(): DatabaseReference {
        if (dataBaseReference != null) return dataBaseReference!!
        dataBaseReference = FirebaseDatabase.getInstance().getReference().child(KEY_USER).child(Sessions.loadUserName(context)).child(KEY_ACCESS_DETAILS)
        dataBaseReference!!.keepSynced(true)
        return dataBaseReference!!
    }
}
