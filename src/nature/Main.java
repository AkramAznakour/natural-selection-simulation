package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.animals.Fox;
import sample.animals.Rabbit;
import sample.grid.FieldGid;

import java.util.Date;
import java.util.concurrent.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("nature-view.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    private final static int NUMBER_OF_RABBITS = 10;
    private final static int NUMBER_OF_FOXES = 5;
    private final static int DIMENSION = 8;

    public static void main(String[] args) {
        ///launch(args);

        Fox[] foxes = new Fox[NUMBER_OF_FOXES];
        Rabbit[] rabbits = new Rabbit[NUMBER_OF_RABBITS];

        FieldGid fieldGid = new FieldGid(DIMENSION);


        for (int i = 0; i < foxes.length; i++) foxes[i] = new Fox(fieldGid);

        for (int i = 0; i < rabbits.length; i++) rabbits[i] = new Rabbit(i, fieldGid);

        for (Fox fox : foxes) fieldGid.populate(fox);

        for (Rabbit rabbit : rabbits) fieldGid.populate(rabbit);

        for (Rabbit rabbit : rabbits) rabbit.start();

        for (Fox foxe : foxes) foxe.start();

        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> System.out.println(fieldGid), 0, 500, TimeUnit.MILLISECONDS);


    }


}
