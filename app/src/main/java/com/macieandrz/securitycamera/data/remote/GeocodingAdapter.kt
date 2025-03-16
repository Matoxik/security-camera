import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import com.squareup.moshi.JsonWriter
import com.macieandrz.securitycamera.data.models.Location

class GeocodingAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Location? {
        var lat: Double? = null
        var lng: Double? = null

        // Get to lat/lng fields
        try {
            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "results" -> {
                        reader.beginArray()
                        if (reader.hasNext()) {
                            reader.beginObject()
                            while (reader.hasNext()) {
                                when (reader.nextName()) {
                                    "geometry" -> {
                                        reader.beginObject()
                                        while (reader.hasNext()) {
                                            when (reader.nextName()) {
                                                "location" -> {
                                                    reader.beginObject()
                                                    while (reader.hasNext()) {
                                                        when (reader.nextName()) {
                                                            "lat" -> lat = reader.nextDouble()
                                                            "lng" -> lng = reader.nextDouble()
                                                            else -> reader.skipValue()
                                                        }
                                                    }
                                                    reader.endObject()
                                                }
                                                else -> reader.skipValue()
                                            }
                                        }
                                        reader.endObject()
                                    }
                                    else -> reader.skipValue()
                                }
                            }
                            reader.endObject()
                        }
                        reader.endArray()
                    }
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
        } catch (e: Exception) {
            return null
        }


        // address will be completed later
        return if (lat != null && lng != null) {
            Location(address = "", lat = lat, lng = lng)
        } else {
            null
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: Location?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.beginObject()
        writer.name("address").value(value.address)
        writer.name("lat").value(value.lat)
        writer.name("lng").value(value.lng)
        writer.endObject()
    }
}
