package lift;

public class MyLiftController implements LiftController {
    private int floor = 0;
    private Direction dir = Direction.UNSET;
    private boolean doorsAreOpen = false;
    private int[] liftCalledUP = new int[9];
    private int[] liftCalledDOWN = new int[9];
    private int[] liftGOTOFloor = new int[9];

    /* Interface for People */
    public synchronized void callLift(int floor, Direction direction) throws InterruptedException {
        // Checks if lift has been called up or down
        if(direction == Direction.UP)
            liftCalledUP[floor]++;
        if(direction == Direction.DOWN)
            liftCalledDOWN[floor]++;
        notifyAll();
        // Waits till lift is at right floor and doors open
        while(!(this.floor == floor && doorsAreOpen && this.dir == direction))
            wait();
        // Checks if people have left the lift
        if(direction == Direction.UP)
            liftCalledUP[floor]--;
        if(direction == Direction.DOWN)
            liftCalledDOWN[floor]--;
    }

    public synchronized void selectFloor(int floor) throws InterruptedException {
        liftGOTOFloor[floor]++;
        notifyAll();
        // Waits till lift is open at the correct floor people exit
        while(!(doorsAreOpen && this.floor == floor))
            wait();
        liftGOTOFloor[floor]--;
    }

    /* Interface for Lifts */
    public synchronized boolean liftAtFloor(int floor, Direction direction) {
        this.floor = floor;
        this.dir = direction;
        // Open doors if lift has been called, or floor has been selected
        if(direction == Direction.UP && liftCalledUP[floor] > 0)
            return true;
        if(direction == Direction.DOWN && liftCalledDOWN[floor] > 0)
            return true;
        if(liftGOTOFloor[floor] > 0)
            return true;
        return false;
    }

    public synchronized void doorsOpen(int floor) throws InterruptedException {
        doorsAreOpen = true;
        notifyAll();
        if(dir == Direction.UP)
            while(liftCalledUP[floor] > 0 || liftGOTOFloor[floor] > 0)
                wait();
        else if(dir == Direction.DOWN)
            while(liftCalledDOWN[floor] > 0 || liftGOTOFloor[floor] > 0)
                wait();
    }

    public synchronized void doorsClosed(int floor) {
        doorsAreOpen = false;
    }
}
