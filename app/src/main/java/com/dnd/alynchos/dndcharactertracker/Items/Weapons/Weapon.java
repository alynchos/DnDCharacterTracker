package com.dnd.alynchos.dndcharactertracker.Items.Weapons;

import com.dnd.alynchos.dndcharactertracker.Items.Item;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 * Base Weapon Class
 */
public class Weapon extends Item {
    public int hit;
    public int hit_bonus;
    public int range;                  /* Measured in feet */
    public boolean isProficient;
    public List<Damage> damages = new LinkedList<>();

    public Weapon(){}

    public Weapon(Item item){
        this.name = item.name;
        this.weight = item.weight;
        this.amount = item.amount;
        this.desc = item.desc;
        this.gold_value = item.gold_value;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Weapon: ")
                .append(name)
                .append(" ")
                .append(amount)
                .append(" ")
                .append(weight)
                .append(" ")
                .append(gold_value)
                .append(" ")
                .append(desc)
                .append(" +")
                .append(hit + hit_bonus)
                .append(" ");
        for(Damage dam : damages) {
            sb.append(dam).append("\n");
        }
        return sb.toString();
    }

}
