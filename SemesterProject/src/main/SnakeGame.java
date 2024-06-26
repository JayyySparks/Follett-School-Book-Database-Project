import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SnakeGame extends Application {
    // Initialize variables
    static int speed = 5;
    static int foodcolor = 0;
    static int width = 20;
    static int height = 20;
    static int foodX = 0;
    static int foodY = 0;
    static int cornersize = 25;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.left;
    static boolean gameOver = false;
    static boolean gameStart = true;
    static Random rand = new Random();
    private static Button restartButton;
    private static Button startGame;
    private static Button returnToMenu;

    public enum Dir {
        left, right, up, down
    }

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void start(Stage primaryStage) {
        try {
            newFood();

            VBox root = new VBox();
            Canvas c = new Canvas(width * cornersize, height * cornersize);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(c);
            
         // Get primary screen & bounds of the screen for size of scene
    		Screen screen = Screen.getPrimary();
    		Rectangle2D bounds = screen.getVisualBounds();
            
            returnToMenu = new Button("Return to Menu");
            returnToMenu.setFont(Font.font("Arial Black", FontWeight.BOLD, 18));
            returnToMenu.setOnAction(e -> {
            	SwitchScene.switchScene("Games", primaryStage);
            });
            returnToMenu.setVisible(false);
            returnToMenu.setPrefSize(200, 50);
            StackPane.setAlignment(returnToMenu, Pos.TOP_CENTER); // Set button position
            StackPane.setMargin(returnToMenu, new Insets(20, 0, 0, 0)); // Set margin
            stackPane.getChildren().add(returnToMenu);

            restartButton = new Button("Restart Game");
            restartButton.setFont(Font.font("Arial Black", FontWeight.BOLD, 18));
            restartButton.setOnAction(e -> {
                snake.clear();
                direction = Dir.left;
                gameOver = false;
                speed = 5;
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                newFood();
                restartButton.setVisible(false);
            });
            restartButton.setVisible(false); // Initially set to invisible
            restartButton.setPrefSize(200, 50); // Set button size
            StackPane.setAlignment(restartButton, Pos.BOTTOM_CENTER); // Set button position
            StackPane.setMargin(restartButton, new Insets(0, 0, 20, 0)); // Set margin
            stackPane.getChildren().add(restartButton);
            
            startGame = new Button("Start Game");
            startGame.setFont(Font.font("Arial Black", FontWeight.BOLD, 18));
            startGame.setOnAction(e -> {
                snake.clear();
                direction = Dir.left;
                gameStart = false;
                speed = 5;
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                snake.add(new Corner(width / 2, height / 2));
                newFood();
                startGame.setVisible(false);
            });
            startGame.setVisible(false); // Initially set to invisible
            startGame.setPrefSize(200, 50); // Set button size
            StackPane.setAlignment(startGame, Pos.BOTTOM_CENTER); // Set button position
            StackPane.setMargin(startGame, new Insets(0, 0, 20, 0)); // Set margin
            stackPane.getChildren().add(startGame);

            // Add a return to menu button
            
            new AnimationTimer() {
                long lastTick = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / speed) {
                        lastTick = now;
                        tick(gc);
                    }
                }

            }.start();

            Scene scene = new Scene(stackPane, width * cornersize, height * cornersize);

         // Setup key controls
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode() == KeyCode.W || key.getCode() == KeyCode.UP) {
                    direction = Dir.up;
                }
                if (key.getCode() == KeyCode.A || key.getCode() == KeyCode.LEFT) {
                    direction = Dir.left;
                }
                if (key.getCode() == KeyCode.S || key.getCode() == KeyCode.DOWN) {
                    direction = Dir.down;
                }
                if (key.getCode() == KeyCode.D || key.getCode() == KeyCode.RIGHT) {
                    direction = Dir.right;
                }
            });

            // Add start snake parts
            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));
            primaryStage.setScene(scene);
            primaryStage.setTitle("SNAKE GAME");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tick
    public static void tick(GraphicsContext gc) {
    	if(gameStart) {
    		startGame.setVisible(true);
    		return;
    	}
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 100, 250);
            restartButton.setVisible(true); // Set the restart button to visible
            returnToMenu.setVisible(true);
            return;
        }

        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        switch (direction) {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0) {
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y > height) {
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x > width) {
                    gameOver = true;
                }
                break;
        }

        // eat food
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            snake.add(new Corner(-1, -1));
            newFood();
        }

        // self destroy
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }

        // fill background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * cornersize, height * cornersize);

        // Scoreboard
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("", 30));
        gc.fillText("Score: " + (speed - 6), 10, 30);

        // random foodcolor
        Color cc = Color.RED;

        gc.setFill(cc);
        gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

        // snake
        for (Corner c : snake) {
            gc.setFill(Color.LIGHTGREEN);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
            gc.setFill(Color.GREEN);
            gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
        }
    }

    // food
    public static void newFood() {
        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            for (Corner c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
            speed++;
            break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
