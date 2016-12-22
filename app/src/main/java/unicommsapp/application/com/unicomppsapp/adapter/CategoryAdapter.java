package unicommsapp.application.com.unicomppsapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unicommsapp.application.com.unicomppsapp.ChatMessage;
import unicommsapp.application.com.unicomppsapp.R;
import unicommsapp.application.com.unicomppsapp.models.Category;

/**
 * Created by GleasonK on 7/11/15.
 *
 * Custom adapter that takes a list of ChatMessages and fills
 * a chat_row_layout.xml view with the ChatMessage's information.
 */
public class CategoryAdapter extends ArrayAdapter<Category> {
    private final Context context;
    private LayoutInflater inflater;
    private List<Category> values;

    public CategoryAdapter(Context context, List<Category> values) {
        super(context, R.layout.category_row_layout, android.R.id.text1, values);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.values=values;
    }

    class ViewHolder {
        TextView categoryTitle;
        int id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // To be sure we have a view, because null is possible
        View itemView = convertView;
        if (itemView == null) {
            itemView = this.inflater.inflate(R.layout.category_row_layout, parent, false);
        }

        Category category = this.values.get(position);

        TextView catText = (TextView)itemView.findViewById(R.id.categoryTitle);
        catText.setText(category.getName());

        return itemView;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }
}