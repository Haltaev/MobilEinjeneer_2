package ru.mobilengineer.data.api.model.response

import android.os.Parcel

import android.os.Parcelable


class ItemParcelable : Parcelable {
    var id: String?
    var title: String?
    var quantity: String?
    var vendorCode: String?

    constructor(
        id: String?,
        username: String?,
        email: String?,
        password: String?
    ) {
        this.id = id
        this.title = username
        this.quantity = email
        this.vendorCode = password
    }

    /**
     * Use when reconstructing User object from parcel
     * This will be used only by the 'CREATOR'
     * @param in a parcel to read this object
     */
    constructor(`in`: Parcel) {
        id = `in`.readString()
        title = `in`.readString()
        quantity = `in`.readString()
        vendorCode = `in`.readString()
    }

    /**
     * Define the kind of object that you gonna parcel,
     * You can use hashCode() here
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Actual object serialization happens here, Write object content
     * to parcel one by one, reading should be done according to this write order
     * @param dest parcel
     * @param flags Additional flags about how the object should be written
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(title)
        dest.writeString(quantity)
        dest.writeString(vendorCode)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is ItemParcelable) {
            return title.equals(obj.title, ignoreCase = true)
        }
        return false
    }

    override fun hashCode(): Int {
        return title.hashCode()
    }

    companion object {
        /**
         * This field is needed for Android to be able to
         * create new objects, individually or as arrays
         *
         * If you don’t do that, Android framework will through exception
         * Parcelable protocol requires a Parcelable.Creator object called CREATOR
         */
        val CREATOR: Parcelable.Creator<ItemParcelable?> = object : Parcelable.Creator<ItemParcelable?> {
            override fun createFromParcel(`in`: Parcel): ItemParcelable? {
                return ItemParcelable(`in`)
            }

            override fun newArray(size: Int): Array<ItemParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }
}