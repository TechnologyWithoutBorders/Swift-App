package ngo.teog.swift;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ngo.teog.swift.R;

/**
 * Created by Julian on 24.02.2018.
 */

public class NewsDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String news = this.getArguments().getString("news");
        final int notificationID = this.getArguments().getInt("notification", -1);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_news, null);
        TextView tv = view.findViewById(R.id.newsView);
        tv.setText(news);

        builder.setTitle("News");
        builder.setView(view);

        // Use the Builder class for convenient dialog construction
        builder.setPositiveButton("CODE_OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(notificationID != -1) {
                        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                        mNotificationManager.cancel(notificationID);
                    }
                }
            });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //ignore
                }
            });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
