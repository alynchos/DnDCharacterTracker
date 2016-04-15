package com.dnd.alynchos.dndcharactertracker.Items.Armor;

import com.dnd.alynchos.dndcharactertracker.Items.Item;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public abstract class Armor extends Item {

    int defense;
    String weight_class;

    public int getDefense(){return defense;}

    public String getWeightClass(){return weight_class;}
}
