package com.engage.simonnewham.engageapp.adapters;

import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.engage.simonnewham.engageapp.R;
import com.engage.simonnewham.engageapp.activities.MainActivity;
import com.engage.simonnewham.engageapp.models.NewsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to display News List Items in a List View
 * @author simonnewham
 */

public class NewsAdapter extends ArrayAdapter<NewsItem>{

    private Context mContext;
    private List<NewsItem> newsList = new ArrayList<>();

    public NewsAdapter(@NonNull Context context, ArrayList<NewsItem> list) {
        super(context, 0 , list);
        mContext = context;
        newsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.content_list_item,parent,false);

        NewsItem currentItem = newsList.get(position);

        TextView name = listItem.findViewById(R.id.textView_name);
        name.setText(currentItem.getName());

        TextView release = listItem.findViewById(R.id.News_date);
        release.setText("Uploaded: "+currentItem.getDate());

        TextView description = listItem.findViewById(R.id.News_description);
        description.setText("Topic: "+(currentItem.getTopic()));

        ImageView image = listItem.findViewById(R.id.image);
        String type = currentItem.getType().toUpperCase();

        //display content icon depending on type
        if(type.equals("TEXT")){
            image.setImageResource(R.drawable.ic_text);
        }
        else if(type.equals("VIDEO")){
            image.setImageResource(R.drawable.ic_video);

        }
        else if (type.equals("AUDIO")){
            image.setImageResource(R.drawable.ic_music);

        }
        else if (type.equals("IMAGE")){
            image.setImageResource(R.drawable.ic_image);

        }
        return listItem;
    }
}