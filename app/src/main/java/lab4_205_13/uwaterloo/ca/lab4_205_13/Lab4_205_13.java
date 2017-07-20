package lab4_205_13.uwaterloo.ca.lab4_205_13;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class Lab4_205_13 extends AppCompatActivity {
    // 2D array to store last 100 readings
    float[][] readings = new float[100][3];
    // Size of GAMEBOARD for screen 1440,2560
    int GAMEBOARD_DIMENSION = 1440;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_205_13);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Create RelativeLayout
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.label1);
        rl.getLayoutParams().height = GAMEBOARD_DIMENSION;
        rl.getLayoutParams().width = GAMEBOARD_DIMENSION;
        rl.setBackgroundResource(R.drawable.gameboard);

        // FSM Output
        TextView tv1 = (TextView) findViewById(R.id.label2);
        tv1.setTextColor(Color.parseColor("#0000FF"));

        TextView state = (TextView) findViewById(R.id.label3);

        // GameLoopTask
        Timer myGameLoop = new Timer();
        final GameLoopTask myGameLoopTask = new GameLoopTask(this,rl,getApplicationContext(),state);
        myGameLoop.schedule(myGameLoopTask, 50, 50); //50ms periodic timer –20fps

        // Accelerometer Sensor
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        final SensorEventListener l1 = new AccelerometerEventListener(tv1, readings, myGameLoopTask);
        sensorManager.registerListener(l1, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

        Button LeftButton = (Button) findViewById(R.id.LeftButton);
        LeftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.LEFT); //calls the function that prints the array of accelerometer readings to the array
            }
        });
        Button DownButton = (Button) findViewById(R.id.DownButton);
        DownButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.DOWN); //calls the function that prints the array of accelerometer readings to the array
            }
        });
        Button UpButton = (Button) findViewById(R.id.UpButton);
        UpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.UP); //calls the function that prints the array of accelerometer readings to the array
            }
        });
        Button RightButton = (Button) findViewById(R.id.RightButton);
        RightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myGameLoopTask.setDirection(GameLoopTask.gameDirection.RIGHT); //calls the function that prints the array of accelerometer readings to the array
            }
        });




    }
}

