package unicommsapp.application.com.unicomppsapp.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import unicommsapp.application.com.unicomppsapp.R;
import unicommsapp.application.com.unicomppsapp.models.Category;

/**
 * Created by fahmed on 23.2.2016.
 */
public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    private Activity context;
    List<Category> categories = null;
    Category category;

    public CategorySpinnerAdapter(Activity context, int resource, List<Category> categories)
    {
        super(context, resource, categories);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.spinner, parent, false);

        category = null;
        category = (Category) this.categories.get(position);

        TextView catText = (TextView) row.findViewById(R.id.categorySpinner);
        catText.setText(category.getName());


        return row;
    }
}
