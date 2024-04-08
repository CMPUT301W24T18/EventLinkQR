package com.example.eventlinkqr;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//import com.example.yourapp.R;
import java.util.List;

/**
 * This class adapts a list of images to display them to the admin
 */
public class ImageAdapter extends ArrayAdapter<ImageModel> {
    private int resourceLayout;

    public ImageAdapter(@NonNull Context context, int resource, @NonNull List<ImageModel> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(this.resourceLayout, parent, false);
        }

        ImageModel item = getItem(position);
        if (item != null) {
            ImageView imageView = convertView.findViewById(R.id.imageViewItem);
            String base64Image = item.getBase64Image();
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageView.setImageBitmap(decodedByte);
        }
        return convertView;
    }
}
