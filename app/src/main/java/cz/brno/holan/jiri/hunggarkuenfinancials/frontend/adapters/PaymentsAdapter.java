package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cz.brno.holan.jiri.hunggarkuenfinancials.R;
import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.Payment;

/**
 * Created by mhatina on 24/08/16.
 */
public class PaymentsAdapter extends ArrayAdapter<Payment> {
    public PaymentsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PaymentsAdapter(Context context, int resource, List<Payment> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());

            convertView = layoutInflater.inflate(R.layout.layout_member, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Payment payment = getItem(position);

        if (payment != null) {
            viewHolder.textView.setText(String.valueOf(payment.getID()));
        }

        return convertView;
    }

    public class ViewHolder {
        public TextView textView;

        ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.member_layout_first_name);
        }
    }
}
