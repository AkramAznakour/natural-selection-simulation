package sample.grid;

import sample.animals.Animal;
import sample.animals.Fox;
import sample.animals.Rabbit;
import sample.helper.AStar;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class FieldGid {

    private final Semaphore semaphore;
    private final int dimension;
    private Square[][] squares;
    private LinkedList<Semaphore> rabbitsLives;


    public FieldGid(int dimension) {
        this.dimension = dimension;
        semaphore = new Semaphore(1, true);
        squares = new Square[dimension][dimension];

        for (int i = 0; i < squares.length; i++)
            for (int j = 0; j < squares[i].length; j++)
                squares[i][j] = new Square(i, j);

        rabbitsLives = new LinkedList<>();
    }


    public Semaphore getSemaphore() {
        return semaphore;
    }

    public LinkedList<Semaphore> getRabbitsLives() {
        return rabbitsLives;
    }

    public int getDimension() {
        return dimension;
    }

    public Square[][] getSquares() {
        return squares;
    }

    public boolean isFreePosition(int _positionX, int _positionY) {
        return isSquarePositionOfType(_positionX, _positionY, SquareState.FREE);
    }

    public boolean isFoxPosition(int _positionX, int _positionY) {
        return isSquarePositionOfType(_positionX, _positionY, SquareState.FOX);
    }

    public boolean isRabbitPosition(int _positionX, int _positionY) {
        return isSquarePositionOfType(_positionX, _positionY, SquareState.RABBIT);
    }

    public boolean isSquarePositionOfType(int _positionX, int _positionY, SquareState type) {

        boolean isOfType = false;

        try {
            semaphore.acquire();

            isOfType = squares[_positionX][_positionY].getSquareState() == type;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();

        }

        return isOfType;
    }

    public void populate(Animal animal) {
        boolean isEmpty = true;
        while (isEmpty) {
            int positionX = (int) Math.floor((Math.random() * dimension));
            int positionY = (int) Math.floor((Math.random() * dimension));

            if (squares[positionX][positionY].getSquareState() == SquareState.FREE) {
                animal.setPositionX(positionX);
                animal.setPositionY(positionY);
                squares[positionX][positionY].setAnimal(animal);
                if (animal instanceof Fox)
                    squares[positionX][positionY].setSquareState(SquareState.FOX);
                else if (animal instanceof Rabbit) {
                    squares[positionX][positionY].setSquareState(SquareState.RABBIT);
                    rabbitsLives.add(new Semaphore(1));
                }
                isEmpty = false;
            }
        }
    }


    public void moveFromTo(Animal animal, int positionX, int positionY, int newPositionX, int newPositionY) {

        try {
            semaphore.acquire();
            squares[positionX][positionY].setSquareState(SquareState.FREE);
            squares[positionX][positionY].setAnimal(null);

            if (animal instanceof Fox)
                squares[newPositionX][newPositionY].setSquareState(SquareState.FOX);
            if (animal instanceof Rabbit)
                squares[newPositionX][newPositionY].setSquareState(SquareState.RABBIT);

            squares[newPositionX][newPositionY].setAnimal(animal);

            animal.setPositionX(newPositionX);
            animal.setPositionY(newPositionY);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }

    }

    @Override
    public String toString() {

        StringBuilder s = new StringBuilder();

        try {
            semaphore.acquire();

            for (Square[] square : squares) {
                System.out.println("");
                for (Square value : square) {
                    if (value.getSquareState() == SquareState.FOX) {
                        String foxStatus =
                                String.format("%02d", ((Fox) value.getAnimal()).getNextSteps().size())
                                        + "|"
                                        + String.format("%02d", ((Fox) value.getAnimal()).getPointsCounter());
                        s.append("[F" + foxStatus + "]");
                    } else if (value.getSquareState() == SquareState.RABBIT) {
                        s.append("[R     ]");
                    } else {
                        s.append("[      ]");
                    }
                }
                s.append("\n");
            }

            s.append("[AliveRabbits : ")
                    .append(getNumberOfAliveRabbits())
                    .append(" ] ")
                    .append("   ")
                    .append("[AliveFoxes : ")
                    .append(getNumberOfAliveFoxes())

                    .append(" ] ")
                    .append("   ");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }

        return s.toString();
    }

    private int getNumberOfAliveFoxes() {

        int count = 0;
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j].getSquareState() == SquareState.FOX && ((Fox) squares[i][j].getAnimal()).getPointsCounter() > 0)
                    count++;
            }

        }

        return count;
    }

    private int getNumberOfAliveRabbits() {

        int count = 0;

        try {
            for (int i = 0; i < rabbitsLives.size(); i++) {
                boolean tryAcquire = ((Semaphore) rabbitsLives.get(i)).tryAcquire(100, TimeUnit.MILLISECONDS);
                if (tryAcquire) {
                    ((Semaphore) rabbitsLives.get(i)).release();
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


    public boolean rabbitIsAlive(Rabbit rabbit) {
        boolean tryAcquire = false;
        try {
            tryAcquire = ((Semaphore) rabbitsLives.get(rabbit.getRabbitId())).tryAcquire(100, TimeUnit.MILLISECONDS);
            if (tryAcquire) {
                ((Semaphore) rabbitsLives.get(rabbit.getRabbitId())).release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tryAcquire;
    }

    public int[] getRandomRabbitPosition() {

        try {
            semaphore.acquire();

            int random = (int) Math.floor((Math.random() * rabbitsLives.size()));

            int count = 0;

            for (int i = 0; i < squares.length; i++) {
                for (int j = 0; j < squares[i].length; j++) {
                    if (squares[i][j].getSquareState() == SquareState.RABBIT) {
                        if (count == random)
                            return new int[]{squares[i][j].getPositionX(), squares[i][j].getPositionY()};
                        count++;

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }


        return new int[]{squares[0][0].getPositionX(), squares[0][0].getPositionY()};
    }


    public LinkedList<int[]> getShortestPath(int[] source, int[] rabbitPosition) {


        LinkedList<int[]> path = new LinkedList<>();

        try {
            semaphore.acquire();
            int[][] matrix = new int[squares.length][squares[0].length];

            for (int i = 0; i < squares.length; i++)
                for (int j = 0; j < squares[i].length; j++)
                    if (squares[i][j].getSquareState() == SquareState.FREE) matrix[i][j] = 0;
                    else matrix[i][j] = 100;


            AStar as = new AStar(matrix, source[0], source[1], true);
            List<AStar.Node> patha = as.findPathTo(rabbitPosition[0], rabbitPosition[1]);

            for (AStar.Node node : patha) {
                path.add(new int[]{node.x, node.y,});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
        return path;
    }

    public void killRabbit(int positionX, int positionY) {
        try {

            int id = ((Rabbit) squares[positionX][positionY].getAnimal()).getRabbitId();
            squares[positionX][positionY].setSquareState(SquareState.FREE);

            boolean aquired = rabbitsLives.get(id).tryAcquire(100, TimeUnit.SECONDS);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void foxDie(int positionX, int positionY) {

        try {
            semaphore.acquire();
            if (squares[positionX][positionY].getSquareState() == SquareState.FOX) {
                squares[positionX][positionY].setSquareState(SquareState.FREE);
                squares[positionX][positionY].setAnimal(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public void RabbitDied(int positionX, int positionY) {
        try {
            semaphore.acquire();
            if (squares[positionX][positionY].getSquareState() == SquareState.RABBIT) {
                squares[positionX][positionY].setSquareState(SquareState.FREE);
                squares[positionX][positionY].setAnimal(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }
}
