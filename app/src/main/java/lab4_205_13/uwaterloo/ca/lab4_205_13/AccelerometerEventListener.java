package lab4_205_13.uwaterloo.ca.lab4_205_13;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

public class AccelerometerEventListener implements SensorEventListener {
    //Low Pass Filter Constant
    private final float C = 20;
    //Direction Output from FSM
    private TextView FSMOutput;
    //Array to store last 100 readings
    private float[][] reading;
    //Finite State Machine
    private myFSM FSM;
    //GameLoopTask object
    private GameLoopTask myGameLoopTask1;

    public AccelerometerEventListener(TextView outputView1 ,  float[][] readings, GameLoopTask currentGameLoopTask) {
        FSMOutput = outputView1;
        reading = readings;
        myGameLoopTask1 = currentGameLoopTask;
        FSM = new myFSM(FSMOutput, myGameLoopTask1);
    }
    private void addReadings(float[] values) {
        //First in First out
        for (int i = 1; i < 100; i++) {
            reading[i - 1][0] = reading[i][0];
            reading[i - 1][1] = reading[i][1];
            reading[i - 1][2] = reading[i][2];
        }
        //Low pass filter
        reading[99][0] += (values[0] - reading[99][0]) / C;
        reading[99][1] += (values[1] - reading[99][1]) / C;
        reading[99][2] += (values[2] - reading[99][2]) / C;
    }
    public void onAccuracyChanged(Sensor s, int i) {
    }
    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            //Adds readings to array
            addReadings(se.values);
            // Feed readings to FSM
            FSM.activateFSM(reading[99][0], reading[99][1] );
        }
    }
}