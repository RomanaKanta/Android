package com.smartmux.weatherapp.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmux.weatherapp.R;
import com.smartmux.weatherapp.model.DayForecast;

/** An array adapter that knows how to render views when given CustomData classes */
public class CustomArrayAdapter extends ArrayAdapter<DayForecast> {
    private LayoutInflater mInflater;
    @SuppressWarnings("unused")
	private List<DayForecast> forecastList;
    @SuppressLint("SimpleDateFormat") 
    private final static SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
    private String [] colors={
    		"#31AFF5",
    		"#27A5EB",
    		"#1D9BE1",
    		"#1391D7",
    		"#0987CD",
    		"#007DC3",
    		"#0073B9"
    		
    		
    		
    };
    public CustomArrayAdapter(Context context,  List<DayForecast> forecastList) {
        super(context, R.layout.custom_data_view, forecastList);
        this.forecastList=forecastList;
       
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.custom_data_view, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.forecastDay = (TextView) convertView.findViewById(R.id.forecastday);
            holder.conditionIcon=(ImageView)convertView.findViewById(R.id.condIcon);
            holder.forecastTemp=(TextView) convertView.findViewById(R.id.forecastTemp);
            
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        // Populate the text
        
        
        Date d = new Date();
		Calendar gc =  new GregorianCalendar();
		gc.setTime(d);
		gc.add(GregorianCalendar.DAY_OF_MONTH, position);
        //sdf.format(gc.getTime())
        holder.forecastDay.setText(sdf.format(gc.getTime()));
        holder.conditionIcon.setImageResource(R.drawable.cloud);
        //getItem(position).weather.currentCondition.getCondition()+"----"+getItem(position).weather.currentCondition.getDescr()
        holder.forecastTemp.setText((int) (getItem(position).forecastTemp.min - 275.15) + "-" + (int) (getItem(position).forecastTemp.max - 275.15)+" °C");
       
               if(getItem(position).weather.currentCondition.getCondition().equals("Rain")){
        	holder.conditionIcon.setImageResource(R.drawable.rain);
        }else if(getItem(position).weather.currentCondition.getCondition().equals("Clear")){
        	holder.conditionIcon.setImageResource(R.drawable.clear);
        }else if(getItem(position).weather.currentCondition.getCondition().equals("Clouds")){
        	holder.conditionIcon.setImageResource(R.drawable.cloud);
        }else if(getItem(position).weather.currentCondition.getCondition().equals("Snow")){
        	 holder.conditionIcon.setImageResource(R.drawable.snow);
        }
//String.valueOf(getItem(position).forecastTemp.max)
        // Set the color
        @SuppressWarnings("unused")
		String c="#bdbdbd";
        
        convertView.setBackgroundColor(Color.parseColor(colors[position]));

        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView forecastDay;
        public TextView forecastTemp;
        public ImageView conditionIcon;
       
    }
}
