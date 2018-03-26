package com.curofy.data.entity.common

import com.google.firebase.database.PropertyName

/**
 * Created by nateshrelhan on 1/15/18.
 */
class AccessCredentialsEntity {
    @PropertyName("access")
    lateinit var access: String
    @PropertyName("pendingTitle")
    var pendingTitle: String? = null
    @PropertyName("pendingSubTitle")
    var pendingSubTitle: String? = null
    @PropertyName("rejectedTitle")
    var rejectedTitle: String? = null
    @PropertyName("rejectedSubTitle")
    var rejectedSubTitle: String? = null
    @PropertyName("documentUploadedTitle")
    var documentUploadedTitle: String? = null
    @PropertyName("documentUploadedSubTitle")
    var documentUploadedSubTitle: String? = null
    @PropertyName("otherTitle")
    var otherTitle: String? = null
    @PropertyName("otherRoute")
    var otherRoute: String? = null
}