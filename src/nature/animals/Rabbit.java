package sample.animals;


import sample.grid.FieldGid;

public class Rabbit extends Thread implements Animal {

    private final int rabbitId;
    FieldGid fieldGid;
    int positionX;
    int positionY;
    private boolean alive;

    public Rabbit(int rabbitId, FieldGid fieldGid) {
        this.rabbitId = rabbitId;
        this.fieldGid = fieldGid;
        alive = true;

    }


    public int getRabbitId() {
        return rabbitId;
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
    public void run() {
        super.run();


        while (fieldGid.rabbitIsAlive(this)) {
            try {
                Thread.sleep(500);
                boolean isEmpty = true;
                while (isEmpty) {
                    int[] newPositions = newRandomAdjacentPosition();
                    if (fieldGid.isFreePosition(newPositions[0], newPositions[1])) {
                        fieldGid.moveFromTo(this, positionX, positionY, newPositions[0], newPositions[1]);
                        isEmpty = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        fieldGid.RabbitDied(positionX,positionY);
     }



    private int[] newRandomAdjacentPosition() {
        int direction = (int) Math.floor((Math.random() * 4));

        int _positionX = positionX;
        int _positionY = positionY;

        if (direction == 0)
            _positionX++;
        else if (direction == 1)
            _positionY++;
        else if (direction == 2)
            _positionX--;
        else if (direction == 3)
            _positionY--;

        if (_positionX < 0 || _positionX >= fieldGid.getDimension() || _positionY < 0 || _positionY >= fieldGid.getDimension())
            return newRandomAdjacentPosition();

        return new int[]{_positionX, _positionY};

    }

    @Override
    public String toString() {
        return "Rabbit{" +
                "fieldGid=" + fieldGid +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", alive=" + alive +
                '}';
    }
}
