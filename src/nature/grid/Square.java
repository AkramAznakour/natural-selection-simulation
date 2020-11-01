package sample.grid;


import sample.animals.Animal;

public class Square {
    private int positionX;
    private int positionY;
    private Animal animal;
    private SquareState squareState = SquareState.FREE;

    public int getPositionX() {
        return positionX;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public int getPositionY() {
        return positionY;
    }

    public SquareState getSquareState() {
        return squareState;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void setSquareState(SquareState squareState) {
        this.squareState = squareState;
    }

    public Square(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }
}
