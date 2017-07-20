package lab4_205_13.uwaterloo.ca.lab4_205_13;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class GameBlock extends GameBlockTemplate{
    // Block Scale
    private float IMAGE_SCALE = 0.65f;
    // Current block x coordinate
    public int myCoordX;
    // Current block y coordinate
    public int myCoordY;
    // Stores direction block is moving in
    public GameLoopTask.gameDirection myDir = GameLoopTask.gameDirection.NO_MOVEMENT;
    // The block cannot move past this coordinate
    public int leftX ;
    public int rightX;
    public int upY;
    public int downY;
    // Block acceleration
    private static float accel = 100.0f;
    // Block Velocity
    private static float velocity = 0f;

    public boolean delete = false;

    private TextView NumOutput = (TextView) findViewById(R.id.label2);

    public int blockNumber;
    private Random rand = new Random();
    private RelativeLayout myRL;
    private int offsetx = 180;
    private int offsety = 90;

    public GameBlock(Context myContext, int coordX, int coordY, RelativeLayout myRL) {
        super(myContext);
        this.myRL = myRL;
        myCoordX = coordX;
        myCoordY = coordY;
        this.setImageResource(R.drawable.gameblock);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.setX(myCoordX);
        this.setY(myCoordY);
        blockNumber =(rand.nextInt(2) + 1)*2;
        NumOutput = new TextView(myContext);
        NumOutput.setText(Integer.toString(blockNumber));
        NumOutput.setTextColor(Color.BLACK);
        NumOutput.setTextSize(50);
        NumOutput.setX(myCoordX+offsetx);
        NumOutput.setY(myCoordY+offsety);
        NumOutput.bringToFront();
        myRL.addView(this);
        myRL.addView(NumOutput);



    }

    public void setBlockDirection(GameLoopTask.gameDirection newDir) {
        myDir = newDir;
    }

    public int[] getCoordinates() {
        int[] coordinates = new int[] {myCoordX,myCoordY};
        return(coordinates);
    }

    public void remove(){
        myRL.removeView(this);
        myRL.removeView(NumOutput);
    }

    public void move(){
        switch(myDir) {
            case LEFT:
                // Move the current coordinate until it equals the left side boundary coordinate
                if (myCoordX > leftX) {
                    // Each move by velocity
                    myCoordX = myCoordX - Math.round(velocity);
                    // increase velocity
                    velocity = velocity + accel;
                    // Check if the block moved past the boundaries
                    // Make it equal the left side boundary if it moved past
                    if (myCoordX <= leftX) {
                        myCoordX = leftX;
                    }
                    this.setX(myCoordX);
                    NumOutput.setX(myCoordX+offsetx);
                }
                else if (myCoordX <= leftX) {
                    myCoordX = leftX;
                    NumOutput.setX(myCoordX+offsetx);
                    this.setX(myCoordX);
                    velocity = 0f;
                    myDir = GameLoopTask.gameDirection.NO_MOVEMENT;
                }
                break;
            case RIGHT:
                if (myCoordX < rightX) {
                    myCoordX = myCoordX + Math.round(velocity);
                    velocity = velocity + accel;
                    if (myCoordX >= rightX) {
                        myCoordX = rightX;
                    }
                    this.setX(myCoordX);
                    NumOutput.setX(myCoordX+offsetx);
                }
                else if (myCoordX >= rightX) {
                    myCoordX = rightX;
                    NumOutput.setX(myCoordX+offsetx);
                    this.setX(myCoordX);
                    velocity = 0f;
                    myDir = GameLoopTask.gameDirection.NO_MOVEMENT;
                }
                break;
            case UP:
                if (myCoordY > upY) {
                    myCoordY = myCoordY - Math.round(velocity);
                    velocity = velocity + accel;
                    if (myCoordY <= upY) {
                        myCoordY = upY;
                    }
                    NumOutput.setY(myCoordY+offsety);
                    this.setY(myCoordY);
                }
                else if (myCoordY <= upY) {
                    myCoordY = upY;
                    NumOutput.setY(myCoordY+offsety);
                    this.setY(myCoordY);
                    velocity = 0f;
                    myDir = GameLoopTask.gameDirection.NO_MOVEMENT;
                }
                break;
            case DOWN:
                if (myCoordY < downY) {
                    myCoordY = myCoordY + Math.round(velocity);
                    velocity = velocity + accel;
                    if (myCoordY >= downY) {
                        myCoordY = downY;
                    }
                    NumOutput.setY(myCoordY+offsety);
                    this.setY(myCoordY);
                }
                else if (myCoordY >= downY) {
                    myCoordY = downY;
                    NumOutput.setY(myCoordY+offsety);
                    this.setY(myCoordY);
                    velocity = 0f;
                    myDir = GameLoopTask.gameDirection.NO_MOVEMENT;
                }
                break;
            case NO_MOVEMENT:
                break;
            default:
                break;
        }

    }

    public void changeNumber(int addNumber){
        blockNumber += addNumber;
        NumOutput.setText(Integer.toString(blockNumber));
    }

    public int getNumber(){
        return blockNumber;
    }
}
