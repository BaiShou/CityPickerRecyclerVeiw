package com.baisoo.citypicker.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baisoo.citypicker.R;

/**
 * Created by baisoo on 16/9/24.
 */

public class CharacterHolder extends RecyclerView.ViewHolder {

    public  TextView mCharater;

    public CharacterHolder(View itemView) {
        super(itemView);
        mCharater = (TextView) itemView.findViewById(R.id.character);
    }
}
