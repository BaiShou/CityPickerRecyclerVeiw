package com.baisoo.citypicker.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baisoo.citypicker.R;

/**
 * Created by baisoo on 16/9/24.
 */

public class CityHolder extends RecyclerView.ViewHolder {

    public  TextView mCityName;

    public CityHolder(View itemView) {
        super(itemView);
        mCityName = (TextView) itemView.findViewById(R.id.city_name);
    }
}
