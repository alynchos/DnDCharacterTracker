package com.dnd.alynchos.dndcharactertracker.Items.Weapons;

import com.dnd.alynchos.dndcharactertracker.Items.Item;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 */
public class Shortbow extends Weapon {

    public Shortbow() {
        name = "Shortbow";
        weight = 3;
        gold_value = 2;
        dice_num = 1;
        dice_size = 6;
        flat_damage = 3;
        damage_type = "piercing";
        range = 80;
        desc = "A simple shortbow. You can shoot the shortbow up to 80ft, or up to 320 ft at a disadvantage.";
    }

}
