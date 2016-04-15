package com.dnd.alynchos.dndcharactertracker.Character;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dnd.alynchos.dndcharactertracker.R;

public class NotesActivity extends ActionBarActivity {

    private EditText mNotes;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_layout);

        mNotes = (EditText) findViewById(R.id.edit_notes);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CharacterManager characterManager = CharacterManager.getInstance();
        if (characterManager.getNotes() != null) {
            mNotes.setText("" + characterManager.getNotes());
        } else {
            mNotes.setText("");
        }
        imm.showSoftInput(mNotes, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    protected void onPause() {
        super.onResume();
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.setNotes(mNotes.getText().toString());
        imm.hideSoftInputFromWindow(mNotes.getWindowToken(), 0);
    }
}
