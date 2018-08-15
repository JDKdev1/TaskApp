package sample.magics.com.myapplication2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidnetworking.interfaces.JSONArrayRequestListener;

public class CustomAdapter extends ArrayAdapter<String>{

    private Activity activity;
    private List data;
    public Resources res;
    StateModel tempValues = null;
    LayoutInflater inflater;

    /*************
     * CustomAdapter Constructor
     *****************/
    public CustomAdapter(
            Activity activitySpinner,
            int textViewResourceId,
            List objects,
            Resources resLocal
    ) {
        super(activitySpinner, textViewResourceId, objects);

        /********** Take passed values **********/
        activity = activitySpinner;
        data = objects;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () **********************/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/

        View row = inflater.inflate(R.layout.spinner_item, parent, false);

        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (StateModel) data.get(position);

        TextView label = (TextView) row.findViewById(R.id.spin_text);


        label.setText(tempValues.getName());

        System.err.println("++++++++++++" + tempValues.getId());
//        }

        return row;
    }
}