package svanimpe.fxgameloop;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import svanimpe.fxgameloop.game.BallPit;
import svanimpe.fxgameloop.game.Game;
import svanimpe.fxgameloop.loop.FixedSteps;
import svanimpe.fxgameloop.loop.FixedStepsWithInterpolation;
import svanimpe.fxgameloop.loop.GameLoop;
import svanimpe.fxgameloop.loop.VariableSteps;

public class Main extends Application
{
    public static final float SCALE = 100f; /* pixels per meter */
    public static final float WIDTH = 12f; /* 12m */
    public static final float HEIGHT = 8f; /* 8m */
    
    private static final int velocityIterations = 8;
    private static final int positionIterations = 3;
    
    private final World world = new World(new Vec2(0f, -10f));
    private final AnchorPane root = new AnchorPane();
    private final Label fpsLabel = new Label();
    
    private Game activeGame = null;
    
    @FXML
    private ChoiceBox<Game> gamesBox;
    
    @FXML
    private ChoiceBox<GameLoop> loopsBox;
    
    @FXML
    private CheckBox maxStepCheck;
    
    @FXML
    private ChoiceBox<Float> stepsBox;
    
    @FXML
    private ChoiceBox<Integer> objectsBox;
    
    @Override
    public void start(Stage stage) throws IOException
    { 
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/ControlPane.fxml"));
        loader.setController(this);
        loader.load();
        GridPane controls = loader.getRoot();
        AnchorPane.setTopAnchor(controls, 0.0);
        AnchorPane.setLeftAnchor(controls, 0.0);
        AnchorPane.setTopAnchor(fpsLabel, 10.0);
        AnchorPane.setRightAnchor(fpsLabel, 10.0);
        root.getChildren().addAll(controls, fpsLabel);
        
        gamesBox.setItems(FXCollections.observableArrayList(
                new BallPit()
        ));
        gamesBox.getSelectionModel().selectFirst();
        
        Consumer<Float> updater = secondsElapsed -> world.step(secondsElapsed, velocityIterations, positionIterations);
        Runnable renderer = () -> activeGame.updatePositions();
        Consumer<Float> interpolater = alpha -> activeGame.interpolatePositions(alpha);
        Consumer<Integer> fpsReporter = fps -> fpsLabel.setText(String.format("FPS: %d", fps));
        
        loopsBox.setItems(FXCollections.observableArrayList(
                new VariableSteps(updater, renderer, fpsReporter),
                new FixedSteps(updater, renderer, fpsReporter),
                new FixedStepsWithInterpolation(updater, renderer, interpolater, fpsReporter)
        ));
        loopsBox.getSelectionModel().selectFirst();
        
        stepsBox.setItems(FXCollections.observableArrayList(
                0.0166f, 0.025f, 0.0333f
        ));
        stepsBox.getSelectionModel().selectFirst();
        stepsBox.disableProperty().bind(maxStepCheck.selectedProperty().not());
        
        objectsBox.setItems(FXCollections.observableArrayList(
                100, 150, 200, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000
        ));
        objectsBox.getSelectionModel().selectFirst();
       
        Scene scene = new Scene(root, WIDTH * SCALE, HEIGHT * SCALE);
        stage.setScene(scene);
        stage.setTitle("FX Game Loop");
        stage.setResizable(false);
        stage.show();
    }
    
    @FXML
    private void play()
    {
        if (activeGame != null) {
            Body body = world.getBodyList();
            while (body != null) {
                world.destroyBody(body);
                body = body.getNext();
            }
            root.getChildren().remove(0); /* Remove the previous gamePane. */
            fpsLabel.setText("");
        }
        
        activeGame = gamesBox.getValue();
        Pane gamePane = new Pane();
        activeGame.load(world, gamePane, objectsBox.getValue());
        root.getChildren().add(0, gamePane); /* Add the gamePane at the bottom, underneath the controls. */
        
        loopsBox.getItems().forEach(AnimationTimer::stop);
        loopsBox.getValue().setMaximumStep(maxStepCheck.isSelected() ? stepsBox.getValue() : Float.MAX_VALUE);
        loopsBox.getValue().start();
    }
    
    public static void main(String... args)
    {
        Application.launch(Main.class, args);
    }
}
