package lab4_205_13.uwaterloo.ca.lab4_205_13;
import android.widget.TextView;

public class myFSM {
    //FSM parameters
    enum FSMStates{WAIT, RISE, FALL, UP, DOWN, STABLE_RIGHT, STABLE_LEFT, STABLE_UP, STABLE_DOWN, DETERMINED}
    private FSMStates States;
    //Direction parameters
    enum Directions{LEFT, RIGHT, UP, DOWN, UNDETERMINED}
    private Directions Sig;
    //1st threshold: minimum Slope of the response onset
    //2nd threshold: the maximum response amplitude of the first peak
    //3rd threshold: the maximum response amplitude after settling for 40 samples.
    private final float[] THRESHOLD_RIGHT = {0.2f, 0.6f, 0.5f};
    private final float[] THRESHOLD_LEFT = {-0.2f, -0.6f, -0.5f};
    private final float[] THRESHOLD_UP = {0.2f, 0.6f, 0.5f};
    private final float[] THRESHOLD_DOWN = {-0.2f, -0.6f, -0.5f};

    //Sample counter.
    private int Counter;
    private final int CounterValue = 40;

    //Historical reading
    private float lastReadingX;
    private float lastReadingY;

    //TextView from the layout.
    private TextView TV;
    private GameLoopTask myGameLoopTask;

    //Constructor. Starts in WAIT state.
    public myFSM(TextView TVin, GameLoopTask currentGameLoopTask){
        States = FSMStates.WAIT;
        Sig = Directions.UNDETERMINED;
        Counter = CounterValue;
        lastReadingX = 0;
        lastReadingY = 0;
        TV = TVin;
        myGameLoopTask = currentGameLoopTask;
    }

    //Resets FSM
    public void resetFSM(){
        States = FSMStates.WAIT;
        Sig = Directions.UNDETERMINED;
        Counter = CounterValue;
        lastReadingX = 0;
        lastReadingY = 0;
    }

    public void activateFSM(float InputX, float InputY){
        //Calculate slope
        float SlopeX = InputX - lastReadingX;
        float SlopeY = InputY - lastReadingY;

        switch(States) {
            case WAIT:
                //Make the first guess on the direction based on slope
                if (SlopeX >= THRESHOLD_RIGHT[0] && SlopeY < THRESHOLD_UP[0] && SlopeY > THRESHOLD_DOWN[0]) {
                    States = FSMStates.RISE;
                }
                else if (SlopeX <= THRESHOLD_LEFT[0] && SlopeY < THRESHOLD_UP[0] && SlopeY > THRESHOLD_DOWN[0]) {
                    States = FSMStates.FALL;
                }
                else if (SlopeY >= THRESHOLD_UP[0] && SlopeX < THRESHOLD_RIGHT[0] && SlopeX > THRESHOLD_LEFT[0]) {
                    States = FSMStates.UP;
                }
                else if (SlopeY <= THRESHOLD_DOWN[0] && SlopeX < THRESHOLD_RIGHT[0] && SlopeX > THRESHOLD_LEFT[0]) {
                    States = FSMStates.DOWN;
                }
                break;

            case RISE:
                //Confirm direction
                if (SlopeX <= 0) {
                    if (lastReadingX >= THRESHOLD_RIGHT[1]) {
                        States = FSMStates.STABLE_RIGHT;
                        Sig = Directions.RIGHT;
                    }
                    else {
                        States = FSMStates.DETERMINED;
                        Sig = Directions.UNDETERMINED;
                    }
                }
                break;
            case FALL:
                if (SlopeX >= 0) {
                    if (lastReadingX <= THRESHOLD_LEFT[1]) {
                        States = FSMStates.STABLE_LEFT;
                        Sig = Directions.LEFT;
                    }
                    else {
                        States = FSMStates.DETERMINED;
                        Sig = Directions.UNDETERMINED;
                    }
                }
                break;
            case UP:
                if (SlopeY <= 0) {
                    if (lastReadingY >= THRESHOLD_UP[1]) {
                        States = FSMStates.STABLE_UP;
                        Sig = Directions.UP;
                    }
                    else {
                        States = FSMStates.DETERMINED;
                        Sig = Directions.UNDETERMINED;
                    }
                }
                break;
            case DOWN:
                if (SlopeY >= 0) {
                    if (lastReadingY <= THRESHOLD_DOWN[1]) {
                        States = FSMStates.STABLE_DOWN;
                        Sig = Directions.DOWN;
                    }
                    else {
                        States = FSMStates.DETERMINED;
                        Sig = Directions.UNDETERMINED;
                    }
                }
                break;

            case STABLE_RIGHT:
                //Wait for the stabilization.
                Counter--;

                //Once stabilized recheck direction
                if(Counter == 0){
                    States = FSMStates.DETERMINED;
                    if(Math.abs(InputX) < THRESHOLD_RIGHT[2]){
                        Sig = Directions.RIGHT;
                    }
                    else{
                        Sig = Directions.UNDETERMINED;
                    }
                }
                break;

            case STABLE_LEFT:
                Counter--;
                if(Counter == 0){
                    States = FSMStates.DETERMINED;

                    if(Math.abs(InputX) > THRESHOLD_LEFT[2]){
                        Sig = Directions.LEFT;
                    }
                    else{
                        Sig = Directions.UNDETERMINED;
                    }
                }
                break;

            case STABLE_UP:
                Counter--;
                if(Counter == 0){
                    States = FSMStates.DETERMINED;

                    if(Math.abs(InputY) < THRESHOLD_UP[2]){
                        Sig = Directions.UP;
                    }
                    else{
                        Sig = Directions.UNDETERMINED;
                    }
                }
                break;

            case STABLE_DOWN:
                Counter--;
                if(Counter == 0){
                    States = FSMStates.DETERMINED;

                    if(Math.abs(InputY) > THRESHOLD_DOWN[2]){
                        Sig = Directions.DOWN;
                    }
                    else{
                        Sig = Directions.UNDETERMINED;
                    }
                }
                break;

            case DETERMINED:
                //Display the signature
                TV.setText(Sig.toString());
                if (Sig == Directions.UP) myGameLoopTask.setDirection(GameLoopTask.gameDirection.UP);
                else if (Sig == Directions.DOWN) myGameLoopTask.setDirection(GameLoopTask.gameDirection.DOWN);
                else if (Sig == Directions.LEFT) myGameLoopTask.setDirection(GameLoopTask.gameDirection.LEFT);
                else if (Sig == Directions.RIGHT) myGameLoopTask.setDirection(GameLoopTask.gameDirection.RIGHT);
                //Reset FSM
                resetFSM();
                break;

            default:
                resetFSM();
                break;
        }
        //Keeps track of last reading
        lastReadingX = InputX;
        lastReadingY = InputY;

    }

}