package com.alaskalinuxuser.criticalvelocity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer; // For visualizing collision shapes.
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class criticalvelocity extends ApplicationAdapter implements ApplicationListener {

	SpriteBatch batch;
    //ShapeRenderer shapeRenderer; // For visualizing collision shapes.
	Texture background, winBG;
	Texture[] vehicles, dialogs, powerups, criticalship, distortship;
	Texture topBar, bottomBar;
	int blinkState, gameState, gapSize, maxOffset, numBarriers, numSpecials, spacing, thrust,
            playerScore, timerDist, waitTime;
	float vehicleY, vehicleX, gravityDown, vehicleSpeed, increaseSpeed, winX;
    float[] bothX, bottomY, topY, specY, specX;
    int[] eachOffset, setSpec;
    long id;
    Boolean criticalVelocity, spacialDistortion, waitBoolean;
    Random randomNumber;
    Circle shipCircle, frontCircle, rearCircle, powerCircle;
    Rectangle barrierRectangleTop, barrierRectangleBottom;
    BitmapFont font;
    Sound sound, soundOne;
    Music bgMusic;

    private Viewport viewport;
    private Camera camera;
    private StringProvider stringprovider;

	public criticalvelocity(StringProvider stringprovider) {
		super();
		this.stringprovider = stringprovider;
	}

	@Override
	public void create () {

        // Testing Only // Gdx.app.log("WJHscreen", String.valueOf(Gdx.graphics.getHeight()) +"x"+ String.valueOf(Gdx.graphics.getWidth()));
        camera = new PerspectiveCamera();
        if (Gdx.graphics.getHeight() <= 800 || Gdx.graphics.getWidth() <= 480) {

            viewport = new ScreenViewport(camera);

        } else {

            viewport = new StretchViewport(480, 800, camera);

        }
        viewport.apply();
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

        // Sound files!
        Audio audio = Gdx.audio;
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("spblk.mp3"));
        sound = Gdx.audio.newSound(Gdx.files.internal("crash.mp3"));
        soundOne = Gdx.audio.newSound(Gdx.files.internal("coin.mp3"));

        //shapeRenderer = new ShapeRenderer(); // For visualizing collision shapes.
        shipCircle = new Circle();
        frontCircle = new Circle();
        rearCircle = new Circle();
        powerCircle = new Circle();
        barrierRectangleTop = new Rectangle();
        barrierRectangleBottom = new Rectangle();

        // Add our font.
        font = new BitmapFont();
        // Set our scale.
        font.getData().scale(5f);

		batch = new SpriteBatch();

        if (Gdx.graphics.getHeight() <= 800 || Gdx.graphics.getWidth() <= 480) {

            background = new Texture("bgblsm.png");

        } else {

            background = new Texture("bgblue.png");

        }

		background = new Texture("bgblue.png");
        winBG = new Texture("bggreen.png");
		topBar = new Texture("topbarrier.png");
		bottomBar = new Texture("bottombarrier.png");

        vehicles = new Texture[7];
        vehicles[0] = new Texture("vehicle.png");
        vehicles[1] = new Texture("vehicletwo.png");
        vehicles[2] = new Texture("vehiclethree.png");
        vehicles[3] = new Texture("vehiclefour.png");
        vehicles[4] = new Texture("vehiclefive.png");
        vehicles[5] = new Texture("vehiclesix.png");
        vehicles[6] = new Texture("vehicleexplode.png");

        powerups = new Texture[3];
        powerups[0] = new Texture("powerupbonus.png");
        powerups[1] = new Texture("powerupdistort.png");
        powerups[2] = new Texture("powerupslow.png");

        criticalship = new Texture[7];
        criticalship[0] = new Texture("superone.png");
        criticalship[1] = new Texture("supertwo.png");
        criticalship[2] = new Texture("superthree.png");
        criticalship[3] = new Texture("superfour.png");
        criticalship[4] = new Texture("superfive.png");
        criticalship[5] = new Texture("supersix.png");
        criticalship[6] = new Texture("vehicleexplode.png");

        distortship = new Texture[7];
        distortship[0] = new Texture("distone.png");
        distortship[1] = new Texture("disttwo.png");
        distortship[2] = new Texture("distthree.png");
        distortship[3] = new Texture("distfour.png");
        distortship[4] = new Texture("distfive.png");
        distortship[5] = new Texture("distsix.png");
        distortship[6] = new Texture("vehicleexplode.png");

        dialogs = new Texture[4];
        dialogs[0] = new Texture("dialogbegin.png");
        dialogs[1] = new Texture("gameoverBox.png");
        dialogs[2] = new Texture("gamewinBox.png");
        dialogs[3] = new Texture("dialogspecial.png");


        // Game variables....
        gapSize = 450;
        maxOffset = (Gdx.graphics.getHeight()/2 - gapSize);
        spacing = (Gdx.graphics.getWidth()*2/3) + (gapSize/2);
        increaseSpeed = .125f;
        thrust = -25;
        numBarriers = 4;
        numSpecials = 1;

        // My arrays....
        eachOffset = new int[numBarriers];
        setSpec = new int[numSpecials];
        bothX = new float[numBarriers];
        topY = new float[numBarriers];
        bottomY = new float[numBarriers];
        specY = new float[numSpecials];
        specX = new float[numSpecials];

        // Random number generator.
        randomNumber = new Random();

        restartGame();

	}// On create.

    public void restartGame() {

        // Que the music.
        bgMusic.stop();
        bgMusic.setVolume(0.5f); // sets the volume to half the maximum volume
        bgMusic.setLooping(true); // will repeat playback until music.stop() is called
        bgMusic.play();

        // Set up our initial position.
        vehicleY = (Gdx.graphics.getHeight()/2 - vehicles[0].getHeight()/2);
        vehicleX = (Gdx.graphics.getWidth()/3 - vehicles[0].getWidth()/2);

        // Set our variables.
        playerScore = 0;
        gravityDown = 0;
        blinkState = 0;
        gameState = 0;
        vehicleSpeed = 5;
        winX = 0;
        criticalVelocity = false;
        spacialDistortion = false;
        waitTime = 60;

        // Testing only //
        //timerDist = 2500;

        // For loop to build the barriers.
        for (int z = 0; z < numBarriers; z++) {

            bottomY[z] = Gdx.graphics.getHeight()/2 - bottomBar.getHeight() - randomNumber.nextInt(maxOffset);
            topY[z] = bottomY[z] + bottomBar.getHeight() + gapSize;
            bothX[z] = Gdx.graphics.getWidth()/2 + Gdx.graphics.getWidth() + (z * spacing);

        }

        // For loop to build the specials.
        for (int s = 0; s < numSpecials; s++) {

            // Place the special.
            specX[s] = Gdx.graphics.getWidth()/(randomNumber.nextInt(10)+1);
            specY[s] = (randomNumber.nextInt(Gdx.graphics.getHeight())+1);

            // Give it a type.
            setSpec[s] = randomNumber.nextInt(3);
            // Testing only // Gdx.app.log("setSpec WJH", String.valueOf(setSpec[s]));

        }

    } // Restart Game.

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

	@Override
	public void render () {

        // Set up to render a red screen.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Begin the batch of textures.
        batch.begin();

        // Draw the background.
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        /*
         * Check our game state.
		 * 0 = paused.
		 * 1 = in play.
		 * 2 = crashed or game over.
		 * 3 = winning
		 * 4 = game over with win condition.
		 * 5 = define special abilities.
		 */

		if (gameState == 1) {

            // We are now in play.
            // Determin our blink state for our ship.
            if (blinkState <= 4) {

                blinkState++;

            } else {

                blinkState = 0;

            }

            // What to do when we touch the screen.
			if (Gdx.input.justTouched() && vehicleY <= Gdx.graphics.getHeight() - vehicles[0].getHeight()) {

                // push the space ship up.
				gravityDown = thrust;

			} // Touching the screen.

            // If we are moving above the bottom of the screen.
            if (vehicleY > 0 - (vehicles[0].getHeight()*.60) || gravityDown <= 0) { // In play.
                gravityDown++;
                vehicleY -= (gravityDown / 2);
            } // In play.

            // If we fall off the screen.
            if (vehicleY < 0 - (vehicles[0].getHeight()*.60)) {

                // Play the crash sound!
                id = sound.play();

                waitBoolean = true;
                waitTime = 60;
                gameState = 2;
            } // we fell off the screen.


            // This is how we move the barriers.
            for (int z = 0; z < numBarriers; z++) {

                if (bothX[z] < 0 - topBar.getWidth()) {

                    // Since it went off the screen, move it to the beginning again.
                    bothX[z] = (numBarriers * spacing) - topBar.getWidth();

                    // And increase the speed.
                    vehicleSpeed = vehicleSpeed + increaseSpeed;

                    Gdx.app.log("WJH", String.valueOf(vehicleSpeed));

                    // And determine Critical Velocity!
                    if (vehicleSpeed >= 12) {

                        criticalVelocity = true;

                    } else {

                        // We maintain this else, in case a slow down power up is used, bringing us
                        // under the threshold of critical velocity....
                        criticalVelocity = false;

                    }

                } else {

                    // Since they are on the screen, let's move them.
                    bothX[z] = bothX[z] - vehicleSpeed;

                } // moving barriers.

                // This is how we move the barriers.
                for (int x = 0; x < numBarriers; x++) {

                    if (bothX[x] < vehicleX - topBar.getWidth()) {

                        // Because the user made it past that, let's give them a score bonus!

                        if (criticalVelocity){

                            // critical velocity, then a three point bonus.
                            playerScore = playerScore + 3;

                        } else {

                            // Not critical velocity, then just a one point bonus.
                            playerScore = playerScore + 1;
                        }

                    } // scoring barriers.
                }

                // Either way, let's draw them.
                batch.draw(topBar, bothX[z], topY[z]);
                batch.draw(bottomBar, bothX[z], bottomY[z]);


            } // Moving barriers.

            // Draw the special.
            for (int p = 0; p < numSpecials; p++) {

                // If the special went off of the screen, move it back to the beginning.
                if (specX[p] < 0 - powerups[0].getWidth()) {

                    // Place the special.
                    specX[p] = Gdx.graphics.getWidth() * (randomNumber.nextInt(10)+1);
                    specY[p] = (randomNumber.nextInt(Gdx.graphics.getHeight())+1);;

                    // Give it a type.
                    setSpec[p] = randomNumber.nextInt(3);

                } // if special is off the screen, move it.
                else {

                    specX[p] = specX[p] - vehicleSpeed - increaseSpeed;
                } //else just move it.

                // Either way, let's draw it.
                batch.draw(powerups[setSpec[p]], specX[p], specY[p]);
                powerCircle.set(specX[p] + powerups[p].getHeight()/2,
                        specY[p] + powerups[p].getWidth()/2, powerups[p].getWidth());

                if (Intersector.overlaps(frontCircle, powerCircle) ||
                        Intersector.overlaps(rearCircle, powerCircle) ||
                        Intersector.overlaps(shipCircle, powerCircle)) {

                    // Play the coin sound!
                    id = soundOne.play();

                    if (setSpec[p] == 2) {

                        //Slow down by 25%
                        vehicleSpeed = vehicleSpeed * 3 / 4;

                    } else if (setSpec[p] == 1) {

                        //distortion, enter distortion mode.
                        timerDist = 2500;
                        spacialDistortion = true;

                    } else {

                        // Bonus
                        playerScore = playerScore + 2500;
                    }

                    specX[p] = -500;

                } // colided with power up.

            } // moving the special powerups.

            for (int z = 0; z < numBarriers; z++) {

                barrierRectangleTop.set(bothX[z], topY[z],
                        topBar.getWidth(),topBar.getHeight());
                //shapeRenderer.rect(barrierRectangleTop.x, barrierRectangleTop.y, // For visualizing collision shapes.
                //barrierRectangleTop.getWidth(), barrierRectangleTop.getHeight()); // For visualizing collision shapes.

                barrierRectangleBottom.set(bothX[z], bottomY[z],
                        bottomBar.getWidth(), bottomBar.getHeight());
                //shapeRenderer.rect(barrierRectangleBottom.x, barrierRectangleBottom.y, // For visualizing collision shapes.
                //barrierRectangleBottom.getWidth(), barrierRectangleBottom.getHeight()); // For visualizing collision shapes.

                if (spacialDistortion) {

                    // Don't crash! But lower the timer.
                    timerDist--;

                    // Display our score.
                    font.draw(batch, String.valueOf(timerDist), 100, 100);

                    // If our countdown timer reaches 0, turn off spacial distortion.
                    if (timerDist <= 0) {

                        spacialDistortion = false;

                    }

                } else {
                    if (Intersector.overlaps(frontCircle, barrierRectangleBottom) ||
                            Intersector.overlaps(frontCircle, barrierRectangleTop) ||
                            Intersector.overlaps(rearCircle, barrierRectangleBottom) ||
                            Intersector.overlaps(rearCircle, barrierRectangleTop) ||
                            Intersector.overlaps(shipCircle, barrierRectangleBottom) ||
                            Intersector.overlaps(shipCircle, barrierRectangleTop)) {

                        // Play the crash sound!
                        id = sound.play();

                        // Then we crashed!
                        waitBoolean = true;
                        waitTime = 60;
                        gameState = 2;

                    } // If we collided.
                }

            } // Collision shapes for barriers.

            // are they about to win?
            if (playerScore >= 25000) {

                // They are about to win! Give them the victory dance!

                winX = Gdx.graphics.getWidth() * 3;
                gameState = 3;

            }

		} // Gamestate is 1, or in play.

        if (gameState == 2) {

            // Let's log it! Testing only! // Gdx.app.log("WJH", "Game over!");
            blinkState = 6;

            // Draw the screen.
            batch.draw(dialogs[1], Gdx.graphics.getWidth()/2 - (dialogs[0].getWidth()/2), Gdx.graphics.getHeight()/2 - (dialogs[0].getHeight()/2));

            waitTime--;
            if (waitTime <= 0) {

                waitBoolean = false;

            }

            if (!waitBoolean) {

                if (Gdx.input.justTouched()) {

                    restartGame();

                }
            } // Touching the screen.

        } // gamestate is 2, or a crash condition!

        if (gameState == 3) {

            // We are now in play.
            // Determine our blink state for our ship.
            if (blinkState <= 4) {

                blinkState++;

            } else {

                blinkState = 0;

            }

            if (winX > 0) {

            // What to do when we touch the screen.
            if (Gdx.input.justTouched() && vehicleY <= Gdx.graphics.getHeight() - vehicles[0].getHeight()) {

                // push the space ship up.
                gravityDown = thrust;

            } // Touching the screen.

            // If we are moving above the bottom of the screen.
            if (vehicleY > 0 - (vehicles[0].getHeight() * .60) || gravityDown <= 0) { // In play.
                gravityDown++;
                vehicleY -= (gravityDown / 2);
            } // In play.

            // If we fall off the screen.
            if (vehicleY < 0 - (vehicles[0].getHeight() * .60)) {

                // Play the crash sound!
                id = sound.play();

                waitBoolean = true;
                waitTime = 60;
                gameState = 2;
            } // we fell off the screen.


            // This is how we move the barriers.
            for (int z = 0; z < numBarriers; z++) {

                // Since they are on the screen, let's move them.
                bothX[z] = bothX[z] - vehicleSpeed;

                // Either way, let's draw them.
                batch.draw(topBar, bothX[z], topY[z]);
                batch.draw(bottomBar, bothX[z], bottomY[z]);


            } // Moving barriers.



                winX = winX - vehicleSpeed;

            } // if not won yet.
            else {

                waitBoolean = true;
                waitTime = 60;
                gameState = 4;

            }

            batch.draw(winBG, winX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        } // Gamestate is 3, or victory conditions. //

        if (gameState == 4) {

            batch.draw(winBG, winX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // Draw the screen.
            batch.draw(dialogs[2], Gdx.graphics.getWidth()/2 - (dialogs[0].getWidth()/2), Gdx.graphics.getHeight()/2 - (dialogs[0].getHeight()/2));

            waitTime--;
            if (waitTime <= 0) {

                waitBoolean = false;

            }

            if (!waitBoolean) {

                if (Gdx.input.justTouched()) {

                    restartGame();

                }
            }

        } // Game state 4 //

        // Draw the vehicle, depending on state.
        if (criticalVelocity) {

            // Critical velocity
            batch.draw(criticalship[blinkState], vehicleX, vehicleY);

        } else if (spacialDistortion) {

            // Spacial Distortion
            batch.draw(distortship[blinkState], vehicleX, vehicleY);

        } else {

            // Regular vehicle.
            batch.draw(vehicles[blinkState], vehicleX, vehicleY);

        }

        if (gameState == 0) {

            // Determin our blink state for our ship.
            if (blinkState <= 4) {

                blinkState++;

            } else {

                blinkState = 0;

            }

            // Draw the screen.
            batch.draw(dialogs[0], Gdx.graphics.getWidth()/2 - (dialogs[0].getWidth()/2), Gdx.graphics.getHeight()/2 - (dialogs[0].getHeight()/2));


            if (Gdx.input.justTouched()) {

                waitBoolean = true;
                gameState = 5;

            }

        } // gamestate is 0, or not started yet.

        if (gameState == 5) {

            // Determin our blink state for our ship.
            if (blinkState <= 4) {

                blinkState++;

            } else {

                blinkState = 0;

            }

            // Draw the screen.
            batch.draw(dialogs[3], Gdx.graphics.getWidth()/2 - (dialogs[0].getWidth()/2), Gdx.graphics.getHeight()/2 - (dialogs[0].getHeight()/2));

            waitTime--;
            if (waitTime <= 0) {

                waitBoolean = false;

            }

            if (!waitBoolean) {

                if (Gdx.input.justTouched()) {

                gameState = 1;

                }
            }

        } // gamestate is 5, or Paused.

        // Display our score.
        font.draw(batch, String.valueOf(playerScore), 100, Gdx.graphics.getHeight() - 100);

		batch.end();

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // For visualizing collision shapes.
        //shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.BLACK); // For visualizing collision shapes.

        shipCircle.set(vehicleX + vehicles[blinkState].getWidth()/2,
                vehicleY + vehicles[blinkState].getHeight()/2,
                vehicles[blinkState].getHeight()/2);
        //shapeRenderer.circle(shipCircle.x,shipCircle.y,shipCircle.radius); // For visualizing collision shapes.

        frontCircle.set(vehicleX + vehicles[blinkState].getWidth() * 22/30,
                vehicleY + vehicles[blinkState].getHeight()/2,
                vehicles[blinkState].getHeight()/2);
        //shapeRenderer.circle(frontCircle.x,frontCircle.y,frontCircle.radius); // For visualizing collision shapes.

        rearCircle.set(vehicleX + vehicles[blinkState].getWidth() * 8/30,
                vehicleY + vehicles[blinkState].getHeight()/2,
                vehicles[blinkState].getHeight()/2);
        //shapeRenderer.circle(rearCircle.x,rearCircle.y,rearCircle.radius); // For visualizing collision shapes.



        //shapeRenderer.end(); // For visualizing collision shapes.

	} // Render

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
        bgMusic.dispose();
        sound.dispose();
        soundOne.dispose();
	}
}
