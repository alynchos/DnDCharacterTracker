package com.dnd.alynchos.dndcharactertracker.Items.Weapons;

import com.dnd.alynchos.dndcharactertracker.Items.Item;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public class Weapon extends Item {

    public int dice_num;
    public int dice_size;
    public int flat_damage;
    public int range;                  /* Measured in feet */
    public String damage_type;

    public Weapon(){}

    public Weapon(Item item){
        this.name = item.name;
        this.weight = item.weight;
        this.amount = item.amount;
        this.desc = item.desc;
        this.gold_value = item.gold_value;
    }

    public String getDamage(){
        String damage = dice_num + "d" + dice_size + " + " + flat_damage + " " + damage_type;
        return damage;
    }

    @Override
    public String toString(){
        return " Weapon: " + name + " " + amount + " " + weight + " " + gold_value+  " " + desc +
                dice_num + "d" + dice_size + " +" + flat_damage + " " + damage_type + " at " +
                range + " ft.";
    }

}
