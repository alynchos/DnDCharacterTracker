package com.dnd.alynchos.dndcharactertracker.Character;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.R;
import com.dnd.alynchos.dndcharactertracker.SaveData.FeedReaderDbHelper;

public class CharacterSheetActivity extends Activity implements View.OnClickListener {

    /* Debugging */
    private static final String TAG = CharacterSheetActivity.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    Activity mActivity;


    /* UI stuff */
    // Display Character
    private View mModifyCharacterView, mAddExperienceView;
    private TextView mTextViewAbility[] = new TextView[6];
    private TextView mTextViewAbilityMod[] = new TextView[6];
    private TextView mTextViewSaveMod[] = new TextView[6];
    private CheckBox mSkillProfs[] = new CheckBox[18];
    private CheckBox mCheckBoxSaveProfs[] = new CheckBox[6];
    private TextView mSkillMods[] = new TextView[18];
    private TextView mProf, mName, mClass, mRace, mAlign;
    private ProgressBar mProgressExperience;
    private TextView mCurrentLevel;
    private Button mButtonModifyCharacter,mButtonAddExperience, mButtonDeleteCharacter;
    // Modify Character
    private AlertDialog mModifyCharacterDialog, mAddExperienceDialog;
    private EditText mEditStr, mEditDex, mEditCon, mEditInt, mEditWis, mEditChr;
    private CheckBox mEditSkillProfs[] = new CheckBox[18];
    private CheckBox mEditSavingThrows[] = new CheckBox[6];
    private EditText mEditProf, mEditName, mEditClass, mEditRace, mEditAlign;
    private EditText mAddExperience;

    boolean updateUIRegistered = false;

    /**
     * Broadcast receiver listening to Personal Status updates from VPTabBarActivity
     */
    private BroadcastReceiver updateUIReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if(intent.getAction().equals(CharacterManager.UPDATE_UI))
            {
                logger.debug("Update UI from broadcast");
                updateUI();
                Toast.makeText(mActivity, "Data Loaded", Toast.LENGTH_SHORT).show();
            }
            else if(intent.getAction().equals(CharacterManager.CLEAR_CHAR))
            {
                logger.debug("Update UI from broadcast");
                updateUI();
                Toast.makeText(mActivity, "Character Deleted", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_sheet_layout);
        mActivity = this;
        // Buttons
        mButtonModifyCharacter = (Button) findViewById(R.id.button_modify_character);
        mButtonModifyCharacter.setOnClickListener(this);
        mButtonAddExperience = (Button) findViewById(R.id.button_add_experience);
        mButtonAddExperience.setOnClickListener(this);
        mButtonDeleteCharacter = (Button) findViewById(R.id.button_delete_char);
        mButtonDeleteCharacter.setOnClickListener(this);
        // Identity
        mName = (TextView) findViewById(R.id.char_name);
        mClass = (TextView) findViewById(R.id.char_class);
        mRace = (TextView) findViewById(R.id.char_race);
        mAlign = (TextView) findViewById(R.id.char_alignment);
        // Abilities
        LinearLayout layout_stats = (LinearLayout) findViewById(R.id.layout_stats);
        mTextViewAbility[0] = (TextView) layout_stats.findViewById(R.id.include_ability_strength).findViewById(R.id.stat_ability_num);
        mTextViewAbilityMod[0] = (TextView) layout_stats.findViewById(R.id.include_ability_strength).findViewById(R.id.mod_ability_num);
        mTextViewAbility[1] = (TextView) layout_stats.findViewById(R.id.include_ability_dexterity).findViewById(R.id.stat_ability_num);
        mTextViewAbilityMod[1] = (TextView) layout_stats.findViewById(R.id.include_ability_dexterity).findViewById(R.id.mod_ability_num);
        mTextViewAbility[2] = (TextView) layout_stats.findViewById(R.id.include_ability_constitution).findViewById(R.id.stat_ability_num);
        mTextViewAbilityMod[2] = (TextView) layout_stats.findViewById(R.id.include_ability_constitution).findViewById(R.id.mod_ability_num);
        mTextViewAbility[3] = (TextView) layout_stats.findViewById(R.id.include_ability_intelligence).findViewById(R.id.stat_ability_num);
        mTextViewAbilityMod[3] = (TextView) layout_stats.findViewById(R.id.include_ability_intelligence).findViewById(R.id.mod_ability_num);
        mTextViewAbility[4] = (TextView) layout_stats.findViewById(R.id.include_ability_wisdom).findViewById(R.id.stat_ability_num);
        mTextViewAbilityMod[4] = (TextView) layout_stats.findViewById(R.id.include_ability_wisdom).findViewById(R.id.mod_ability_num);
        mTextViewAbility[5] = (TextView) layout_stats.findViewById(R.id.include_ability_charisma).findViewById(R.id.stat_ability_num);
        mTextViewAbilityMod[5] = (TextView) layout_stats.findViewById(R.id.include_ability_charisma).findViewById(R.id.mod_ability_num);
        // Skills
        mSkillProfs[0] = (CheckBox) findViewById(R.id.char_acrobatics);
        mSkillProfs[1] = (CheckBox) findViewById(R.id.char_animal_handling);
        mSkillProfs[2] = (CheckBox) findViewById(R.id.char_arcana);
        mSkillProfs[3] = (CheckBox) findViewById(R.id.char_athletics);
        mSkillProfs[4] = (CheckBox) findViewById(R.id.char_deception);
        mSkillProfs[5] = (CheckBox) findViewById(R.id.char_history);
        mSkillProfs[6] = (CheckBox) findViewById(R.id.char_insight);
        mSkillProfs[7] = (CheckBox) findViewById(R.id.char_intimidation);
        mSkillProfs[8] = (CheckBox) findViewById(R.id.char_investigation);
        mSkillProfs[9] = (CheckBox) findViewById(R.id.char_medicine);
        mSkillProfs[10] = (CheckBox) findViewById(R.id.char_nature);
        mSkillProfs[11] = (CheckBox) findViewById(R.id.char_perception);
        mSkillProfs[12] = (CheckBox) findViewById(R.id.char_performance);
        mSkillProfs[13] = (CheckBox) findViewById(R.id.char_persuasion);
        mSkillProfs[14] = (CheckBox) findViewById(R.id.char_religion);
        mSkillProfs[15] = (CheckBox) findViewById(R.id.char_sleight_of_hand);
        mSkillProfs[16] = (CheckBox) findViewById(R.id.char_stealth);
        mSkillProfs[17] = (CheckBox) findViewById(R.id.char_survival);
        // Mods
        mSkillMods[0] = (TextView) findViewById(R.id.mod_acrobatics);
        mSkillMods[1] = (TextView) findViewById(R.id.mod_animal_handling);
        mSkillMods[2] = (TextView) findViewById(R.id.mod_arcana);
        mSkillMods[3] = (TextView) findViewById(R.id.mod_athletics);
        mSkillMods[4] = (TextView) findViewById(R.id.mod_deception);
        mSkillMods[5] = (TextView) findViewById(R.id.mod_history);
        mSkillMods[6] = (TextView) findViewById(R.id.mod_insight);
        mSkillMods[7] = (TextView) findViewById(R.id.mod_intimidation);
        mSkillMods[8] = (TextView) findViewById(R.id.mod_investigation);
        mSkillMods[9] = (TextView) findViewById(R.id.mod_medicine);
        mSkillMods[10] = (TextView) findViewById(R.id.mod_nature);
        mSkillMods[11] = (TextView) findViewById(R.id.mod_perception);
        mSkillMods[12] = (TextView) findViewById(R.id.mod_performance);
        mSkillMods[13] = (TextView) findViewById(R.id.mod_persuasion);
        mSkillMods[14] = (TextView) findViewById(R.id.mod_religion);
        mSkillMods[15] = (TextView) findViewById(R.id.mod_sleight_of_hand);
        mSkillMods[16] = (TextView) findViewById(R.id.mod_stealth);
        mSkillMods[17] = (TextView) findViewById(R.id.mod_survival);
        // Proficiency
        mProf = (TextView) findViewById(R.id.char_proficiency);
        // Saving Throws
        mTextViewSaveMod[0] = (TextView) findViewById(R.id.mod_saving_throws_str);
        mTextViewSaveMod[1] = (TextView) findViewById(R.id.mod_saving_throws_dex);
        mTextViewSaveMod[2] = (TextView) findViewById(R.id.mod_saving_throws_con);
        mTextViewSaveMod[3] = (TextView) findViewById(R.id.mod_saving_throws_int);
        mTextViewSaveMod[4] = (TextView) findViewById(R.id.mod_saving_throws_wis);
        mTextViewSaveMod[5] = (TextView) findViewById(R.id.mod_saving_throws_chr);
        mCheckBoxSaveProfs[0] = (CheckBox) findViewById(R.id.char_saving_throw_str);
        mCheckBoxSaveProfs[1] = (CheckBox) findViewById(R.id.char_saving_throw_dex);
        mCheckBoxSaveProfs[2] = (CheckBox) findViewById(R.id.char_saving_throw_con);
        mCheckBoxSaveProfs[3] = (CheckBox) findViewById(R.id.char_saving_throw_int);
        mCheckBoxSaveProfs[4] = (CheckBox) findViewById(R.id.char_saving_throw_wis);
        mCheckBoxSaveProfs[5] = (CheckBox) findViewById(R.id.char_saving_throw_chr);
        // Experience
        mProgressExperience = (ProgressBar) findViewById(R.id.prog_current_exp);
        mProgressExperience.setMax(CharacterManager.LEVEL_CAPS[0]);
        //mCurrentLevel = (TextView) findViewById(R.id.text_current_level);

        CharacterManager characterManager = CharacterManager.getInstance();
        if (characterManager.getCharacter() == null) {
            characterManager.setCharacter(new BaseCharacter());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the update intent listener
        if(!updateUIRegistered)
        {
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.UPDATE_UI));
            registerReceiver(updateUIReceiver, new IntentFilter(CharacterManager.CLEAR_CHAR));
            updateUIRegistered = true;
        }
        updateUI();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (updateUIRegistered) {
            unregisterReceiver(updateUIReceiver);
            updateUIRegistered = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_modify_character:
                modifyCharacterDialog();
                break;
            case R.id.button_delete_char:
                deleteCharDialog();
                break;
            case R.id.button_add_experience:
                addExperienceDialog();
                break;
        }
    }

    private void modifyCharacterDialog() {
        logger.debug("Showing Character Modify Dialog");
        LayoutInflater inflater = getLayoutInflater();
        mModifyCharacterView = inflater.inflate(R.layout.modify_character_layout, null);
        initModifyCharacterView();
        mModifyCharacterDialog = new AlertDialog.Builder(this)
                .setTitle("Modify Character")
                .setView(mModifyCharacterView)
                .setPositiveButton(android.R.string.ok, characterSave)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mModifyCharacterDialog.show();
    }

    private void deleteCharDialog() {
        logger.debug("Showing Delete Character Dialog");
        AlertDialog deleteCharacterDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Character?!")
                .setPositiveButton(android.R.string.ok, deleteCharacter)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        deleteCharacterDialog.show();
    }

    private void addExperienceDialog(){
        logger.debug("Showing Character Modify Dialog");
        LayoutInflater inflater = getLayoutInflater();
        mAddExperienceView = inflater.inflate(R.layout.add_experience_layout, null);
        initAddExperienceView();
        mAddExperienceDialog = new AlertDialog.Builder(this)
                .setTitle("Add Experience")
                .setView(mAddExperienceView)
                .setPositiveButton(android.R.string.ok, addExperience)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mAddExperienceDialog.show();
    }

    private void updateUI() {
        CharacterManager characterManager = CharacterManager.getInstance();
        // Identity
        String name = characterManager.getName();
        String class_ = characterManager.getClass_();
        String race = characterManager.getRace();
        String align = characterManager.getAlign();
        mName.setText(name);
        mClass.setText(class_);
        mRace.setText(race);
        mAlign.setText(align);
        // Stats
        int temp[] = characterManager.getAbilities();
        for (int i = 0; i < temp.length; i++) {
            mTextViewAbility[i].setText("" + temp[i]);
        }
        // Mods
        temp = characterManager.getAbilityModifiers();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] >= 0) {
                mTextViewAbilityMod[i].setText("+" + temp[i]);
            } else {
                mTextViewAbilityMod[i].setText(" " + temp[i]);
            }
        }
        // Skills
        temp = characterManager.getSkillProfs();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != 0) mSkillProfs[i].setChecked(true);
        }
        // Mods
        temp = characterManager.getSkillModifiers();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] >= 0) {
                mSkillMods[i].setText("+" + temp[i]);

            } else {
                mSkillMods[i].setText(" " + temp[i]);
            }
        }
        // Proficiency
        int getProf = characterManager.getProficiency();
        if (getProf >= 0) {
            mProf.setText("+" + getProf);
        } else {
            mProf.setText(" " + getProf);
        }
        // Saving Throws
        temp = characterManager.getSaveProfs();
        for(int i = 0; i < temp.length; i++){
            if (temp[i] != 0) mCheckBoxSaveProfs[i].setChecked(true);
        }
        temp = characterManager.getSaveMods();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] >= 0) {
                mTextViewSaveMod[i].setText("+" + temp[i]);
            } else {
                mTextViewSaveMod[i].setText(" " + temp[i]);
            }
        }
        // Experience
        updateExperienceProgressBar(characterManager.getExperienceProgress());
    }

    private void updateExperienceProgressBar(int exp){
        CharacterManager characterManager = CharacterManager.getInstance();
        int diff = exp - mProgressExperience.getMax();
        if(diff >= 0){
            mProgressExperience.setMax(characterManager.getNextLevelCap());
            mProgressExperience.setProgress(diff);
        }
        else{
            mProgressExperience.setProgress(exp);
        }
        mCurrentLevel.setText("Level " + characterManager.getLevel());
    }

    private void initAddExperienceView() {
        // Identity
        mAddExperience = (EditText) mAddExperienceView.findViewById(R.id.edit_text_add_experience);
    }

    private void initModifyCharacterView() {
        // Identity
        mEditName = (EditText) mModifyCharacterView.findViewById(R.id.edit_name);
        mEditClass = (EditText) mModifyCharacterView.findViewById(R.id.edit_class);
        mEditRace = (EditText) mModifyCharacterView.findViewById(R.id.edit_race);
        mEditAlign = (EditText) mModifyCharacterView.findViewById(R.id.edit_alignment);
        // Abilities
        mEditStr = (EditText) mModifyCharacterView.findViewById(R.id.edit_strength);
        mEditDex = (EditText) mModifyCharacterView.findViewById(R.id.edit_dexterity);
        mEditCon = (EditText) mModifyCharacterView.findViewById(R.id.edit_constitution);
        mEditInt = (EditText) mModifyCharacterView.findViewById(R.id.edit_intelligence);
        mEditWis = (EditText) mModifyCharacterView.findViewById(R.id.edit_wisdom);
        mEditChr = (EditText) mModifyCharacterView.findViewById(R.id.edit_charisma);
        // Skills
        mEditSkillProfs[0] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_acrobatics);
        mEditSkillProfs[1] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_animal_handling);
        mEditSkillProfs[2] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_arcana);
        mEditSkillProfs[3] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_athletics);
        mEditSkillProfs[4] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_deception);
        mEditSkillProfs[5] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_history);
        mEditSkillProfs[6] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_insight);
        mEditSkillProfs[7] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_intimidation);
        mEditSkillProfs[8] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_investigation);
        mEditSkillProfs[9] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_medicine);
        mEditSkillProfs[10] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_nature);
        mEditSkillProfs[11] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_perception);
        mEditSkillProfs[12] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_performance);
        mEditSkillProfs[13] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_persuasion);
        mEditSkillProfs[14] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_religion);
        mEditSkillProfs[15] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_sleight_of_hand);
        mEditSkillProfs[16] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_stealth);
        mEditSkillProfs[17] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_survival);
        // Prof
        mEditProf = (EditText) mModifyCharacterView.findViewById(R.id.edit_proficiency);
        // Saving Throws
        mEditSavingThrows[0] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_save_str);
        mEditSavingThrows[1] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_save_dex);
        mEditSavingThrows[2] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_save_con);
        mEditSavingThrows[3] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_save_int);
        mEditSavingThrows[4] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_save_wis);
        mEditSavingThrows[5] = (CheckBox) mModifyCharacterView.findViewById(R.id.check_save_chr);
        fillCurrentModifierStats();
    }

    private void fillCurrentModifierStats() {
        CharacterManager characterManager = CharacterManager.getInstance();
        // Identity
        String name = characterManager.getName();
        String class_ = characterManager.getClass_();
        String race = characterManager.getRace();
        String align = characterManager.getAlign();
        mEditName.setText(((name.equals("Name") || name.equals(" ")) ? "" : name));
        mEditClass.setText((class_.equals(" ") ? "" : class_));
        mEditRace.setText((race.equals(" ") ? "" : race));
        mEditAlign.setText((align.equals(" ") ? "" : align));
        // Abilities
        int temp[] = characterManager.getAbilities();
        mEditStr.setText("" + (temp[0] > 0 ? temp[0]: ""));
        mEditDex.setText("" + (temp[1] > 0 ? temp[1]: ""));
        mEditCon.setText("" + (temp[2] > 0 ? temp[2]: ""));
        mEditInt.setText("" + (temp[3] > 0 ? temp[3]: ""));
        mEditWis.setText("" + (temp[4] > 0 ? temp[4]: ""));
        mEditChr.setText("" + (temp[5] > 0 ? temp[5]: ""));
        // Skills
        temp = characterManager.getSkillProfs();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != 0) mEditSkillProfs[i].setChecked(true);
        }
        // Prof
        int getProf = characterManager.getProficiency();
        mEditProf.setText("" + (getProf == 0 ? "" : getProf));
        // Saving Throws
        temp = characterManager.getSaveProfs();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != 0) mEditSavingThrows[i].setChecked(true);
        }
    }

    /* Save Character */
    DialogInterface.OnClickListener characterSave = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CharacterManager characterManager = CharacterManager.getInstance();
            // Extract Identity
            String identity[] = new String[4];
            identity[0] = mEditName.getText().toString();
            identity[1] = mEditRace.getText().toString();
            identity[2] = mEditClass.getText().toString();
            identity[3] = mEditAlign.getText().toString();
            // Extract Abilities
            int abilities[] = new int[6];
            try {
                abilities[0] = Integer.parseInt(mEditStr.getText().toString());
            } catch (Exception e) {
                abilities[0] = 0;
            }
            try {
                abilities[1] = Integer.parseInt(mEditDex.getText().toString());
            } catch (Exception e) {
                abilities[1] = 0;
            }
            try {
                abilities[2] = Integer.parseInt(mEditCon.getText().toString());
            } catch (Exception e) {
                abilities[2] = 0;
            }
            try {
                abilities[3] = Integer.parseInt(mEditInt.getText().toString());
            } catch (Exception e) {
                abilities[3] = 0;
            }
            try {
                abilities[4] = Integer.parseInt(mEditWis.getText().toString());
            } catch (Exception e) {
                abilities[4] = 0;
            }
            try {
                abilities[5] = Integer.parseInt(mEditChr.getText().toString());
            } catch (Exception e) {
                abilities[5] = 0;
            }
            // Extract Skills
            int skillProfs[] = new int[18];
            for (int i = 0; i < mEditSkillProfs.length; i++) {
                if (mEditSkillProfs[i].isChecked()) {
                    skillProfs[i] = 1;
                } else {
                    skillProfs[i] = 0;
                }
                // TODO: CHECK AGAINST SPECIAL TALENTS THAT INCREASE SKILL PROFS
            }
            int prof;
            try {
                prof = Integer.parseInt(mEditProf.getText().toString());
            } catch (Exception e) {
                prof = 0;
            }
            // Extract Saving Throws
            int saveProfs[] = new int[6];
            for (int i = 0; i < mEditSavingThrows.length; i++) {
                if (mEditSavingThrows[i].isChecked()) {
                    saveProfs[i] = 1;
                } else {
                    saveProfs[i] = 0;
                }
            }
            // Update Character data
            characterManager.setIdentity(identity);
            characterManager.setAbilities(abilities);
            characterManager.setSkillsProfs(skillProfs);
            characterManager.setProficiency(prof);
            characterManager.setSavingThrows(saveProfs);
            updateUI();
        }
    };

    /* Delete Character */
    DialogInterface.OnClickListener deleteCharacter = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AsyncTask<Void, Void, Integer> task = (new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... voids) {
                    FeedReaderDbHelper feedReaderDbHelper = FeedReaderDbHelper.getInstance();
                    feedReaderDbHelper.deleteCharacterDataBase();
                    return 1;
                }

                @Override
                protected void onPostExecute(Integer count) {
                    CharacterManager characterManager = CharacterManager.getInstance();
                    characterManager.setCharacter(new BaseCharacter());
                    Intent intent = new Intent();
                    intent.setAction(CharacterManager.CLEAR_CHAR);
                    mActivity.sendBroadcast(intent);
                    return;
                }
            });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    /* Add Experience */
    DialogInterface.OnClickListener addExperience = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CharacterManager characterManager = CharacterManager.getInstance();
            int curr_exp_progress = characterManager.getExperienceProgress();
            int add_exp;
            try{
                add_exp = Integer.parseInt(mAddExperience.getText().toString());
            }catch(Exception e){
                add_exp = 0;
            }
            int updated_exp_progress = curr_exp_progress + add_exp;
            updateExperienceProgressBar(updated_exp_progress);
            characterManager.addExperience(add_exp);
        }
    };
}
