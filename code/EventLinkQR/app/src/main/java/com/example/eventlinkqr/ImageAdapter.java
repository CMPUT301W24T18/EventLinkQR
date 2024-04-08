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
 * An ArrayAdapter for handling a collection of ImageModel objects.
 * This adapter is responsible for converting each ImageModel into a view within a ListView.
 */
public class ImageAdapter extends ArrayAdapter<ImageModel> {
    private int resourceLayout;

    /**
     * Constructor for ImageAdapter.
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when instantiating views.
     * @param items The list of ImageModel objects to represent in the ListView.
     */
    public ImageAdapter(@NonNull Context context, int resource, @NonNull List<ImageModel> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     * @param position The position in the data set of the data item whose view we want.
     * @param convertView The old view to reuse, if possible. If not possible, a new view is inflated.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
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
