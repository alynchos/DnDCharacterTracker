package com.dnd.alynchos.dndcharactertracker.Items;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 * An Item in the character's inventory
 */
public class Item {

    public double weight;
    public double gold_value;
    public String desc = " ";
    public String name;
    public int amount;

    @Override
    public String toString(){
        return "Item: " + name + " " + amount + " " + weight + " " + gold_value+  " " + desc;
    }
}
