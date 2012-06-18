package com.illuzor.sag.content;


import android.graphics.Color;
import com.illuzor.sag.constants.ItemType;

public class Item {

    public String itemType;
    public int currentY, currentX;
    public int color;
    int speed;

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

    public void update() {
        currentY += speed;
    }
}
