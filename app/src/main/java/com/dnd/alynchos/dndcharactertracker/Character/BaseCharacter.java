package com.dnd.alynchos.dndcharactertracker.Character;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.Items.Item;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alex Lynchosky on 12/22/2014.
 * The core class for building a character
 */
public class BaseCharacter {

    /* Debugging */
    private static final String TAG = BaseCharacter.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    public String name = " ";
    public String race = " ";
    public String class_ = " ";
    public String align = " ";
    public String background = " ";
    public int age;
    public int health, armor, initiative, speed;
    public List<String> weapons = new LinkedList<>();
    public List<String> ammo = new LinkedList<>();
    public int str, dex, con, intel, wis, chr;
    public int plat, gold, silver, copper;
    public int experience, level = 1;
    public int proficiency;
    public String personality_traits, ideals, bonds, flaws;
    public HashMap<String, Item> inventory = new HashMap<>();
    public String notes;

    public int saveProf[] = {0, 0, 0, 0, 0, 0}; /* How proficient the character is in each saving throw (size 6)*/
    public int skillProf[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; /* How proficient the character is in each skill (size 18) */

    private final int CARRY_COEF = 15;

    public enum Stat {
        Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma
    }

    public enum Skill {
        Acrobatics, AnimalHandling, Arcana, Athletics, Deception, History, Insight,
        Intimidation, Investigation, Medicine, Nature, Perception, Performance,
        Persuasion, Religion, SleightOfHand, Stealth, Survival
    }

    /**
     * *************************************************************
     * **************** Public Setter Functions ********************
     * *************************************************************
     */

    public void setSkillProf(Skill skill, int val) {
        skillProf[getSkillIndex(skill)] = val;
    }

    public void setSkillProfs(int skillProfs[]) {
        if (skillProfs.length != 18) {
            logger.debug("Incorrect skill array length: " + skillProfs.length);
            return;
        }
        for (int i = 0; i < skillProfs.length; i++) {
            skillProf[i] = skillProfs[i];
        }
    }

    /**
     * *************************************************************
     * **************** Public Getter Functions ********************
     * *************************************************************
     */

    public int getAbilityModifier(Stat ability) {
        int mod = getStat(ability);
        int val = (int) Math.floor(((double) (mod / 2) - 5));
        return val;
    }

    public int getSkillMod(Stat ability, Skill skill) {
        int val = getAbilityModifier(ability);
        int prof = getSkillProficiency(skill);
        val += prof * proficiency;
        return val;
    }

    public int getSavingThrow(Stat ability) {
        int val = getAbilityModifier(ability);
        int prof = getSaveProficiency(ability);
        val += prof * proficiency;
        return val;
    }


    /*
     *  **********************************************************
     *  Inventory Management
     *  **********************************************************
     */

    public void addItemToInventory(Item new_item) {
        inventory.put(new_item.name, new_item);
    }

    public void deleteInventory(){inventory = new HashMap<>();}

    public void deleteItem(String name){inventory.remove(name);}

    public int getCarryCap() {
        int cap = CARRY_COEF * getStat(Stat.Strength);
        return cap;
    }

    public int getCarryWeight(){
        int total = 0;
        Item set[] = inventory.values().toArray(new Item[inventory.size()]);
        for(int i =0; i < set.length; i++){
            int curr_amount = set[i].amount;
            double curr_weight = set[i].weight;
            total += (int)Math.floor(curr_amount * curr_weight);
        }
        return total;
    }

    public HashMap<String, Item> getInventory() {
        return inventory;
    }

    public Item getItem(String name){
        Item item = inventory.get(name);
        if(item == null){
            logger.debug("Could not find item: " + name);
        }
        return inventory.get(name);
    }

    public void modItem(String name, Item item){
        inventory.remove(name);
        inventory.put(item.name, item);
    }

    /**
     * *************************************************************
     * **************** Private Helper Functions *******************
     * *************************************************************
     */


    private int getStat(Stat ability) {
        int mod = 0;
        switch (ability) {
            case Strength:
                mod = str;
                break;
            case Dexterity:
                mod = dex;
                break;
            case Constitution:
                mod = con;
                break;
            case Intelligence:
                mod = intel;
                break;
            case Wisdom:
                mod = wis;
                break;
            case Charisma:
                mod = chr;
                break;
        }
        return mod;
    }

    private int getStatIndex(Stat ability) {
        int index = 0;
        switch (ability) {
            case Strength:
                index = 0;
                break;
            case Dexterity:
                index = 1;
                break;
            case Constitution:
                index = 2;
                break;
            case Intelligence:
                index = 3;
                break;
            case Wisdom:
                index = 4;
                break;
            case Charisma:
                index = 5;
                break;
        }
        return index;
    }

    private int getSkillIndex(Skill skill) {
        int index = 0;
        switch (skill) {
            case Acrobatics:
                index = 0;
                break;
            case AnimalHandling:
                index = 1;
                break;
            case Arcana:
                index = 2;
                break;
            case Athletics:
                index = 3;
                break;
            case Deception:
                index = 4;
                break;
            case History:
                index = 5;
                break;
            case Insight:
                index = 6;
                break;
            case Intimidation:
                index = 7;
                break;
            case Investigation:
                index = 8;
                break;
            case Medicine:
                index = 9;
                break;
            case Nature:
                index = 10;
                break;
            case Perception:
                index = 11;
                break;
            case Performance:
                index = 12;
                break;
            case Persuasion:
                index = 13;
                break;
            case Religion:
                index = 14;
                break;
            case SleightOfHand:
                index = 15;
                break;
            case Stealth:
                index = 16;
                break;
            case Survival:
                index = 17;
                break;
        }
        return index;
    }

    /* Returns the coefficient of the proficiency of the given skill */
    private int getSkillProficiency(Skill skill) {
        return skillProf[getSkillIndex(skill)];
    }

    /* Returns the coefficient of the proficiency of the given saving throw */
    private int getSaveProficiency(Stat ability) {
        return saveProf[getStatIndex(ability)];
    }
}
