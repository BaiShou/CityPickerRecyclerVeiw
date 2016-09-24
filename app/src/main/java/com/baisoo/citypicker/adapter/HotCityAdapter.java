package com.baisoo.citypicker.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baisoo.citypicker.R;
import com.baisoo.citypicker.model.City;
import com.baisoo.citypicker.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baisoo on 16/9/24.
 */
public class HotCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mCitys;
    private CityRecyclerAdapter.OnCityClickListener onCityClickListener;

    public HotCityAdapter(Context mContext, List<String> mCitys,CityRecyclerAdapter.OnCityClickListener listener) {
        this.mContext = mContext;
        this.mCitys = mCitys == null ? new ArrayList<String>() : mCitys;
        this.onCityClickListener = listener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_hot_city_gridview, parent, false);
        return new HotCityItemHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((HotCityItemHolder) holder).mTvHotCityName.setText(mCitys.get(position));

        ((HotCityItemHolder) holder).mTvHotCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回定位城市
                if (onCityClickListener != null){
                    onCityClickListener.onCityClick(((TextView)view).getText().toString());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCitys.size();
    }

    class HotCityItemHolder extends RecyclerView.ViewHolder {


        public TextView mTvHotCityName;

        public HotCityItemHolder(View itemView) {
            super(itemView);
            mTvHotCityName = (TextView) itemView.findViewById(R.id.tv_hot_city_name);

        }
    }


}
