package sample.animals;


import sample.grid.FieldGid;

import java.util.LinkedList;
import java.util.List;

public class Fox extends Thread implements Animal {

    private LinkedList<int[]> nextSteps = new LinkedList<int[]>();
    private FieldGid fieldGid;
    private int positionX;
    private int positionY;
    private boolean alive = true;
    private int pointsCounter;

    public Fox(FieldGid fieldGid) {
        this.fieldGid = fieldGid;
        pointsCounter = 10;
        alive = true;
    }


    public LinkedList<int[]> getNextSteps() {
        return nextSteps;
    }

    public void setNextSteps(LinkedList<int[]> nextSteps) {
        this.nextSteps = nextSteps;
    }

    public FieldGid getFieldGid() {
        return fieldGid;
    }

    public void setFieldGid(FieldGid fieldGid) {
        this.fieldGid = fieldGid;
    }


    public int getPointsCounter() {
        return pointsCounter;
    }

    public void setPointsCounter(int pointsCounter) {
        this.pointsCounter = pointsCounter;
    }

    @Override
    public void run() {
        super.run();

        while (alive) {
            try {
                Thread.sleep(500);

                if (!nextSteps.isEmpty() && pointsCounter > 0) {

                    int[] peekNextSteps = nextSteps.peek();
                    if (fieldGid.isFreePosition(peekNextSteps[0], peekNextSteps[1])) {
                        fieldGid.moveFromTo(this, positionX, positionY, peekNextSteps[0], peekNextSteps[1]);
                        pointsCounter -= 1;
                        nextSteps.pop();
                    } else if (fieldGid.isRabbitPosition(peekNextSteps[0], peekNextSteps[1])) {
                        pointsCounter += 20;
                        fieldGid.killRabbit(peekNextSteps[0], peekNextSteps[1]);
                        nextSteps = new LinkedList<int[]>();
                    } else if (fieldGid.isFoxPosition(peekNextSteps[0], peekNextSteps[1])) {
                        pointsCounter -= 1;
                        nextSteps = new LinkedList<int[]>();
                        int[] rabbitPosition = fieldGid.getRandomRabbitPosition();
                        nextSteps.addAll(fieldGid.getShortestPath(new int[]{positionX, positionY}, rabbitPosition));
                        nextSteps.pop();

                    }

                } else if (pointsCounter == 0) {
                    alive = false;
                    break;
                } else {
                    nextSteps = new LinkedList<int[]>();
                    int[] rabbitPosition = fieldGid.getRandomRabbitPosition();
                    LinkedList<int[]> shortestPath = fieldGid.getShortestPath(new int[]{positionX, positionY}, rabbitPosition);
                    nextSteps.addAll(shortestPath);
                }

                fieldGid.getSemaphore().release();

            } catch (Exception ignored) {
                ignored.printStackTrace();
            } finally {
                //System.out.println(this);
            }

        }
fieldGid.foxDie(positionX,positionY);
    }


    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }


    @Override
    public String toString() {
        return "Fox{" +
                "nextSteps=" + nextSteps +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                '}';
    }
}
