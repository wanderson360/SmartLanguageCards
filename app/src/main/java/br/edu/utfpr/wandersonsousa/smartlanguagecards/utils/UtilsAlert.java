package br.edu.utfpr.wandersonsousa.smartlanguagecards.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import br.edu.utfpr.wandersonsousa.smartlanguagecards.R;

public final class UtilsAlert {

    private UtilsAlert() {}

    public static void showAlert(Context context, int idMessage) {
        showAlert(context, context.getString(idMessage), null);
    }

    public static void showAlert(Context context, int idMessage,
                                 DialogInterface.OnClickListener listener) {
        showAlert(context, context.getString(idMessage), listener);
    }

    public static void showAlert(Context context, String message,
                                 DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.alerta))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(message)
                .setNeutralButton(context.getString(R.string.ok), listener)
                .show();
    }

    public static void confirmAction(Context context, int idMessage,
                                     DialogInterface.OnClickListener listenerYes,
                                     DialogInterface.OnClickListener listenerNo) {
        confirmAction(context, context.getString(idMessage), listenerYes, listenerNo);
    }
    public static void confirmAction(Context context, String message,
                                     DialogInterface.OnClickListener listenerYes,
                                     DialogInterface.OnClickListener listenerNo) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirmation)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(message)
                .setPositiveButton(R.string.yes, listenerYes)
                .setNegativeButton(R.string.no, listenerNo)
                .show();
    }
}