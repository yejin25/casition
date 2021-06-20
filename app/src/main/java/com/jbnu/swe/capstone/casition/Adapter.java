package com.jbnu.swe.capstone.casition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Adapter extends BaseAdapter {
    private Context mContext;
    public Adapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return CalcListActivity.dataArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);                                    // 무조건 viewGroup이 아니라 Context로 진행하는게 속 편함
            view = inflater.inflate(R.layout.list_item, null);

            ViewHolder holder = new ViewHolder();
            holder.area = (TextView) view.findViewById(R.id.text_area);
            holder.area_data = (TextView) view.findViewById(R.id.text_area_data);
            holder.time = (TextView) view.findViewById(R.id.text_time);
            holder.time_data = (TextView) view.findViewById(R.id.text_time_data);
            holder.out = (TextView) view.findViewById(R.id.text_out);
            holder.out_data = (TextView) view.findViewById(R.id.text_out_data);

            view.setTag(holder);                                                                    // tagging 넣어줘야 작동함
        }
        ViewHolder holder = (ViewHolder)view.getTag();
        holder.area_data.setText(CalcListActivity.dataArrayList.get(i).area);
        holder.time_data.setText(CalcListActivity.dataArrayList.get(i).time);
        holder.out_data.setText(CalcListActivity.dataArrayList.get(i).out_time);

        return view;
    }
    private static class ViewHolder{
        TextView text, area, area_data, time, time_data, out, out_data;
    }
}
