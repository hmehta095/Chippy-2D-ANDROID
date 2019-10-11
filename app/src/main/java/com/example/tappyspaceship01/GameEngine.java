package com.example.tappyspaceship01;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG="DINO-RAINBOWS";

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;

    Bitmap background;
    int bgXPosition = 0;
    int backgroundRightSide = 0;

    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;

    int blocklive = 10;

    Player player;
    int lives = 3;

    int SKULL_LIVE = 20;

//    ArrayList<Player> bullets = new ArrayList<Player>();


    Player powerImage;

    int SQUARE_WIDTH = 15;

    // -----------------------------------
    // GAME SPECIFIC VARIABLES
    // -----------------------------------
        EnemyPixel[] enemyPixels = new EnemyPixel[35];

        EnemyPixel[] enemyBullet = new EnemyPixel[8];
        EnemyPixel[] enemyBulletY = new EnemyPixel[15];
        EnemyPixel skull;
    // ----------------------------
    // ## SPRITES
    // ----------------------------

    // represent the TOP LEFT CORNER OF THE GRAPHIC

    // ----------------------------
    // ## GAME STATS
    // ----------------------------


    public GameEngine(Context context, int w, int h) {
        super(context);

        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;

        this.background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);

        this.background = Bitmap.createScaledBitmap(
                this.background,
                this.screenWidth,
                this.screenHeight,
                false
        );

        this.bgXPosition = 0;

        this.player = new Player(getContext(),100,100);


        final int min = 1;
        final int max = 7;
        final int random = new Random().nextInt((max - min) + 1) + min;
        powerImage = new Player(getContext(),random*1000,random*100);




        this.printScreenInfo();
        int xVal = 90,yVal = 100;

        for(int i = 0 ; i< enemyBullet.length;i++){
            if(i<5) {
                this.enemyBullet[i] = new EnemyPixel(getContext(), 4000, yVal + 100 * i);
            }
            else {
                this.enemyBullet[i] = new EnemyPixel(getContext(), 4000, 300 + 100 * i);

            }
        }

        for(int i = 0 ; i< enemyBulletY.length;i++){
            if(i<5) {
                this.enemyBulletY[i] = new EnemyPixel(getContext(), xVal+100*i, -4000);
            }
            else {
                this.enemyBulletY[i] = new EnemyPixel(getContext(), 400+100*i, -4000);

            }
        }



        for (int i = 0; i < enemyPixels.length; i++) {


            if (i < 7)
            {
                this.enemyPixels[i] = new EnemyPixel(getContext(), 1000 + i * xVal, yVal);
            }
            if (i >=7 && i< 14 )
            {
                this.enemyPixels[i] = new EnemyPixel(getContext(), 1000 + (i - 7) * xVal, yVal + 90);
            }
            if (i >=14 && i <= 21)
            {
                if (i == 17)
                {
                    skull = new EnemyPixel(getContext(),1000 + (i - 14) * xVal, yVal + 180);
                }

                    this.enemyPixels[i] = new EnemyPixel(getContext(), 1000 + (i - 14) * xVal, yVal + 180);


            }
            if (i >=21 && i <= 28)
            {
                this.enemyPixels[i] = new EnemyPixel(getContext(), 1000 + (i - 21) * xVal, yVal + 270);
            }
            if (i >=28 && i <= enemyPixels.length)
            {
                this.enemyPixels[i] = new EnemyPixel(getContext(), 1000 + (i - 28) * xVal, yVal + 350);
            }
        }



    }



    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }


    float mouseX ;
    float mouseY ;


    public void moveBulletToMouse(Bitmap bullet, float mouseXPos, float mouseYPos) {
        // @TODO:  Move the square
        // 1. calculate distance between bullet and square
        double a = (mouseXPos - player.getxPosition());
        double b = (mouseYPos - player.getyPosition());
        double distance = Math.sqrt((a*a) + (b*b));

        // 2. calculate the "rate" to move
        double xn = (a / distance);
        double yn = (b / distance);

        // 3. move the bullet
        player.setxPosition(player.getxPosition() + (int)(xn * 15));
        player.setyPosition(player.getyPosition() + (int)(yn * 15));
        player.updateHitbox();


    }


    private void spawnPlayer() {
        //@TODO: Start the player at the left side of screen


//        int baseSpeed = 2;
//        for (int i = 1; i < 100; i++) {
//            Player b = new Player(getContext(), this.player.getxPosition(), this.player.getyPosition() +50, SQUARE_WIDTH, 50);
//            this.bullets.add(b);
//        }
    }
    private void spawnEnemyShips() {
        Random random = new Random();

        //@TODO: Place the enemies in a random location

    }

    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------


    int numLoops = 0;

    public void updatePositions() {

        // UPDATE BACKGROUND POSITION
        // 1. Move the background
        this.bgXPosition = this.bgXPosition - 50;

        backgroundRightSide = this.bgXPosition + this.background.getWidth();
        // 2. Background collision detection
        if (backgroundRightSide < 0) {
            this.bgXPosition = 0;
        }

        this.powerImage.setxPosition(this.powerImage.getxPosition()-25);
        this.powerImage.updatepowerHitbox();



//        Move Player
        moveBulletToMouse(player.getImage(), this.mouseX, this.mouseY);






        numLoops = numLoops + 1;


        // DEAL WITH BULLETS

        // Shoot a bullet every (5) iterations of the loop
        if (numLoops % 2  == 0) {
            this.player.spawnBullet();
            spawnPlayer();
        }


        // MOVING THE BULLETS
        int BULLET_SPEED = 50;
        for (int i = 0; i < this.player.getBullets().size();i++) {
            Rect bullet = this.player.getBullets().get(i);
            bullet.left = bullet.left + BULLET_SPEED;
            bullet.right = bullet.right + BULLET_SPEED;
        }


        for(int i=0;i<this.player.getBullets().size();i++){
            Rect bullet = this.player.getBullets().get(i);

            if (this.skull.getHitbox().intersect(bullet)){
               SKULL_LIVE = SKULL_LIVE-1;
               skull.updateHitbox();

               if (SKULL_LIVE < 1){
                   this.skull =  new EnemyPixel(getContext(), -100 , -100);
               }

            }



            for (int j = 0; j < enemyPixels.length;j++)
            {
                if (this.enemyPixels[j].getHitbox().intersect(bullet))
                {

                    blocklive = blocklive-1;
                    if (blocklive<=1){
                        enemyPixels[j] =  new EnemyPixel(getContext(), -100 , -100);
                        blocklive = 10;


                    }
                    if(blocklive<10 && blocklive > 1  ){
                    enemyPixels[j].updateHitbox();}
                    this.player.getBullets().remove(bullet);


                }

                if (this.enemyPixels[j].getHitbox().intersect(player.getHitbox())){
                    lives = lives-1;
                    this.player = new Player(getContext(),100,100);
                    enemyPixels[j].updateHitbox();

                }

            }
            for (int j = 0; j < enemyBulletY.length;j++)
            {

                if (this.enemyBulletY[j].getHitbox().intersect(player.getHitbox())){
                    lives = lives-1;
                    this.player = new Player(getContext(),100,100);
                    enemyBulletY[j].updateHitbox();
                    this.mouseX=100;
                    this.mouseY=100;

                }

            }
            for (int j = 0; j < enemyBullet.length;j++)
            {

                if (this.enemyBullet[j].getHitbox().intersect(player.getHitbox())){
                    lives = lives-1;
                    this.player = new Player(getContext(),100,600);
                    enemyBullet[j].updateHitbox();
                    this.mouseX=100;
                    this.mouseY=600;

                }

                if(this.enemyBullet[j].getHitbox().intersect(bullet)) {
                    this.player.getBullets().remove(bullet);
                }

            }

            if(this.powerImage.getxPosition()<0){
                final int min = 1;
                final int max = 7;
                final int random = new Random().nextInt((max - min) + 1) + min;
                powerImage = new Player(getContext(),random*1000,random*100);
            }

            if(this.player.getHitbox().intersect(powerImage.getPowerImageHitbox())){
                lives = lives +5;
                final int min = 1;
                final int max = 7;
                final int random = new Random().nextInt((max - min) + 1) + min;
                powerImage = new Player(getContext(),random*1000,random*100);
            }


            if(SKULL_LIVE<1){
                Intent intent = new Intent(this.getContext(),Win.class);
                this.getContext().startActivity(intent);
            }
            if(lives<1){
                Intent intent = new Intent(this.getContext(),LoseScreen.class);
                this.getContext().startActivity(intent);
            }

        }



//        for (int i = 0; i < this.bullets.size();i++) {
//            Player b = this.bullets.get(i);
//            moveBulletToMouse(b, this.mouseX, this.mouseY);
//        }


//        if(blocklive < 5){
//            for(int i = 0 ; i< enemyBullet.length;i++){
//
//                this.enemyBullet[i] = new EnemyPixel(getContext(), 1000-10 , 100+100*i);
//
//            }
//        }


//          move enemy bullet right to left

//        if (blocklive<8) {

            for (int i = 0; i < this.enemyBullet.length; i++) {
                EnemyPixel image = this.enemyBullet[i];
                image.setxPosition(image.getxPosition() - 20);
                image.updateHitbox();

                if (image.getxPosition()<0){
                    image.setxPosition(screenWidth+1000);
                    image.updateHitbox();
                }
            }
//        }


//              move enemy bullet top to bottom
        for (int i = 0; i < this.enemyBulletY.length; i++) {
            EnemyPixel image = this.enemyBulletY[i];
            image.setyPosition(image.getyPosition() + 20);
            image.updateHitbox();

            if (image.getyPosition()>screenHeight){
                image.setyPosition(-4000);
                image.updateHitbox();
            }
        }






    }





    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.WHITE);




            // DRAW THE PLAYER HITBOX
            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.BLUE);




            // DRAW THE BACKGROUND
            // -----------------------------
            canvas.drawBitmap(this.background,
                    this.bgXPosition,
                    0,
                    paintbrush);

            canvas.drawBitmap(this.background,
                    backgroundRightSide,
                    0,
                    paintbrush);


            paintbrush.setColor(Color.WHITE);


            for (int i =0; i<enemyPixels.length;i++)
            {
                canvas.drawBitmap(enemyPixels[i].getImage(), enemyPixels[i].getxPosition(), enemyPixels[i].getyPosition(), paintbrush);
                // draw the player's hitbox
                canvas.drawRect(enemyPixels[i].getHitbox(), paintbrush);
            }

            for (int i =0; i<enemyBullet.length;i++)
            {
                canvas.drawBitmap(enemyBullet[i].getImage1(), enemyBullet[i].getxPosition(), enemyBullet[i].getyPosition(), paintbrush);
                // draw the player's hitbox
                canvas.drawRect(enemyBullet[i].getBulletHitbox(), paintbrush);
            }
            for (int i =0; i<enemyBulletY.length;i++)
            {
                canvas.drawBitmap(enemyBulletY[i].getImage1(), enemyBulletY[i].getxPosition(), enemyBulletY[i].getyPosition(), paintbrush);
                // draw the player's hitbox
                canvas.drawRect(enemyBulletY[i].getBulletHitbox(), paintbrush);
            }


            paintbrush.setTextSize(55);
            canvas.drawText("Lives Remaining: " + lives,this.screenWidth-550,80,paintbrush);


//            paintbrush.setColor(Color.RED);
            canvas.drawBitmap(skull.getImage(), skull.getxPosition(), skull.getyPosition(), paintbrush);
            // draw the player's hitbox
            skull.setImage(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.skull));
            canvas.drawRect(skull.getHitbox(), paintbrush);




//            paintbrush.setColor(Color.BLACK);
            canvas.drawBitmap(player.getImage(), player.getxPosition(), player.getyPosition(), paintbrush);
            canvas.drawRect(player.getHitbox(), paintbrush);

            canvas.drawBitmap(powerImage.getPowerImage(), powerImage.getxPosition(), powerImage.getyPosition(), paintbrush);
            canvas.drawRect(powerImage.getPowerImageHitbox(), paintbrush);

            // draw bullet on screen
            for (int i = 0; i < this.player.getBullets().size(); i++) {
                Rect bullet = this.player.getBullets().get(i);
                canvas.drawRect(bullet, paintbrush);
            }
//
//            for (int i = 0; i < this.bullets.size();i++) {
//                Player b = this.bullets.get(i);
//                canvas.drawRect(
//                        b.getxPosition(),
//                        b.getyPosition(),
//                        b.getxPosition() + b.getWidth(),
//                        b.getyPosition() + b.getWidth(),
//                        paintbrush
//                );
//            }

            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setFPS() {
        try {
            gameThread.sleep(120);
        }
        catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------


    String fingerAction = "";

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
//        float fingerXPosition = event.getX();
//        float fingerYPosition = event.getY();
        if (userAction == MotionEvent.ACTION_DOWN) {

            this.mouseX = event.getX();
            this.mouseY = event.getY();

//            if(fingerYPosition <= this.screenHeight/2){
//                //move racket left
//                player.setyPosition(player.getyPosition() - 100);
//                player.updateHitbox();
//
//            }
//            else if(fingerYPosition > this.screenHeight/2){
//                //move racket right
//                player.setyPosition(player.getyPosition() + 100);
//                player.updateHitbox();
//            }
        }
        else if (userAction == MotionEvent.ACTION_UP) {

        }

        return true;
    }
}
