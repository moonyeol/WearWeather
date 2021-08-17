package wear.weather.model

import android.os.Parcel
import android.os.Parcelable

data class BrandTagModel(
        var x: Int? = 0,
        var y: Int? = 0,
        var text: String? = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        x?.let { parcel.writeInt(it) }
        y?.let { parcel.writeInt(it) }
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BrandTagModel> {
        override fun createFromParcel(parcel: Parcel): BrandTagModel {
            return BrandTagModel(parcel)
        }

        override fun newArray(size: Int): Array<BrandTagModel?> {
            return arrayOfNulls(size)
        }
    }
}
