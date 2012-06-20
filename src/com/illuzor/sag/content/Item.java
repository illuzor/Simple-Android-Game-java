package com.illuzor.sag.content;


import android.graphics.Color;
import com.illuzor.sag.constants.ItemType;

/**
 * @author illuzor
 *
 * Класс айтема. Хранит данные о положении и скорости
 */

public class Item {

    public String itemType; // тип айтема
    public int currentY, currentX; // координаты
    public int color; // цвет
    int speed; // скорость

    public Item(String itemType, int speed, int x) {
        this.itemType = itemType;
        this.speed = speed;
        currentX = x;

        if (itemType.equals(ItemType.GOOD)) {
            color = Color.GREEN;
        } else if(itemType.equals(ItemType.VERY_GOOD)){
            color = Color.BLUE;
        }   else if(itemType.equals(ItemType.EVIL)){
            color = Color.RED;
        }
    }

    // увеличиваем значение .y координаты
    public void update() {
        currentY += speed;
    }
}
