package com.example.imu_gyro_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import java.lang.Math;

public class MainActivity extends AppCompatActivity {

    TextView headerTextView;
    TextView sensorTextView;
    String accuracyText;
    List list;
    private SensorManager sm;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get sensor instance
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //textViews
        headerTextView = (TextView)findViewById(R.id.headerTextView);
        sensorTextView = (TextView)findViewById(R.id.sensorTextView);
//        list = sm.getSensorList(Sensor.TYPE_GYROSCOPE);
//        if(list.size()>0){
//            sm.registerListener(sl, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
//            headerTextView.setText("Accelerometer");
//        }else{
//            Toast.makeText(getBaseContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
//        }
    }

//    SensorEventListener sl = new SensorEventListener(){
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//            if (sensor.getType() == Sensor.TYPE_GYROSCOPE){
//                if (accuracy == 0) {
//                    accuracyText = "Unreliable";
//                }
//                else if (accuracy == 1) {
//                    accuracyText = "Low Accuracy";
//                }
//                else if (accuracy == 2) {
//                    accuracyText = "Medium Accuracy";
//                }
//                else {
//                    accuracyText = "High Accuracy";
//                }
//            }
//            headerTextView.setText("Gyroscope: " + accuracyText);
//        }
//        public void onSensorChanged(SensorEvent event) {
//            float[] values = event.values;
//            sensorTextView.setText("x: "+values[0]+"\ny: "+values[1]+"\nz: "+values[2]);
//        }
//    };

    private static final float NS2S = 1.0f / 1000000000.0f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;

    public void onSensorChanged(SensorEvent event) {

        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {

            final float dT = (event.timestamp - timestamp) * NS2S;

            // Axis of the rotation sample, not normalized yet.
            double axisX = Double.parseDouble(Float.toString(event.values[0]));
            double axisY = Double.parseDouble(Float.toString(event.values[1]));
            double axisZ =  Double.parseDouble(Float.toString(event.values[2]));;

            // Calculate the angular speed of the sample
            double omegaMagnitude = Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)

//            double EPSILON = 1.0;
//            if (omegaMagnitude > EPSILON) {
//                axisX /= omegaMagnitude;
//                axisY /= omegaMagnitude;
//                axisZ /= omegaMagnitude;
//            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            double thetaOverTwo = omegaMagnitude * dT / 2.0f;
            double sinThetaOverTwo = Math.sin(thetaOverTwo);
            double cosThetaOverTwo = Math.cos(thetaOverTwo);
            deltaRotationVector[0] = Float.parseFloat(Double.toString(sinThetaOverTwo * axisX));
            deltaRotationVector[1] = Float.parseFloat(Double.toString(sinThetaOverTwo * axisY));
            deltaRotationVector[2] = Float.parseFloat(Double.toString(sinThetaOverTwo * axisZ));
            deltaRotationVector[3] = Float.parseFloat(Double.toString(cosThetaOverTwo));
        }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        sensorTextView.setText(deltaRotationVector.toString());
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
    }
}

