package com.example.bm400736.meteo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bm400736.meteo.R;
import com.example.bm400736.meteo.data.DataModel;

import java.util.ArrayList;

/**
 * Created by bm400736 on 31/03/2017.
 */

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;


    // View lookup cache
    private static class ViewHolder {
        TextView txtDate;
        TextView txtDescription;
        TextView txtTemperature;
        TextView txtTemperatureUnit;
        ImageView info;
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;

        switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date);
            viewHolder.txtTemperature = (TextView) convertView.findViewById(R.id.temperature);
            viewHolder.txtTemperatureUnit = (TextView) convertView.findViewById(R.id.temperature_unit);
            viewHolder.txtDescription = (TextView) convertView.findViewById(R.id.description);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        /*Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
*/
        viewHolder.txtDate.setText(dataModel.getDate());
        viewHolder.txtTemperature.setText(dataModel.getTemperature());
        viewHolder.txtTemperatureUnit.setText(dataModel.getTemperatureUnit());
        viewHolder.txtDescription.setText(dataModel.getDescription());
        viewHolder.txtDescription.setText(dataModel.getDescription());
        //viewHolder.info.setImageDrawable();
        //viewHolder.info.setImageResource();


        //marche pas
        Bitmap bMap = BitmapFactory.decodeFile("/drawable/i" + dataModel.getFeature() + ".png");
        viewHolder.info.setImageBitmap(bMap);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

}