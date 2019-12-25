/*
 * Theme Song Title: Slot Machine
 * Arrangement: Mahito Yokota, Toru Minegishi, Yasuaki Iwata, Koji Kondo
 * From: youtube.com/GilvaSunner
 *
 * Sound Effect Title: Slot Machine Sound Reel 2015
 * From: youtube.com/Dave Cina
 * Sound Effect Title: Sad Crowd Aww Sound Effect
 * From: youtube.com/Sound Effects & Templates
 * 
 * Images: Pictures of my friends' cats
 * This program is written by Pikulkaew Boonpeng for Advanced Java Programming
 * class by Professor Richmond, A at BunkerHill Community College.
 */
package slotmachine;

import java.io.File;
import static java.lang.System.exit;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * A slot machine program that displays three spinning wheels containing images.
 * When the spin is over the player wins if all three windows display the same
 * image; otherwise, the player loses.
 *
 * @author Pikulkaew Boonpeng
 * @since 11-15-2019
 */
public class SlotMachine extends Application {

    final int SPACE = 10; // for easy spacing
    VBox boxAll = new VBox(SPACE * 5); // contains all the components

    Label lbHead = new Label("KITTEN SLOT MACHINE"); // head line

    final int START_MONEY = 1000; // initializing money
    int bankMoney = START_MONEY; // bank's money
    int playerMoney = START_MONEY; // player's money
    HBox boxMoney = new HBox(SPACE * 5); // contains bank's and player's moneys
    Label lbBank = new Label(String.valueOf("Bank: $" + bankMoney));
    Label lbPlayer = new Label(String.valueOf("Your Credits: $" + playerMoney));

    HBox boxBtn = new HBox(SPACE); // contains buttons
    Button btnSpin = new Button("SPIN"), btnQuit = new Button("QUIT");

    HBox boxWheel = new HBox(SPACE * 4); // contains the wheels
    final int W = 200; // wheel's width
    final int H = 200; // wheel's height
    ImageView iv1 = new ImageView(new Image("images/1.png"));
    ImageView iv2 = new ImageView(new Image("images/2.png"));
    ImageView iv3 = new ImageView(new Image("images/3.png"));
    ImageView iv4 = new ImageView(new Image("images/4.png"));
    ImageView iv5 = new ImageView(new Image("images/5.png"));

    Random random = new Random();
    int lot1 = 1, lot2 = 2, lot3 = 3; // initialize the wheels
    Timeline spin;

    // sound elements
    final String SONG = "sounds/SlotMachine.wav",
            SPIN_SOUND = "sounds/SlotMachineSoundReel2015.wav",
            SAD_SOUND = "sounds/SadCrowdAwwSoundEffect.wav";
    Media song, sadSound, spinSound;
    MediaPlayer playSong, playSad, playSpin;

    /**
     * Contains setup and buttons' actions
     * @param primaryStage A window for the game
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            setup();
            btnActions();

            Scene scene = new Scene(boxAll, 800, 700);

            primaryStage.setTitle("Project3: Slot Machine by Pikulkaew Boonpeng");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Actions for Spin and Quit buttons.
     */
    private void btnActions() {
        btnSpin.setOnAction(e -> {
            if (playerMoney == 0) {
                noMoneyWindow();
            } else {
                // Player is not allowed to click the button while the wheels are spinning 
                btnSpin.setDisable(true);

                // Set spin sound
                spinSound = new Media(new File(SPIN_SOUND).toURI().toString());
                playSpin = new MediaPlayer(spinSound);
                playSpin.setStartTime(Duration.millis(2100));
                playSpin.setStopTime(Duration.millis(4000));
                playSpin.setCycleCount(1);
                playSpin.play();

                // Set money
                bankMoney += 1;
                playerMoney -= 1;
                setStringMoney();

                // No jackpots
                boxWheel.setStyle("-fx-background-color: black;");

                // Set animation
                spin = new Timeline(new KeyFrame(Duration.millis(50), (ActionEvent event) -> {
                    lot1 = random.nextInt(5) + 1;
                    lot2 = random.nextInt(5) + 1;
                    lot3 = random.nextInt(5) + 1;
                    setLot(iv1, lot1);
                    setLot(iv2, lot2);
                    setLot(iv3, lot3);
                }));
                spin.setCycleCount(30);
                spin.play();
                spin.setOnFinished(f -> {
                    if (playerMoney == 0) {
                        noMoneyWindow();
                    } else {
                        // Allows the player to click on the button
                        btnSpin.setDisable(false);

                        // If jackpot
                        if (lot1 == lot2 && lot1 == lot3) {
                            // Set jackpot sound
                            MediaPlayer playJackpot = new MediaPlayer(spinSound);
                            playJackpot.setStartTime(Duration.millis(4000));
                            playJackpot.setCycleCount(1);
                            playJackpot.play();

                            // Set  style
                            boxWheel.setStyle("-fx-background-color: red;");

                            // Set money
                            playerMoney += bankMoney;
                            bankMoney = START_MONEY;
                            setStringMoney();
                        }
                    }
                });
            }
        });

        btnQuit.setOnAction(e -> {
            playSadSound();
            quitWindow();
        });
    }

    /**
     * Called for playing sad song.
     */
    private void playSadSound() {
        sadSound = new Media(new File(SAD_SOUND).toURI().toString());
        playSad = new MediaPlayer(sadSound);
        playSad.setStartTime(Duration.millis(300));
        playSad.setCycleCount(1);
        playSad.play();
    }

    /**
     * Called when the player has no money left.
     */
    private void noMoneyWindow() {
        playSadSound();

        // Maintain player's money to be zero and
        // the bank's money to be whatever it is on the moment
        setStringMoney();

        setDisableMainBtns();

        // Create the Play-Again? window
        Stage st = new Stage();
        Button yes = new Button("YES");
        Button no = new Button("NO");
        setupWindow(st, yes, no, "Play Again?", "You are OUT of credits.\nDo you want to play again?");

        yes.setOnAction(y -> {
            st.hide();
            setEnableMainBtns();

            // Reset money
            bankMoney = START_MONEY;
            playerMoney = START_MONEY;
            setStringMoney();
        });

        no.setOnAction(n -> {
            st.hide();
            quitWindow();
            setDisableMainBtns();
        });
    }

    /**
     * Called to ask if the player wants a new game.
     */
    private void newGameWindow() {
        Stage st = new Stage();
        Button yes = new Button("YES");
        Button no = new Button("NO");
        setupWindow(st, yes, no, "New Game", "New Game?");

        yes.setOnAction(yA -> {
            st.hide();

            // Reset money
            bankMoney = START_MONEY;
            playerMoney = START_MONEY;
            setStringMoney();

            boxWheel.setStyle("-fx-background-color: black;");
            setEnableMainBtns();
            setLot(iv1, 1);
            setLot(iv2, 2);
            setLot(iv3, 3);
        });

        no.setOnAction(nA -> {
            st.hide();
            setEnableMainBtns();
        });
    }

    /**
     * Called to ask if the player wants to quit.
     */
    private void quitWindow() {
        Stage st = new Stage();
        Button yes = new Button("YES");
        Button no = new Button("NO");
        setupWindow(st, yes, no, "Exit", "Leaving? Are you sure?");

        yes.setOnAction(y -> {
            exit(0);
        });

        // Asks if the player wants a new game
        no.setOnAction(n -> {
            st.hide();
            newGameWindow();
            setDisableMainBtns();
        });
    }

    /**
     * Creates a new window.
     * @param st A window
     * @param yes Button Yes
     * @param no Button No
     * @param title The window's title
     * @param msg Message in the window
     */
    private void setupWindow(Stage st, Button yes, Button no, String title, String msg) {
        HBox btns = new HBox(SPACE);
        btns.getChildren().addAll(yes, no);
        btns.setAlignment(Pos.CENTER);

        VBox frm = new VBox(SPACE);
        frm.setAlignment(Pos.CENTER);
        frm.getChildren().addAll(new Label(msg), btns);

        Scene s = new Scene(frm, 200, 120);
        st.setTitle(title);
        st.setScene(s);
        st.show();
        st.setAlwaysOnTop(true);
        setDisableMainBtns();
    }

    /**
     * Sets numbers to images
     * @param iv Wheel
     * @param i a number for an image
     */
    private void setLot(ImageView iv, int i) {
        switch (i) {
            case 1:
                iv.setImage(new Image("images/1.png"));
                break;
            case 2:
                iv.setImage(new Image("images/2.png"));
                break;
            case 3:
                iv.setImage(new Image("images/3.png"));
                break;
            case 4:
                iv.setImage(new Image("images/4.png"));
                break;
            case 5:
                iv.setImage(new Image("images/5.png"));
                break;
        }
    }

    /**
     * Sets image size.
     * @param iv Wheel
     */
    private void setImageSize(ImageView iv) {
        iv.setFitHeight(H);
        iv.setFitWidth(W);
    }

    /**
     * Enable Spin and Quit buttons.
     */
    private void setEnableMainBtns() {
        btnSpin.setDisable(false);
        btnQuit.setDisable(false);
    }

    /**
     * Disable Spin and Quit buttons.
     */
    private void setDisableMainBtns() {
        btnSpin.setDisable(true);
        btnQuit.setDisable(true);
    }

    /**
     * Sets balance to Bank's and Player's money
     */
    private void setStringMoney() {
        lbBank.setText(String.valueOf("Bank: $" + bankMoney));
        lbPlayer.setText(String.valueOf("Your Credits: $" + playerMoney));
    }

    /**
     * Setup the interface
     */
    private void setup() {
        // Background Song
        song = new Media(new File(SONG).toURI().toString());
        playSong = new MediaPlayer(song);
        playSong.setCycleCount(MediaPlayer.INDEFINITE);
        playSong.play();

        boxAll.getChildren().addAll(lbHead, boxMoney, boxWheel, boxBtn);
        boxAll.setAlignment(Pos.CENTER);
        boxAll.setStyle("-fx-background-color: purple;");

        lbHead.setFont(Font.font(50));
        lbHead.setStyle("-fx-text-fill: yellow;");

        lbBank.setFont(Font.font(30));
        lbBank.setStyle("-fx-text-fill: white;");
        lbPlayer.setFont(Font.font(30));
        lbPlayer.setStyle("-fx-text-fill: white;");
        boxMoney.getChildren().addAll(lbBank, lbPlayer);
        boxMoney.setAlignment(Pos.CENTER);

        setImageSize(iv1);
        setImageSize(iv2);
        setImageSize(iv3);
        setImageSize(iv4);
        setImageSize(iv5);
        boxWheel.setStyle("-fx-background-color: black;");
        boxWheel.getChildren().addAll(iv1, iv2, iv3);
        boxWheel.setAlignment(Pos.CENTER);
        boxWheel.setPrefSize(800, 220);

        btnSpin.setMinSize(120, 80);
        btnSpin.setFont(Font.font(20));
        btnQuit.setMinSize(120, 80);
        btnQuit.setFont(Font.font(20));
        boxBtn.getChildren().addAll(btnSpin, btnQuit);
        boxBtn.setAlignment(Pos.CENTER);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
