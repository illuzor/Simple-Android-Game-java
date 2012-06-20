package com.illuzor.sag;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.illuzor.sag.constants.ItemType;
import com.illuzor.sag.content.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author illuzor
 * игровой  SurfaceView
 * Тут просчтитывается и отрисовывается игровая графика
 */

public class GameView extends SurfaceView implements Runnable {

    public int platformX;
    boolean isRuning, drawingStarted, vibroEnabled = false;
    int score, itemSize, iterator, levelCounter, maxScore, vibroDuration, platformDistanation, movingStep, stageWidth, speed = 0;
    int level = 1;
    int itemsIterval = 210; // интервал появления айтемов
    List<Item> items = new ArrayList<Item>(); // массив с айтемами
    SurfaceHolder surfaceHolder;
    Thread gameThread = null;
    GameScreen gameActivity;
    private Context context;

    public GameView(Context context, GameScreen gameActivity) {
        super(context);
        this.context = context;
        this.gameActivity = gameActivity;
        surfaceHolder = getHolder();
        setVibroType();
    }

    // задаём тип вибрации
    private void setVibroType() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(gameActivity.getBaseContext());

        int vibroType = Integer.parseInt(prefs.getString("vibro", "0"));

        switch (vibroType) {
            case 0:
                vibroEnabled = false;
                break;
            case 1:
                vibroEnabled = true;
                vibroDuration = 150;
                break;
            case 2:
                vibroEnabled = true;
                vibroDuration = 600;
                break;
        }
    }

    public void pause() {
        isRuning = false;
        while (true) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        gameThread = null;
    }

    public void resume() {
        isRuning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // тут всё отрисовывается
    @Override
    public void run() {
        while (isRuning) {
            if (!surfaceHolder.getSurface().isValid())
                continue;

            Canvas gameCanvas = surfaceHolder.lockCanvas(); // игровой канвас, на котором рисуется вся графика
            gameCanvas.drawColor(Color.BLACK);

            if (iterator <= itemsIterval) {
                iterator++; // счётчик интервала появления айтема
            } else {
                defineItem(gameCanvas.getWidth()); // добавление айтема
                iterator = 0; // обнуляем счётчик
                levelCounter++;  // увеличиваем уровень
                if (levelCounter == 20) defineLevel();
            }

            Paint platformPaint = new Paint(); // пеинт для платформы и ниже пеинты для текстов
            platformPaint.setColor(Color.GREEN);

            Paint scoreTextPaint = new Paint();
            scoreTextPaint.setColor(Color.WHITE);
            scoreTextPaint.setTextAlign(Paint.Align.LEFT);
            scoreTextPaint.setTextSize(30);

            Paint levelTextPaint = new Paint();
            levelTextPaint.setColor(Color.WHITE);
            levelTextPaint.setTextAlign(Paint.Align.RIGHT);
            levelTextPaint.setTextSize(30);

            int platformY = (int) (gameCanvas.getHeight() * 0.94); // задаём размер платформы

            if (!drawingStarted) { // задаём некоторые начальные параметры
                stageWidth = gameCanvas.getWidth();
                itemSize = (int) Math.round(stageWidth * 0.03);
                platformX = platformDistanation = stageWidth / 2;
                movingStep = Math.round(stageWidth / 56);
                drawingStarted = true;
            }

            if (platformDistanation < platformX) {  // изменяе положение платформы по .x
                if (platformX > platformDistanation) platformX -= movingStep;
            } else {
                if (platformX < platformDistanation) platformX += movingStep;
            }

            // чтоб платформа не дёргалась
            if (Math.max(platformX, platformDistanation) - Math.min(platformX, platformDistanation) < movingStep)
                platformX = platformDistanation;

            // в цикле проверяется массив с айтемами, происходит их отрисовка в канвас
            // также проеряются коллизии и уход айтема за экран
            for (int i = 0; i < items.toArray().length; i++) {
                Item currentItem = items.get(i); // берём айтем из массива

                Paint currentPaint = new Paint();
                currentPaint.setColor(currentItem.color); // пеинт с цветом из айтема

                if (currentItem.currentY > gameCanvas.getHeight()) { // если айтем ушёл вниз экрана
                    if (currentItem.itemType.equals(ItemType.GOOD)) score--; // и при этом оказался красным, убавляем очко
                    items.remove(i); // удаляем айтем из массива
                }

                // проверяем столкновение с платформаой
                if (checkCollision(currentItem.currentY, currentItem.currentX, platformY, stageWidth/5)) {
                    defineCollisionScore(currentItem.itemType);
                    items.remove(i);
                }
                if (currentItem.itemType != ItemType.EVIL) {
                    gameCanvas.drawRect(new Rect(currentItem.currentX, currentItem.currentY, currentItem.currentX + itemSize, currentItem.currentY + itemSize), currentPaint);
                } else{
                    gameCanvas.drawCircle(currentItem.currentX, currentItem.currentY, itemSize/2, currentPaint);
                }
                currentItem.update();
            }

            if (maxScore < score) maxScore = score;
            if (score < -5) gameActivity.showScores(maxScore);  // проигрыщ

            // рисуем платформу и текстовые поля
            Rect platformRect = new Rect(platformX - stageWidth/5 / 2, platformY - 10, platformX + stageWidth/5 / 2, platformY + 10);
            gameCanvas.drawRect(platformRect, platformPaint);
            gameCanvas.drawText("SCORE: " + score, 5, 45, scoreTextPaint);
            gameCanvas.drawText("LEVEL: " + level, gameCanvas.getWidth() - 5, 45, levelTextPaint);

            surfaceHolder.unlockCanvasAndPost(gameCanvas);
        }
    }

    // задаём .x координату назначения для платформы
    public void movePlatformTo(int xCoordinate) {
        platformDistanation = xCoordinate;
    }

    // задаём .x координату назначения для платформы
    public void movePlatformTo(double aspect) {
        int min = Math.min((int) Math.round(aspect * stageWidth), platformDistanation);
        int max = Math.max((int) Math.round(aspect * stageWidth), platformDistanation);
        if (max - min > 3) platformDistanation = (int) Math.round(aspect * stageWidth);
    }

    // новый уровень, ускоряем появление айтемов
    private void defineLevel() {
        levelCounter = 0;
        level++;
        itemsIterval -= level * 22;
        if (itemsIterval < 30) itemsIterval = 30;
    }

    // проверка столкновения айтема с платформой по их координатам. true - столкновение есть, false - нет столкновения
    private boolean checkCollision(int itemY, int itemX, int platformY, int platformWidth) {
        return itemY > platformY - itemSize && itemY < platformY + 20 && itemX > platformX - platformWidth / 2 && itemX < platformX + platformWidth / 2;
    }

    //создаём новый айтем случайного типа и доавляем его в массив
    private void defineItem(int canvasWidth) {
        double randomRange = Math.random();
        String itemType = ItemType.GOOD;
        if (randomRange > 0.65 && randomRange < .95) {
            itemType = ItemType.EVIL;
        } else if (randomRange > .95) {
            itemType = ItemType.VERY_GOOD;
        }
        items.add(new Item(itemType, level + 1, new Random().nextInt(canvasWidth - itemSize)));
    }

    //  расчёт количества очков при столкновении. красный круг - минус одно, зелёный квадрат - пюс одно, синий квадрат - плюс пять
    private void defineCollisionScore(String itemType) {
        if (itemType.equals(ItemType.GOOD)) {
            score++;
        } else if (itemType.equals(ItemType.VERY_GOOD)) {
            score += 5;
        } else if (itemType.equals(ItemType.EVIL)) {
            score--;
            if (vibroEnabled) {
                Vibrator vibro = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibro.vibrate(vibroDuration);
            }
        }
    }

}