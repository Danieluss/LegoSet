package me.danieluss.ubiquitous_systems2.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Codes")
data class Code(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "ItemID") var itemID: Long,
    @ColumnInfo(name = "ColorID") var colorID: Long?,
    @ColumnInfo(name = "Code") var code: Long?,
    @ColumnInfo(name = "Image") var image: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Code

        if (id != other.id) return false
        if (itemID != other.itemID) return false
        if (colorID != other.colorID) return false
        if (code != other.code) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image!!.contentEquals(other.image!!)) return false
        } else if (other.image != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + itemID.hashCode()
        result = 31 * result + (colorID?.hashCode() ?: 0)
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (image?.contentHashCode() ?: 0)
        return result
    }
}
