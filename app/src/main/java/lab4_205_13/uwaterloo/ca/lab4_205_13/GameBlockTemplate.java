package lab4_205_13.uwaterloo.ca.lab4_205_13;

import android.content.Context;
import android.widget.ImageView;

abstract public class GameBlockTemplate extends ImageView {
    public abstract void setBlockDirection(GameLoopTask.gameDirection newDir);
    public abstract void move();
    GameBlockTemplate(Context myContext) {super(myContext);}
}
