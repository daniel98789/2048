package lab4_205_13.uwaterloo.ca.lab4_205_13;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TimerTask;

public class GameLoopTask extends TimerTask{
    // Direction block in moving
    public enum gameDirection {UP, DOWN, LEFT, RIGHT, NO_MOVEMENT};
    public gameDirection currentGameDirection = gameDirection.NO_MOVEMENT;
    // MainActivity
    private Activity myActivity;
    // Context
    private Context myContext;
    // Relative Layout
    private RelativeLayout myRL;
    // LinkedList of Blocks
    public LinkedList<GameBlock> myGBList;
    public LinkedList<GameBlock> myRemoveList;
    //Coordinates of Grid
    private static int[][] coordinates = new int[][] {{-45,315,675,1035},{-45,315,675,1035}};
    // If block is not moving
    private boolean stopped = true;
    // If grid is filled
    private boolean[][] boolArray = new boolean[4][4];
    private boolean finishedMoving = true;
    private boolean changedDirection = false;
    private int i = -1;
    private boolean moved;

    enum gamestates {LOST, NOT_YET, WON};
    gamestates gamestate = gamestates.NOT_YET;

    public TextView state;

    public GameLoopTask(Activity ActivityIn, RelativeLayout rlIn, Context contextIn, TextView statein) {
        myActivity = ActivityIn;
        myContext = contextIn;
        myRL = rlIn;
        myGBList = new LinkedList<>();
        myRemoveList = new LinkedList<>();
        createBlock();
        state = statein;
        state.setText(String.valueOf(gamestate));
        state.setTextColor(Color.BLACK);
        state.setTextSize(30);
    }

    public void run() {
        myActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (GameBlock block: myGBList) {
                            block.move();
                            if (block.blockNumber == 64) { // check for win conditoin
                                gamestate = gamestates.WON;
                                state.setText(String.valueOf(gamestate));
                            }
                            if (lostCheck()) { // check for lost conditoin
                                gamestate = gamestates.LOST;
                                state.setText(String.valueOf(gamestate));
                            }
                        }
                        if (changedDirection) {
                            finishedMoving = true;
                            for (GameBlock block1: myGBList) { // check if all blocks have stopped moving
                                finishedMoving = finishedMoving && (block1.myDir == gameDirection.NO_MOVEMENT);
                            }
                            if (finishedMoving) { // if stopped create a new block and remove all merged blocks
                                for (GameBlock block1 : myRemoveList) {
                                    block1.delete = true; // delete block from linkedlist and set it pointer to null for garbage collection
                                    block1.remove();
                                    myGBList.remove(block1);
                                    block1 = null;
                                }
                                myRemoveList = new LinkedList<>();
                                if (moved){ // gerated blcok only if blocks have moved or merged
                                    createBlock();
                                }

                                changedDirection = false;
                                moved = false;
                            }
                        }
                    }
                }
        );
    }

    public boolean isOccupied(int x, int y){ // check if a cell is occupied by a block
        int coor[];
        for (GameBlock block: myGBList) {
            coor = block.getCoordinates();
            if (coor[0] == x && coor[1] == y){
                return true;
            }
        }
        return false;
    }

    private void createBlock() {
        int x = 0,y = 0, x1,y1;
        boolArray = new boolean[4][4];
        for (GameBlock block: myGBList) { // check grid for already occupied cells
            if (block.myCoordX == coordinates[0][0]) x = 0;
            else if (block.myCoordX == coordinates[0][1]) x = 1;
            else if (block.myCoordX == coordinates[0][2]) x = 2;
            else if (block.myCoordX == coordinates[0][3]) x = 3;

            if (block.myCoordY == coordinates[1][0]) y = 0;
            else if (block.myCoordY == coordinates[1][1]) y = 1;
            else if (block.myCoordY == coordinates[1][2]) y = 2;
            else if (block.myCoordY == coordinates[1][3]) y = 3;
            boolArray[x][y] = true;
        }

        Integer[] arr = new Integer[4]; //random number generator
        Integer[] arr1 = new Integer[4];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
            arr1[i] = i;
        }
        Collections.shuffle(Arrays.asList(arr));
        Collections.shuffle(Arrays.asList(arr1));

       for ( x1 = 0; x1 < 4; x1++) { // generate block on empty cell
            for (y1 =0; y1 < 4 ; y1++) {
                x = arr[x1];
                y = arr1[y1];
                if (!boolArray[x][y]) {
                    break;
                }
            }
           if (!boolArray[x][y] ) {
               break;
           }
        }
        if (gamestate != gamestates.LOST) { // dont generate block if you have lost
            GameBlock newBlock = new GameBlock(myContext,coordinates[0][x],coordinates[1][y],myRL);
            myGBList.add(newBlock);
        }

    }

    private boolean mergeAlg(int a, int[] arr ) {
        // a = current block index
        boolean mergable = false;
        //Log.d("a",Integer.toString(a));
        Log.d("lookahead",Arrays.toString(arr));
        for (i = (a+1); i <= 3; i++) { // look for the next block on row or column and see if it is the same number
            //Log.d("mergable",String.valueOf(mergable));
            if (arr[i] != 0) {
                if (arr[a] == arr[i]) {
                    //Log.d("adsfe","sdf");
                    mergable = true;
                    for (int j = (i+1); j<=3; j++) {// check if that block will merge will another block
                        if (arr[j] != 0) {
                            if (arr[i] == arr[j]) {
                                //Log.d("adsfe","sdf");
                                mergable = false;
                                break;
                            } else {
                                mergable = true;
                                break;
                            }
                        }
                        //Log.d("mergable",String.valueOf(mergable));
                    }
                    if (mergable == false)  break;
                }
                else {
                    mergable = false;
                    break;
                }
            }
            if (mergable == true)  break;
        }
        if (arr[0] == arr[1] && arr[2] == arr[3] && arr[0] != 0 && arr[1] != 0 && arr[2] != 0 && arr[3] != 0) { //special case
            if (a == 0 ) {
                i = 1;
                mergable = true;
            }
            if (a == 2 ) {
                i = 3;
                mergable = true;
            }
        }
        if (!mergable) {
            i = -1;
        }

        return mergable;
    }

    private int numMergeAhead(int a, int[] arr ) { // check fro how many block ahead can be merged
        int numMerge = 0;
        int k;
        while (a <= 3) {
            boolean mergable = false;
            for (k = (a+1); k <= 3; k++) {
                if (arr[k] != 0) {
                    if (arr[a] == arr[k]) {
                        mergable = true;
                        for (int j = (k+1); j<=3; j++) {
                            if (arr[j] != 0) {
                                if (arr[k] == arr[j]) {
                                    mergable = false;
                                    break;
                                } else {
                                    mergable = true;
                                    break;
                                }
                            }
                        }
                        if (mergable == false)  break;
                    }
                    else {
                        mergable = false;
                        break;
                    }
                }
                if (mergable == true)  break;
            }
            if (arr[0] == arr[1] && arr[2] == arr[3] && arr[0] != 0 && arr[1] != 0 && arr[2] != 0 && arr[3] != 0) {
                if (a == 0 ) {
                    k = 1;
                    mergable = true;
                }
                if (a == 0 ) {
                    k = 4;
                    mergable = true;
                }
            }
            if (!mergable) {
                k = -1;
            }

            if (mergable) {
                numMerge++;
            }
            a++;
            //log.d("nummergable",String.valueOf(numMerge));

        }
        return numMerge;
    }
    public boolean lostCheck() {// check for lost condition
        int x=0,y=0;
        int arr[][] = new int[4][4];
        for (GameBlock block1 : myGBList) { // put current gameboard into array by block number
            if (block1.myCoordX == coordinates[0][0]) x = 0;
            else if (block1.myCoordX == coordinates[0][1]) x = 1;
            else if (block1.myCoordX == coordinates[0][2]) x = 2;
            else if (block1.myCoordX == coordinates[0][3]) x = 3;

            if (block1.myCoordY == coordinates[1][0]) y = 0;
            else if (block1.myCoordY == coordinates[1][1]) y = 1;
            else if (block1.myCoordY == coordinates[1][2]) y = 2;
            else if (block1.myCoordY == coordinates[1][3]) y = 3;
            arr[x][y] = block1.blockNumber;
        }

        boolean pairFound = false;
        boolean full = true;

        for(int i = 0; i < 4 && !pairFound; i++){ // check if gameboard is full
            for(int j = 0; j < 4 && !pairFound; j++){
                if (arr[i][j] == 0) {
                    full = false;

                }
            }
        }

        if (full == true) {
            for (int i = 0; i < 4 && !pairFound; i++) { // check if all blocks and merge with adjacent blocks
                for (int j = 0; j < 4 && !pairFound; j++) {
                    if (i > 0 && arr[i - 1][j] == arr[i][j])
                        pairFound = true;
                    else if (i < 3 && arr[i + 1][j] == arr[i][j])
                        pairFound = true;
                    else if (j > 0 && arr[i][j - 1] == arr[i][j])
                        pairFound = true;
                    else if (j < 3 && arr[i][j + 1] == arr[i][j])
                        pairFound = true;

                }
            }
        }
        else pairFound= true;

        if(!pairFound) {
            gamestate = gamestates.LOST;
            return (true);
        }
        else return false;
    }

    public void setDirection(gameDirection newDirection) {
        int arr1[][] = new int[4][4];
        int x1=0,y1=0;
        stopped = true;
        for (GameBlock block1: myGBList) { // check if all block have finished moving
            stopped = stopped && (block1.myDir == gameDirection.NO_MOVEMENT);
        }
        if (stopped) { // if stopped then input new direction
            moved = false;
            currentGameDirection = newDirection;
            for (GameBlock block1 : myGBList) { // put current gameboard into array by block number
                if (block1.myCoordX == coordinates[0][0]) x1 = 0;
                else if (block1.myCoordX == coordinates[0][1]) x1 = 1;
                else if (block1.myCoordX == coordinates[0][2]) x1 = 2;
                else if (block1.myCoordX == coordinates[0][3]) x1 = 3;

                if (block1.myCoordY == coordinates[1][0]) y1 = 0;
                else if (block1.myCoordY == coordinates[1][1]) y1 = 1;
                else if (block1.myCoordY == coordinates[1][2]) y1 = 2;
                else if (block1.myCoordY == coordinates[1][3]) y1 = 3;
                arr1[x1][y1] = block1.blockNumber;
            }

            for (GameBlock block : myGBList) { // move every block on gameboard

                int blockCount = 0;
                int slotCount = 0;
                int coord[]= block.getCoordinates();
                int targetX = coord[0];
                int targetY = coord[1];
                int x,y;
                int arr[] = new int[4];
                int a=0 , b=0;
                boolean mergable= false;
                boolean deletable= false;
                int numMerge = 0;

                if (block.myCoordX == coordinates[0][0]) a = 0;
                else if (block.myCoordX == coordinates[0][1]) a = 1;
                else if (block.myCoordX == coordinates[0][2]) a = 2;
                else if (block.myCoordX == coordinates[0][3]) a = 3;

                if (block.myCoordY == coordinates[1][0]) b = 0;
                else if (block.myCoordY == coordinates[1][1]) b = 1;
                else if (block.myCoordY == coordinates[1][2]) b = 2;
                else if (block.myCoordY == coordinates[1][3]) b = 3;

                if (newDirection == gameDirection.LEFT) { //
                    x = 0;
                    targetX = coordinates[0][x];
                    while (targetX != coord[0]) { // check for slot count and block count
                        if (isOccupied(targetX,coord[1])){
                            blockCount++;
                        }
                        slotCount++;
                        x++;
                        if (x > 3){
                            x = 3;
                            break;
                        }
                        targetX = coordinates[0][x];
                    }
                    arr[0] = arr1[3][b];
                    arr[1] = arr1[2][b];
                    arr[2] = arr1[1][b];
                    arr[3] = arr1[0][b];
                    mergable = mergeAlg(3-a,arr); // check if it can merge
                    numMerge = numMergeAhead(3-a+1,arr); // check how many blocks ahead can merge

                    if (mergable && i != -1) { // change block number if it can be merged
                        for (GameBlock block1 : myGBList) {
                            // Log.d("xcord",Integer.toString(coordinates[0][i]));
                            //Log.d("ycord",Integer.toString(coordinates[1][b]));
                            if (block1.myCoordX == coordinates[0][3-i]&& block1.myCoordY == coordinates[1][b]) {
                                //Log.d("sfds","asdfsa");
                                block1.changeNumber(block.getNumber());
                            }
                        }
                    }
                    targetX = coordinates[0][x+(blockCount-slotCount)-numMerge]; // move by total slot count + merageble blocks ahead - block count
                    targetY = coord[1];
                    if ((x+(blockCount-slotCount)-numMerge) != a  || mergable) {
                        moved = true;
                    }
                    block.leftX = targetX;
                    block.setBlockDirection(currentGameDirection);
                }

                else if (newDirection == gameDirection.RIGHT) {
                    x = 3;
                    targetX = coordinates[0][x];
                    while (targetX != coord[0]) {
                        arr[3-x] = 0;
                        if (isOccupied(targetX,coord[1])){
                            blockCount++;
                            arr[3-x] = arr1[x][b];
                        }
                        slotCount++;
                        x--;
                        if (x < 0 ){
                            x = 0;
                            break;
                        }
                        targetX = coordinates[0][x];

                    }
                    arr[0] = arr1[0][b];
                    arr[1] = arr1[1][b];
                    arr[2] = arr1[2][b];
                    arr[3] = arr1[3][b];

                    mergable = mergeAlg(a,arr);
                    numMerge = numMergeAhead(a+1,arr);
                    if (mergable && i != -1) {
                        for (GameBlock block1 : myGBList) {
                           // Log.d("xcord",Integer.toString(coordinates[0][i]));
                            //Log.d("ycord",Integer.toString(coordinates[1][b]));
                            if (block1.myCoordX == coordinates[0][i]&& block1.myCoordY == coordinates[1][b]) {
                                //Log.d("sfds","asdfsa");
                                block1.changeNumber(block.getNumber());
                            }
                        }
                    }
                    targetX = coordinates[0][x-(blockCount-slotCount)+numMerge];
                    targetY = coord[1];
                    block.rightX = targetX;
                    if ((x-(blockCount-slotCount)+numMerge) != a  || mergable) {
                        moved = true;
                    }
                    block.setBlockDirection(currentGameDirection);
                }
                else if (newDirection == gameDirection.UP) {
                    y = 0;
                    targetY = coordinates[1][y];
                    while (targetY != coord[1]) {
                        arr[y] = 0;
                        if (isOccupied(coord[0],targetY)){
                            blockCount++;
                            arr[y] = arr1[a][y];
                        }
                        slotCount++;
                        y++;
                        if (y > 3){
                            y = 3;
                            break;
                        }
                        targetY = coordinates[1][y];

                    }
                    arr[0] = arr1[a][3];
                    arr[1] = arr1[a][2];
                    arr[2] = arr1[a][1];
                    arr[3] = arr1[a][0];

                    mergable = mergeAlg(3-b,arr);
                    numMerge = numMergeAhead(3-b+1,arr);
                    if (mergable && i != -1) {
                        for (GameBlock block1 : myGBList) {
                            // Log.d("xcord",Integer.toString(coordinates[0][i]));
                            //Log.d("ycord",Integer.toString(coordinates[1][b]));
                            if (block1.myCoordX == coordinates[0][a]&& block1.myCoordY == coordinates[1][3-i]) {
                                //Log.d("sfds","asdfsa");
                                block1.changeNumber(block.getNumber());
                            }
                        }
                    }
                    targetY = coordinates[1][y+(blockCount-slotCount)-numMerge];
                    targetX = coord[0];
                    if ((y+(blockCount-slotCount)-numMerge) != b  || mergable) {
                        moved = true;
                    }
                    block.upY = targetY;
                    block.setBlockDirection(currentGameDirection);
                }
                else if (newDirection == gameDirection.DOWN) {
                    y = 3;
                    targetY = coordinates[0][y];
                    while (targetY != coord[1]) {
                        arr[3-y] = 0;
                        if (isOccupied(coord[0],targetY)){
                            blockCount++;
                            arr[3-y] = arr1[a][y];
                        }
                        slotCount++;
                        y--;
                        if (y < 0){
                            y = 0;
                            break;
                        }
                        targetY = coordinates[1][y];
                    }
                    arr[0] = arr1[a][0];
                    arr[1] = arr1[a][1];
                    arr[2] = arr1[a][2];
                    arr[3] = arr1[a][3];

                    mergable = mergeAlg(b,arr);
                    numMerge = numMergeAhead(b+1,arr);
                    if (mergable && i != -1) {
                        for (GameBlock block1 : myGBList) {
                            if (block1.myCoordX == coordinates[0][a]&& block1.myCoordY == coordinates[1][i]) {
                                block1.changeNumber(block.getNumber());
                            }
                        }
                    }
                    targetY = coordinates[1][y-(blockCount-slotCount)+numMerge];
                    targetX = coord[0];
                    if ((y-(blockCount-slotCount)+numMerge) != b || mergable) {
                        moved = true;
                    }
                    block.downY = targetY;
                    block.setBlockDirection(currentGameDirection);
                }

                Log.d("arr1", Arrays.deepToString(arr1));
                Log.d("a",Integer.toString(a));
                Log.d("b",Integer.toString(b));
                Log.d("nummergable",String.valueOf(numMerge));
                Log.d("mergable",String.valueOf(mergable));
                Log.d("i",Integer.toString(i));
                Log.d("slotcount",Integer.toString(slotCount));
                Log.d("blockcount",Integer.toString(blockCount));
                Log.d("TargetX",Integer.toString(targetX));
                Log.d("TargetY",Integer.toString(targetY));
                Log.d("moved",String.valueOf(moved));

                changedDirection = true;
                if (mergable){ // add merged block into a list of blocks that needs to be deleted
                    myRemoveList.add(block);

                }
            }

        }

    }
}