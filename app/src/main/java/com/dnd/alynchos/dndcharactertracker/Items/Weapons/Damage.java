package com.dnd.alynchos.dndcharactertracker.Items.Weapons;

/**
 * Created by Alex Lynchosky on 9/27/2016.
 * Class to store all types of damage
 */
public class Damage {
    public int dice_num;
    public int dice_size;
    public int flat_damage;
    public String damage_type;
    public int dc_save;
    public String dc_save_type;

    public Damage(int dice_num, int dice_size, int flat_damage, String damage_type, int dc_save, String dc_save_type) {
        this.dice_num = dice_num;
        this.dice_size = dice_size;
        this.flat_damage = flat_damage;
        this.damage_type = damage_type;
        this.dc_save = dc_save;
        this.dc_save_type = dc_save_type;
    }

    @Override
    public String toString(){
        return dice_num + "d" + dice_size + " +" + flat_damage + " " + damage_type + (dc_save > 0 ? (" with DC " + dc_save + " " + dc_save_type + " save.") : ".");
    }
}
