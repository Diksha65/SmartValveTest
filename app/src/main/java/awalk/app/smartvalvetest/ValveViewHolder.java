package awalk.app.smartvalvetest;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Vamsi on 03-03-2017.
 */

public class ValveViewHolder extends RecyclerView.ViewHolder {

    public TextView VName,VStatus;
    public View itemView;

    ValveViewHolder(View view) {
        super(view);
        itemView = view;
        VName = (TextView) view.findViewById(R.id.itemName);
        VStatus = (TextView) view.findViewById(R.id.itemStatus);
    }

    public void setName(String name) {
        VName.setText(name);
    }

    public void setStatus(String status) {
        VStatus.setText(status);
    }
}
