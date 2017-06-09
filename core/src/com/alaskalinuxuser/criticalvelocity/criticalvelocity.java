package com.alaskalinuxuser.criticalvelocity;

import com.badlogic.gdx.ApplicationAdapter;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class criticalvelocity extends ApplicationAdapter {

	SpriteBatch batch;
    //ShapeRenderer shapeRenderer; // For visualizing collision shapes.
	Texture background, winBG;
	Texture[] vehicles, dialogs;
	Texture topBar, bottomBar;
	int blinkState, gameState, gapSize, maxOffset, numBarriers, spacing, thrust, playerScore;
	float vehicleY, vehicleX, gravityDown, vehicleSpeed, increaseSpeed, winX;
    float[] bothX, bottomY, topY;
    int[] eachOffset;
    long id;
    Random randomNumber;
    Circle shipCircle, frontCircle, rearCircle;
    Rectangle barrierRectangleTop, barrierRectangleBottom;
    BitmapFont font;
    Sound sound;
    Music bgMusic;

    private Viewport viewport;
    private Camera camera;

	@Override
	public void create () {

        camera = new PerspectiveCamera();
        viewport = new StretchViewport(480, 800, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth/2,camera.viewportHeight/2,0);

        // Sound files!
        Audio audio = Gdx.audio;
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("spblk.mp3"));
        sound = Gdx.audio.newSound(Gdx.files.internal("crash.mp3"));

        //shapeRenderer = new ShapeRenderer(); // For visualizing collision shapes.
        shipCircle = new Circle();
        frontCircle = new Circle();
        rearCircle = new Circle();
        barrierRectangleTop = new Rectangle();
        barrierRectangleBottom = new Rectangle();

        // Add our font.
        font = new BitmapFont();
        // Set our scale.
        font.getData().scale(5f);

		batch = new SpriteBatch();
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

        dialogs = new Texture[3];
        dialogs[0] = new Texture("dialogbegin.png");
        dialogs[1] = new Texture("gameoverBox.png");
        dialogs[2] = new Texture("gamewinBox.png");


        // Game variables....
        gapSize = 450;
        maxOffset = (Gdx.graphics.getHeight()/2 - gapSize);
        spacing = (Gdx.graphics.getWidth()*2/3) + (gapSize/2);
        increaseSpeed = .1f;
        thrust = -25;
        numBarriers = 4;

        // My arrays....
        eachOffset = new int[numBarriers];
        bothX = new float[numBarriers];
        topY = new float[numBarriers];
        bottomY = new float[numBarriers];

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

        // For loop to build the barriers.
        for (int z = 0; z < numBarriers; z++) {

            bottomY[z] = Gdx.graphics.getHeight()/2 - bottomBar.getHeight() - randomNumber.nextInt(maxOffset);
            topY[z] = bottomY[z] + bottomBar.getHeight() + gapSize;
            bothX[z] = Gdx.graphics.getWidth()/2 + Gdx.graphics.getWidth() + (z * spacing);

        }

    } // Restart Game.

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

	@Override
	public void render () {

        // Set up to render a red screen.
        Gdx.gl.glClearColor(1, 0, 0, 1);
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

                gameState = 2;
            } // we fell off the screen.


            // This is how we move the barriers.
            for (int z = 0; z < numBarriers; z++) {

                if (bothX[z] < 0 - topBar.getWidth()) {

                    // Since it went off the screen, move it to the beginning again.
                    bothX[z] = (numBarriers * spacing) - topBar.getWidth();

                    // And increase the speed.
                    vehicleSpeed = vehicleSpeed + increaseSpeed;

                } else {

                    // Since they are on the screen, let's move them.
                    bothX[z] = bothX[z] - vehicleSpeed;

                } // moving barriers.

                // This is how we move the barriers.
                for (int x = 0; x < numBarriers; x++) {

                    if (bothX[x] < vehicleX - topBar.getWidth()) {

                        // Because the user made it past that, let's give them a score bonus!
                        playerScore = playerScore + 1;

                    } // scoring barriers.
                }

                // Either way, let's draw them.
                batch.draw(topBar, bothX[z], topY[z]);
                batch.draw(bottomBar, bothX[z], bottomY[z]);


            } // Moving barriers.

            for (int z = 0; z < numBarriers; z++) {

                barrierRectangleTop.set(bothX[z], topY[z],
                        topBar.getWidth(),topBar.getHeight());
                //shapeRenderer.rect(barrierRectangleTop.x, barrierRectangleTop.y, // For visualizing collision shapes.
                //barrierRectangleTop.getWidth(), barrierRectangleTop.getHeight()); // For visualizing collision shapes.

                barrierRectangleBottom.set(bothX[z], bottomY[z],
                        bottomBar.getWidth(), bottomBar.getHeight());
                //shapeRenderer.rect(barrierRectangleBottom.x, barrierRectangleBottom.y, // For visualizing collision shapes.
                //barrierRectangleBottom.getWidth(), barrierRectangleBottom.getHeight()); // For visualizing collision shapes.

                if (Intersector.overlaps(frontCircle, barrierRectangleBottom) ||
                        Intersector.overlaps(frontCircle, barrierRectangleTop) ||
                        Intersector.overlaps(rearCircle, barrierRectangleBottom) ||
                        Intersector.overlaps(rearCircle, barrierRectangleTop) ||
                        Intersector.overlaps(shipCircle, barrierRectangleBottom) ||
                        Intersector.overlaps(shipCircle, barrierRectangleTop)) {

                    // Play the crash sound!
                    id = sound.play();

                    // Then we crashed!
                    gameState = 2;

                } // If we collided.

            } // Collision shapes for barriers.

            // are they about to win?
            if (playerScore >= 25000) {

                // They are about to win! Give them the victory dance!

                winX = Gdx.graphics.getWidth() * 3;
                gameState = 3;

            }

		} // Gamestate is 1, or in play.

        else if (gameState == 2) {

            // Let's log it! Testing only! // Gdx.app.log("WJH", "Game over!");
            blinkState = 6;

            // Draw the screen.
            batch.draw(dialogs[1], Gdx.graphics.getWidth()/2 - (dialogs[0].getWidth()/2), Gdx.graphics.getHeight()/2 - (dialogs[0].getHeight()/2));



            // What to do when we touch the screen.
            if (Gdx.input.justTouched()) {

                // Restart the game....
                restartGame();

            } // Touching the screen.

        } // gamestate is 2, or a crash condition!

        else if (gameState == 3) {

            // We are now in play.
            // Determin our blink state for our ship.
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

                gameState = 4;

            }

            batch.draw(winBG, winX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        } // Gamestate is 3, or victory conditions.

        else if (gameState == 4) {

            batch.draw(winBG, winX, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // Draw the screen.
            batch.draw(dialogs[2], Gdx.graphics.getWidth()/2 - (dialogs[0].getWidth()/2), Gdx.graphics.getHeight()/2 - (dialogs[0].getHeight()/2));

            if (Gdx.input.justTouched()) {

                restartGame();

            }

        }

        // Draw the vehicle.
		batch.draw(vehicles[blinkState], vehicleX , vehicleY);

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

                gameState = 1;

            }

        } // gamestate is 0, or not started yet.

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
	}
}
