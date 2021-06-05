package com.aarshinkov.mobile.mycardocs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aarshinkov.mobile.mycardocs.R;
import com.aarshinkov.mobile.mycardocs.activities.service.ServiceExpenseActivity;
import com.aarshinkov.mobile.mycardocs.responses.cars.Car;
import com.aarshinkov.mobile.mycardocs.responses.expenses.service.ServiceExpense;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.aarshinkov.mobile.mycardocs.utils.Utils.getMonthName;
import static com.aarshinkov.mobile.mycardocs.utils.Utils.getStringResource;

public class ServiceExpenseAdapter extends RecyclerView.Adapter<ServiceExpenseAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private final List<ServiceExpense> data;

    public ServiceExpenseAdapter(Context context, List<ServiceExpense> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ServiceExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_service_expense, parent, false);
        return new ServiceExpenseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceExpenseAdapter.ViewHolder holder, int position) {

        ServiceExpense serviceExpense = data.get(position);

        long timestamp = serviceExpense.getCreatedOn().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);

        int date = cal.get(Calendar.DATE);
        int monthNum = cal.get(Calendar.MONTH) + 1;
        String month = getMonthName(context, monthNum);
        int year = cal.get(Calendar.YEAR);
//        fuelExpense.getCreatedOn().get

        holder.getSeiDateTV().setText(String.valueOf(date));
        holder.getSeiMonthTV().setText(month.toUpperCase());
        holder.getSeiYearTV().setText(String.valueOf(year));

        final String priceFormatted = String.format(Locale.getDefault(), "%.2f", serviceExpense.getPrice());

        // NEGATIVE
//        holder.getSeiPriceTV().setText(context.getString(R.string.negative_number, context.getString(R.string.price_bgn, totalFormatted)));
        holder.getSeiPriceTV().setText(context.getString(R.string.price_bgn, priceFormatted));

        final Integer type = serviceExpense.getType().getType();

        holder.getSeiExpenseTypeTV().setText(getStringResource(context, "service_type_" + type));

        switch (type) {
            case 1:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_service);
                break;
            case 2:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_engine);
                break;
            case 3:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_tyre);
                break;
            case 4:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_oil);
                break;
            case 5:
            case 6:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_car_filter);
                break;
            case 7:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_battery);
                break;
            case 8:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_balance);
                break;
            case 9:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_belt);
                break;
            case 10:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_tow);
                break;
            case 11:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_lights);
                break;
            case 12:
                holder.getSeiTypeIV().setImageResource(R.drawable.ic_repair);
                break;
        }

        final Car car = serviceExpense.getCar();

        holder.getSeiCarTV().setText(car.getBrand() + " " + car.getModel() + " (" + car.getLicensePlate() + ")");

        holder.getSeiCardView().setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), ServiceExpenseActivity.class);
            intent.putExtra("serviceExpenseId", serviceExpense.getServiceExpenseId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView seiCardView;
        private final TextView seiDateTV;
        private final TextView seiMonthTV;
        private final TextView seiYearTV;
        private final TextView seiPriceTV;
        private final ImageView seiTypeIV;
        private final TextView seiExpenseTypeTV;
        private final TextView seiCarTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            seiCardView = itemView.findViewById(R.id.seiCardView);
            seiDateTV = itemView.findViewById(R.id.seiDateTV);
            seiMonthTV = itemView.findViewById(R.id.seiMonthTV);
            seiYearTV = itemView.findViewById(R.id.seiYearTV);
            seiPriceTV = itemView.findViewById(R.id.seiPriceTV);
            seiTypeIV = itemView.findViewById(R.id.seiTypeIV);
            seiExpenseTypeTV = itemView.findViewById(R.id.seiExpenseTypeTV);
            seiCarTV = itemView.findViewById(R.id.seiCarTV);
        }

        public CardView getSeiCardView() {
            return seiCardView;
        }

        public TextView getSeiDateTV() {
            return seiDateTV;
        }

        public TextView getSeiMonthTV() {
            return seiMonthTV;
        }

        public TextView getSeiYearTV() {
            return seiYearTV;
        }

        public TextView getSeiPriceTV() {
            return seiPriceTV;
        }

        public ImageView getSeiTypeIV() {
            return seiTypeIV;
        }

        public TextView getSeiExpenseTypeTV() {
            return seiExpenseTypeTV;
        }

        public TextView getSeiCarTV() {
            return seiCarTV;
        }
    }
}
