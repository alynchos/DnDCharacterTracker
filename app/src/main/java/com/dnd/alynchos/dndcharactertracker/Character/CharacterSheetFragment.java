package com.dnd.alynchos.dndcharactertracker.Character;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CharacterSheetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CharacterSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterSheetFragment extends Fragment implements View.OnClickListener {

    /* Debugging */
    private static final String TAG = CharacterSheetFragment.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    private View mModifyCharacterView;
    private TextView mTextViewAbility[] = new TextView[6];
    private TextView mTextViewAbilityMod[] = new TextView[6];
    private TextView mTextViewSaveMod[] = new TextView[6];
    private TextView mProf;
    private CheckBox mSkillProfs[] = new CheckBox[18];
    private CheckBox mCheckBoxSaveProfs[] = new CheckBox[6];
    private TextView mSkillMods[] = new TextView[18];
    private Button mButtonModifyCharacter;
    private EditText mEditStr, mEditDex, mEditCon, mEditInt, mEditWis, mEditChr;
    private CheckBox mEditSkillProfs[] = new CheckBox[18];
    private CheckBox mEditSavingThrows[] = new CheckBox[6];
    private EditText mEditProf;
    private boolean RefreshUI = true;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REFRESH_UI = "refreshUI";

    private OnFragmentInteractionListener mListener;

    public CharacterSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param refreshUI Whether the instance should refresh the ui.
     * @return A new instance of fragment CharacterSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CharacterSheetFragment newInstance(boolean refreshUI) {
        CharacterSheetFragment fragment = new CharacterSheetFragment();
        Bundle args = new Bundle();
        args.putBoolean(REFRESH_UI, refreshUI);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            RefreshUI = getArguments().getBoolean(REFRESH_UI);
        }
        // TODO: CHECK IF THIS WORKS
        CharacterManager characterManager = CharacterManager.getInstance();
        if (characterManager.getCharacter() == null) {
            characterManager.setCharacter(new BaseCharacter());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character_sheet_layout, container, false);
        // Buttons
        mButtonModifyCharacter = (Button) view.findViewById(R.id.button_modify_character);
        mButtonModifyCharacter.setOnClickListener(this);
        // Abilities
        LinearLayout layout_stats = (LinearLayout) view.findViewById(R.id.layout_stats);
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
        mSkillProfs[0] = (CheckBox) view.findViewById(R.id.char_acrobatics);
        mSkillProfs[1] = (CheckBox) view.findViewById(R.id.char_animal_handling);
        mSkillProfs[2] = (CheckBox) view.findViewById(R.id.char_arcana);
        mSkillProfs[3] = (CheckBox) view.findViewById(R.id.char_athletics);
        mSkillProfs[4] = (CheckBox) view.findViewById(R.id.char_deception);
        mSkillProfs[5] = (CheckBox) view.findViewById(R.id.char_history);
        mSkillProfs[6] = (CheckBox) view.findViewById(R.id.char_insight);
        mSkillProfs[7] = (CheckBox) view.findViewById(R.id.char_intimidation);
        mSkillProfs[8] = (CheckBox) view.findViewById(R.id.char_investigation);
        mSkillProfs[9] = (CheckBox) view.findViewById(R.id.char_medicine);
        mSkillProfs[10] = (CheckBox) view.findViewById(R.id.char_nature);
        mSkillProfs[11] = (CheckBox) view.findViewById(R.id.char_perception);
        mSkillProfs[12] = (CheckBox) view.findViewById(R.id.char_performance);
        mSkillProfs[13] = (CheckBox) view.findViewById(R.id.char_persuasion);
        mSkillProfs[14] = (CheckBox) view.findViewById(R.id.char_religion);
        mSkillProfs[15] = (CheckBox) view.findViewById(R.id.char_sleight_of_hand);
        mSkillProfs[16] = (CheckBox) view.findViewById(R.id.char_stealth);
        mSkillProfs[17] = (CheckBox) view.findViewById(R.id.char_survival);
        // Mods
        mSkillMods[0] = (TextView) view.findViewById(R.id.mod_acrobatics);
        mSkillMods[1] = (TextView) view.findViewById(R.id.mod_animal_handling);
        mSkillMods[2] = (TextView) view.findViewById(R.id.mod_arcana);
        mSkillMods[3] = (TextView) view.findViewById(R.id.mod_athletics);
        mSkillMods[4] = (TextView) view.findViewById(R.id.mod_deception);
        mSkillMods[5] = (TextView) view.findViewById(R.id.mod_history);
        mSkillMods[6] = (TextView) view.findViewById(R.id.mod_insight);
        mSkillMods[7] = (TextView) view.findViewById(R.id.mod_intimidation);
        mSkillMods[8] = (TextView) view.findViewById(R.id.mod_investigation);
        mSkillMods[9] = (TextView) view.findViewById(R.id.mod_medicine);
        mSkillMods[10] = (TextView) view.findViewById(R.id.mod_nature);
        mSkillMods[11] = (TextView) view.findViewById(R.id.mod_perception);
        mSkillMods[12] = (TextView) view.findViewById(R.id.mod_performance);
        mSkillMods[13] = (TextView) view.findViewById(R.id.mod_persuasion);
        mSkillMods[14] = (TextView) view.findViewById(R.id.mod_religion);
        mSkillMods[15] = (TextView) view.findViewById(R.id.mod_sleight_of_hand);
        mSkillMods[16] = (TextView) view.findViewById(R.id.mod_stealth);
        mSkillMods[17] = (TextView) view.findViewById(R.id.mod_survival);
        // Proficiency
        mProf = (TextView) view.findViewById(R.id.char_proficiency);
        // Saving Throws
        mTextViewSaveMod[0] = (TextView) view.findViewById(R.id.mod_saving_throws_str);
        mTextViewSaveMod[1] = (TextView) view.findViewById(R.id.mod_saving_throws_dex);
        mTextViewSaveMod[2] = (TextView) view.findViewById(R.id.mod_saving_throws_con);
        mTextViewSaveMod[3] = (TextView) view.findViewById(R.id.mod_saving_throws_int);
        mTextViewSaveMod[4] = (TextView) view.findViewById(R.id.mod_saving_throws_wis);
        mTextViewSaveMod[5] = (TextView) view.findViewById(R.id.mod_saving_throws_chr);
        mCheckBoxSaveProfs[0] = (CheckBox) view.findViewById(R.id.char_saving_throw_str);
        mCheckBoxSaveProfs[1] = (CheckBox) view.findViewById(R.id.char_saving_throw_dex);
        mCheckBoxSaveProfs[2] = (CheckBox) view.findViewById(R.id.char_saving_throw_con);
        mCheckBoxSaveProfs[3] = (CheckBox) view.findViewById(R.id.char_saving_throw_int);
        mCheckBoxSaveProfs[4] = (CheckBox) view.findViewById(R.id.char_saving_throw_wis);
        mCheckBoxSaveProfs[5] = (CheckBox) view.findViewById(R.id.char_saving_throw_chr);

        CharacterManager characterManager = CharacterManager.getInstance();
        if (characterManager.getCharacter() == null) {
            characterManager.setCharacter(new BaseCharacter());
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCharacterSheetFragmentInteraction(String string);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_modify_character:
                modifyCharacterDialog();
                break;
        }
    }

    /* Private Helpers */

    private void modifyCharacterDialog() {
        logger.debug("Showing Character Modify Dialog");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mModifyCharacterView = inflater.inflate(R.layout.modify_character_layout, null);
        initModifyCharacterView();
        AlertDialog mModifyCharacterDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Modify Character")
                .setView(mModifyCharacterView)
                .setPositiveButton(android.R.string.ok, characterSave)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        mModifyCharacterDialog.show();
    }

    private void initModifyCharacterView() {
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
        // Abilities
        int temp[] = characterManager.getAbilities();
        mEditStr.setText("" + (temp[0] > 0 ? temp[0] : ""));
        mEditDex.setText("" + (temp[1] > 0 ? temp[1] : ""));
        mEditCon.setText("" + (temp[2] > 0 ? temp[2] : ""));
        mEditInt.setText("" + (temp[3] > 0 ? temp[3] : ""));
        mEditWis.setText("" + (temp[4] > 0 ? temp[4] : ""));
        mEditChr.setText("" + (temp[5] > 0 ? temp[5] : ""));
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

    public void updateUI() {
        // TODO: MAKE THIS REFRESH THINGY WORK
        //if(!RefreshUI) return;
        CharacterManager characterManager = CharacterManager.getInstance();
        // Stats
        int temp[] = characterManager.getAbilities();
        for (int i = 0; i < temp.length; i++) {
            mTextViewAbility[i].setText("" + temp[i]);
        }
        // Mods
        temp = characterManager.getAbilityModifiers();
        for (int i = 0; i < temp.length; i++) {
            mTextViewAbilityMod[i].setTypeface(Typeface.MONOSPACE);
            if (temp[i] >= 10) {
                mTextViewAbilityMod[i].setText("+" + temp[i]);
            } else if (temp[i] >= 0) {
                mTextViewAbilityMod[i].setText("+" + temp[i] + " ");
            } else if (temp[i] <= -10) {
                mTextViewAbilityMod[i].setText(temp[i]);
            } else {
                mTextViewAbilityMod[i].setText("" + temp[i] + " ");
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
            mSkillMods[i].setTypeface(Typeface.MONOSPACE);
            if (temp[i] >= 10) {
                mSkillMods[i].setText("+" + temp[i]);
            } else if (temp[i] >= 0) {
                mSkillMods[i].setText("+" + temp[i] + " ");
            } else if (temp[i] <= -10) {
                mSkillMods[i].setText(temp[i]);
            } else {
                mSkillMods[i].setText("" + temp[i] + " ");
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
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != 0) mCheckBoxSaveProfs[i].setChecked(true);
        }
        temp = characterManager.getSaveMods();
        for (int i = 0; i < temp.length; i++) {
            mTextViewSaveMod[i].setTypeface(Typeface.MONOSPACE);
            if (temp[i] >= 10) {
                mTextViewSaveMod[i].setText("+" + temp[i]);
            } else if (temp[i] >= 0) {
                mTextViewSaveMod[i].setText("+" + temp[i] + " ");
            } else if (temp[i] <= -10) {
                mTextViewSaveMod[i].setText(temp[i]);
            } else {
                mTextViewSaveMod[i].setText("" + temp[i] + " ");
            }
        }
        RefreshUI = false;
    }

    /* End Private Helpers */

    /* Public Methods */


    /* End Public Methods */


    /* Save Character */

    DialogInterface.OnClickListener characterSave = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CharacterManager characterManager = CharacterManager.getInstance();
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
            characterManager.setAbilities(abilities);
            characterManager.setSkillsProfs(skillProfs);
            characterManager.setProficiency(prof);
            characterManager.setSavingThrows(saveProfs);
            RefreshUI = true;
            updateUI();
        }
    };

    /* End Save Character */
}
