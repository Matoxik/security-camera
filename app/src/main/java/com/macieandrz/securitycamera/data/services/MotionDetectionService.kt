import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class MotionDetectionService(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onMotionDetected: suspend () -> Unit
) {
    private var isEnabled = false
    private var lastNotificationTime: Long = 0
    private val notificationInterval = 60000L // 60 seconds
    private var previousRotationValues: FloatArray? = null
    private val motionDetectionThreshold = 15f // Motion detection threshold in degrees

    private lateinit var sensorManager: SensorManager
    private var rotationSensor: Sensor? = null
    private var isSensorRegistered = false

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR && isEnabled) {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                val orientationAngles = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)

                // Convert to degrees
                val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                // Check for position change
                previousRotationValues?.let { previousValues ->
                    val previousPitch = previousValues[0]
                    val previousRoll = previousValues[1]

                    val pitchDifference = abs(pitch - previousPitch)
                    val rollDifference = abs(roll - previousRoll)

                    // If significant motion is detected and 60 seconds have passed
                    if ((pitchDifference > motionDetectionThreshold || rollDifference > motionDetectionThreshold) &&
                        System.currentTimeMillis() - lastNotificationTime >= notificationInterval) {

                        coroutineScope.launch(Dispatchers.IO) {
                            onMotionDetected()
                            lastNotificationTime = System.currentTimeMillis()
                        }
                    }
                }

                // Update previous values
                previousRotationValues = floatArrayOf(pitch, roll)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }
    }

    fun initialize() {
        if (!::sensorManager.isInitialized) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (enabled) {
            registerSensor()
        } else {
            unregisterSensor()
        }
    }

    private fun registerSensor() {
        if (!isSensorRegistered) {
            rotationSensor?.let {
                sensorManager.registerListener(
                    sensorEventListener,
                    it,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                isSensorRegistered = true
                Log.d("MotionDetectionService", "Motion sensor registered")
            }
        }
    }

    private fun unregisterSensor() {
        if (isSensorRegistered) {
            sensorManager.unregisterListener(sensorEventListener)
            isSensorRegistered = false
            previousRotationValues = null
            Log.d("MotionDetectionService", "Motion sensor unregistered")
        }
    }

    fun release() {
        if (isSensorRegistered) {
            sensorManager.unregisterListener(sensorEventListener)
            isSensorRegistered = false
        }
    }
}
