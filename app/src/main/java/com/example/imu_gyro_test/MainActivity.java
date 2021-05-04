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

import java.math.BigDecimal;
import java.util.List;
import java.lang.Math;

public class MainActivity extends AppCompatActivity{

    TextView headerTextView;
    TextView sensorTextView;
    TextView header2TextView;
    TextView sensor2TextView;
    String accuracyText;
    List list;

    private long prevtimestamp;
    private SensorManager sm;
    private Sensor sensor;

    private float[] prevVal;
    private float[] currentRotation;
    private double[] currentVelo;
    private float[] prevVelo;

    private static final float NS2S = 1.0f / 1000000000.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get sensor instance
        SensorManager sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        sensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        //textViews
        headerTextView = (TextView)findViewById(R.id.headerTextView);
        sensorTextView = (TextView)findViewById(R.id.sensorTextView);
        header2TextView = (TextView)findViewById(R.id.header2TextView);
        sensor2TextView = (TextView)findViewById(R.id.sensor2TextView);

        prevtimestamp = 0;
        currentRotation = new float[]{ 0.0f, 0.0f, 0.0f };
        currentVelo = new double[]{ 0, 0, 0 };
        prevVelo = new float[]{ 0.0f, 0.0f, 0.0f };

        list = sm.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
        if(list.size()>0){

            sm.registerListener(sl, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_FASTEST);
            headerTextView.setText("Linear Accelerometer");

        }else {

            Toast.makeText(getBaseContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG).show();

        }

        list = sm.getSensorList(Sensor.TYPE_GYROSCOPE);
        if(list.size()>0){

            sm.registerListener(s2, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
            header2TextView.setText("GYROSCOPE");

        }else {
            Toast.makeText(getBaseContext(), "Error: No Gyroscope.", Toast.LENGTH_LONG).show();
        }

    }

    SensorEventListener sl = new SensorEventListener(){
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
                if (accuracy == 0) {
                    accuracyText = "Unreliable";
                }
                else if (accuracy == 1) {
                    accuracyText = "Low Accuracy";
                }
                else if (accuracy == 2) {
                    accuracyText = "Medium Accuracy";
                }
                else {
                    accuracyText = "High Accuracy";
                }
            }
            headerTextView.setText("Accuracy: " + accuracyText);
        }
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            sensorTextView.setText("x: "+values[0]+"\ny: "+values[1]+"\nz: "+values[2]);
        }
    };

    SensorEventListener s2 = new SensorEventListener(){
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (sensor.getType() == Sensor.TYPE_GYROSCOPE){
                if (accuracy == 0) {
                    accuracyText = "Unreliable";
                }
                else if (accuracy == 1) {
                    accuracyText = "Low Accuracy";
                }
                else if (accuracy == 2) {
                    accuracyText = "Medium Accuracy";
                }
                else {
                    accuracyText = "High Accuracy";
                }
            }
            header2TextView.setText("Accuracy: " + accuracyText);
        }
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            sensor2TextView.setText("x: "+values[0]+"\ny: "+values[1]+"\nz: "+values[2]);

            if(prevtimestamp == 0.0f){
                prevVal = values;
                prevtimestamp = event.timestamp;

            }else{
                final float timediff = (event.timestamp - prevtimestamp);
                values[0] = BigDecimal.valueOf(values[0] * (180/Math.PI)).floatValue();
                prevVal[0] = BigDecimal.valueOf(prevVal[0] * (180/Math.PI)).floatValue();
                currentVelo[0] = BigDecimal.valueOf(values[0]).subtract(BigDecimal.valueOf(prevVal[0])).multiply(BigDecimal.valueOf(timediff)).doubleValue();
//                currentVelo[1] += ((values[1] - prevVal[1]) * timediff)/2;
//                currentVelo[2] += ((values[2] - prevVal[2]) * timediff)/2;
//
//                currentRotation[0] += ((currentVelo[0] - prevVelo[0]) * timediff)/2;
//                currentRotation[1] += ((currentVelo[1] - prevVelo[1]) * timediff)/2;
//                currentRotation[2] += ((currentVelo[2] - prevVelo[2]) * timediff)/2;

                prevVal = values;
                //prevVelo = Float.parseFloat(currentVelo.toString());
                prevtimestamp = event.timestamp;
                String Text = "x: "+values[0]+"\ny: "+values[1]+"\nz: "+values[2] + "\nx: "+prevVal[0]+"\ny: "+currentRotation[1]+"\nz: "+currentRotation[2] + "\n" + currentVelo[0];
                sensor2TextView.setText(Text);
            }
        }
    };


//    private static final float NS2S = 1.0f / 1000000000.0f;
//    private final float[] deltaRotationVector = new float[4];
//    private float timestamp;
//
//    public void onSensorChanged(SensorEvent event) {
//
//        // This timestep's delta rotation to be multiplied by the current rotation
//        // after computing it from the gyro sample data.
//        if (timestamp != 0) {
//
//            final float dT = (event.timestamp - timestamp) * NS2S;
//
//            // Axis of the rotation sample, not normalized yet.
//            double axisX = Double.parseDouble(Float.toString(event.values[0]));
//            double axisY = Double.parseDouble(Float.toString(event.values[1]));
//            double axisZ =  Double.parseDouble(Float.toString(event.values[2]));;
//
//            // Calculate the angular speed of the sample
//            double omegaMagnitude = Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);
//
//            // Normalize the rotation vector if it's big enough to get the axis
//            // (that is, EPSILON should represent your maximum allowable margin of error)
//
////            double EPSILON = 1.0;
////            if (omegaMagnitude > EPSILON) {
////                axisX /= omegaMagnitude;
////                axisY /= omegaMagnitude;
////                axisZ /= omegaMagnitude;
////            }
//
//            // Integrate around this axis with the angular speed by the timestep
//            // in order to get a delta rotation from this sample over the timestep
//            // We will convert this axis-angle representation of the delta rotation
//            // into a quaternion before turning it into the rotation matrix.
//            double thetaOverTwo = omegaMagnitude * dT / 2.0f;
//            double sinThetaOverTwo = Math.sin(thetaOverTwo);
//            double cosThetaOverTwo = Math.cos(thetaOverTwo);
//            deltaRotationVector[0] = Float.parseFloat(Double.toString(sinThetaOverTwo * axisX));
//            deltaRotationVector[1] = Float.parseFloat(Double.toString(sinThetaOverTwo * axisY));
//            deltaRotationVector[2] = Float.parseFloat(Double.toString(sinThetaOverTwo * axisZ));
//            deltaRotationVector[3] = Float.parseFloat(Double.toString(cosThetaOverTwo));
//        }
//        timestamp = event.timestamp;
//        float[] deltaRotationMatrix = new float[9];
//        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
//        sensorTextView.setText(deltaRotationVector.toString());
//        // User code should concatenate the delta rotation we computed with the current rotation
//        // in order to get the updated rotation.
//        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
//    }

//    public void onSensorChanged(SensorEvent event){
//        // In this example, alpha is calculated as t / (t + dT),
//        // where t is the low-pass filter's time-constant and
//        // dT is the event delivery rate.
//
//        final float alpha = 0.8f;
//
//        // Isolate the force of gravity with the low-pass filter.
//        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
//        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
//        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
//
//        // Remove the gravity contribution with the high-pass filter.
//        linear_acceleration[0] = event.values[0] - gravity[0];
//        linear_acceleration[1] = event.values[1] - gravity[1];
//        linear_acceleration[2] = event.values[2] - gravity[2];
//    }

}

