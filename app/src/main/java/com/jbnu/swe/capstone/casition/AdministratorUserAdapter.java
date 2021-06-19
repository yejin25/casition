package com.jbnu.swe.capstone.casition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdministratorUserAdapter extends BaseAdapter {
    private Context mContext;

    public AdministratorUserAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return AdministratorActivity.userListStaticArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return AdministratorActivity.userListStaticArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_user_license, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.userName = (TextView)view.findViewById(R.id.user_license_check_textview);

            view.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.userName.setText(AdministratorActivity.userListStaticArrayList.get(position));

        return view;
    }

    private static class ViewHolder {
        TextView userName;
    }
}

