package com.example.patel.pdftogo;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import static android.support.design.widget.Snackbar.make;

public class NewPDFDialogFragment extends DialogFragment{

    String mPdfFileName;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

      final View dialogCustom =inflater.inflate(R.layout.dialog_custom, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogCustom)
                // Add action buttons
                .setPositiveButton(R.string.createaNewPDF, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final EditText pdfName = (EditText)dialogCustom.findViewById(R.id.pdfFileName);
                        mPdfFileName= pdfName.getText().toString();

                            if (mPdfFileName != null)
                            {
                                Intent toCanvas = new Intent(getActivity(), CanvasActivity.class);
                                toCanvas.putExtra("pdfFileName",mPdfFileName);
                                startActivity(toCanvas);
                            }
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewPDFDialogFragment.this.getDialog().cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.floatingBackground));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.floatingBackground));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
        return alert;

    }



}
