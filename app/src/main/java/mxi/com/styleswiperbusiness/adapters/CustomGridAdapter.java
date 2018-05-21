package mxi.com.styleswiperbusiness.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mxi.com.styleswiperbusiness.Activities.StyleDetail;
import mxi.com.styleswiperbusiness.Models.ListStyleInfo;
import mxi.com.styleswiperbusiness.Models.SavedStylesInfo;
import mxi.com.styleswiperbusiness.Network.Constants;
import mxi.com.styleswiperbusiness.R;

/**
 * Created by parth on 31/12/16.
 */
public class CustomGridAdapter extends BaseAdapter {
    Activity context;
    ArrayList<ListStyleInfo> savedStyleInfo;
    private static LayoutInflater inflater = null;

    public CustomGridAdapter(Activity mainActivity, ArrayList<ListStyleInfo> styleData) {
        // TODO Auto-generated constructor stub
        this.savedStyleInfo = styleData;
        context = mainActivity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return savedStyleInfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        ImageView ivRemove;
        ImageView ivImageStyles;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.item_list_styles, null);

        holder.ivImageStyles = (ImageView) rowView.findViewById(R.id.iv_image_in_item_list);
        holder.ivRemove = (ImageView) rowView.findViewById(R.id.iv_remove_image_in_item_list);

        try {
            if(savedStyleInfo.size() > 0){
                Picasso.with(context).load(savedStyleInfo.get(position).getImage()).into(holder.ivImageStyles);
//                holder.ivRemove.setTag(position);
                rowView.setTag(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPos = Integer.parseInt(v.getTag().toString());

                SavedStylesInfo info = SavedStylesInfo.findById(SavedStylesInfo.class, savedStyleInfo.get(selectedPos).getId());
                info.delete();
                info.save();
                savedStyleInfo.remove(selectedPos);
                notifyDataSetChanged();
            }
        });
*/
        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedPos = Integer.parseInt(v.getTag().toString());
                Log.e("Gride Adapter", "Style Cost = " + savedStyleInfo.get(selectedPos).getPrice());
                Intent intent = new Intent(context, StyleDetail.class);
                intent.putExtra(Constants.StyleDetails.StyleID, savedStyleInfo.get(selectedPos).getStyleId());
                intent.putExtra(Constants.StyleDetails.Style, savedStyleInfo.get(selectedPos).getStyle());
                intent.putExtra(Constants.StyleDetails.StyleImage, savedStyleInfo.get(selectedPos).getImage());
                intent.putExtra(Constants.StyleDetails.Color, savedStyleInfo.get(selectedPos).getColor());
                intent.putExtra(Constants.StyleDetails.Length, savedStyleInfo.get(selectedPos).getLength());
                intent.putExtra(Constants.StyleDetails.Cost, savedStyleInfo.get(selectedPos).getPrice());
                intent.putExtra("StyleVisited", savedStyleInfo.get(selectedPos).getVisited());
                intent.putExtra("StyleLiked", savedStyleInfo.get(selectedPos).getLiked());
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}
