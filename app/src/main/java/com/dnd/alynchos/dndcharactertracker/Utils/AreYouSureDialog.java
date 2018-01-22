package com.dnd.alynchos.dndcharactertracker.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.dnd.alynchos.dndcharactertracker.R;

/**
 * Created by Alex Lynchosky on 10/10/2016.
 * A simple dialog to ask, "Are you sure?"
 */
public class AreYouSureDialog extends AlertDialog.Builder {
    private String message;
    private DialogInterface.OnClickListener pListener;
    private DialogInterface.OnClickListener nListener;

    public AreYouSureDialog(Context context, String message, DialogInterface.OnClickListener pListener, DialogInterface.OnClickListener nListener) {
        super(context);
        this.message = message;
        this.pListener = pListener;
        this.nListener = nListener;
        generate(context);
    }

    private void generate(Context context) {
        this.setTitle(context.getString(R.string.text_are_you_sure));
        this.setMessage(message);
        this.setPositiveButton(android.R.string.yes, pListener);
        this.setNegativeButton(android.R.string.cancel, nListener);
        this.create();
    }

}
