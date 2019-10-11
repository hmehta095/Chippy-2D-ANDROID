package com.example.tappyspaceship01;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.ArrayList;

public class Player {

    // PROPERTIES
    private Bitmap image;
    private Bitmap powerImage;
    private Rect hitbox;
    private Rect powerImageHitbox;

    public int xPosition;
    public int yPosition;
    public int width;
    private int speed;

    private ArrayList<Rect> bullets = new ArrayList<Rect>();
    private final int BULLET_WIDTH = 5;


    public Player(Context context, int x, int y) {
        // 1. set up the initial position of the Enemy
        this.xPosition = x;
        this.yPosition = y;

        // 2. Set the default image - all enemies have same image
        this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_ship);
        this.powerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.life);

        // 3. Set the default hitbox - all enemies have same hitbox
        this.hitbox = new Rect(
                this.xPosition,
                this.yPosition,
                this.xPosition + this.image.getWidth(),
                this.yPosition + this.image.getHeight()
        );
        this.powerImageHitbox = new Rect(
                this.xPosition,
                this.yPosition,
                this.xPosition + this.powerImage.getWidth(),
                this.yPosition + this.powerImage.getHeight()
        );
    }

    public Player(Context context, int x, int y, int width, int speed) {
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.speed = speed;
    }

    public Rect getPowerImageHitbox() {
        return powerImageHitbox;
    }

    public void setPowerImageHitbox(Rect powerImageHitbox) {
        this.powerImageHitbox = powerImageHitbox;
    }

    public Bitmap getPowerImage() {
        return powerImage;
    }

    public void setPowerImage(Bitmap powerImage) {
        this.powerImage = powerImage;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public ArrayList<Rect> getBullets() {
        return bullets;
    }

    public void setBullets(ArrayList<Rect> bullets) {
        this.bullets = bullets;
    }

    public int getBULLET_WIDTH() {
        return BULLET_WIDTH;
    }

    // GETTER AND SETTER METHODS
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Rect getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rect hitbox) {
        this.hitbox = hitbox;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public void updateHitbox() {
        this.hitbox.left = this.xPosition;
        this.hitbox.top = this.yPosition;
        this.hitbox.right = this.xPosition + this.image.getWidth();
        this.hitbox.bottom = this.yPosition + this.image.getHeight();
    }
    public void updatepowerHitbox() {
        this.powerImageHitbox.left = this.xPosition;
        this.powerImageHitbox.top = this.yPosition;
        this.powerImageHitbox.right = this.xPosition + this.powerImage.getWidth();
        this.powerImageHitbox.bottom = this.yPosition + this.powerImage.getHeight();
    }

    // Make a new bullet
    public void spawnBullet() {
        // make bullet come out of middle of enemty
        Rect bullet = new Rect(this.xPosition,
                this.yPosition + this.image.getHeight() / 2,
                this.xPosition + BULLET_WIDTH,
                this.yPosition + this.image.getHeight() / 2 + BULLET_WIDTH
        );
        this.bullets.add(bullet);
    }

}
