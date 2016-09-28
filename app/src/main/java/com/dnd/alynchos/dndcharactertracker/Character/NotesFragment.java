package com.dnd.alynchos.dndcharactertracker.Character;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dnd.alynchos.dndcharactertracker.Debug.Logger;
import com.dnd.alynchos.dndcharactertracker.DnDTabBarActivity;
import com.dnd.alynchos.dndcharactertracker.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {

    /* Debugging */
    private static final String TAG = NotesFragment.class.getSimpleName();
    private static final Logger logger = new Logger(TAG);

    private EditText mNotes;
    private InputMethodManager imm;

    public NotesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_layout, container, false);
        mNotes = (EditText) view.findViewById(R.id.edit_notes);
        mNotes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        CharacterManager characterManager = CharacterManager.getInstance();
        if (characterManager.getNotes() != null) {
            mNotes.setText(String.format("%s", characterManager.getNotes()));
        } else {
            mNotes.setText("");
        }
        imm.showSoftInput(mNotes, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onPause() {
        super.onPause();
        CharacterManager characterManager = CharacterManager.getInstance();
        characterManager.setNotes(mNotes.getText().toString());
        imm.hideSoftInputFromWindow(mNotes.getWindowToken(), 0);
    }

}
