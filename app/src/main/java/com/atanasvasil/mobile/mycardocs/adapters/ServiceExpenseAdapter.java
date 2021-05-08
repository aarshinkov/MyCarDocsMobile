package com.atanasvasil.mobile.mycardocs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.fuel.FuelExpenseActivity;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;
import com.atanasvasil.mobile.mycardocs.responses.expenses.fuel.FuelExpense;
import com.atanasvasil.mobile.mycardocs.responses.expenses.service.ServiceExpense;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.atanasvasil.mobile.mycardocs.utils.Utils.getMonthName;

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
//        int month = cal.get(Calendar.MONTH);
        String month = getMonthName(context, 4);
        int year = cal.get(Calendar.YEAR);
//        fuelExpense.getCreatedOn().get

        holder.getSeiDateTV().setText(String.valueOf(date));
        holder.getSeiMonthTV().setText(month.toUpperCase());
        holder.getSeiYearTV().setText(String.valueOf(year));

        final String priceFormatted = String.format(Locale.getDefault(), "%.2f", serviceExpense.getPrice());

        // NEGATIVE
//        holder.getSeiPriceTV().setText(context.getString(R.string.negative_number, context.getString(R.string.price_bgn, totalFormatted)));
        holder.getSeiPriceTV().setText(context.getString(R.string.price_bgn, priceFormatted));

        final Car car = serviceExpense.getCar();

        holder.getSeiCarTV().setText(car.getBrand() + " " + car.getModel() + " (" + car.getLicensePlate() + ")");

        holder.getSeiCardView().setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), FuelExpenseActivity.class);
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
        private final TextView seiExpenseTypeTV;
        private final TextView seiCarTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            seiCardView = itemView.findViewById(R.id.seiCardView);
            seiDateTV = itemView.findViewById(R.id.seiDateTV);
            seiMonthTV = itemView.findViewById(R.id.seiMonthTV);
            seiYearTV = itemView.findViewById(R.id.seiYearTV);
            seiPriceTV = itemView.findViewById(R.id.seiPriceTV);
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

        public TextView getSeiExpenseTypeTV() {
            return seiExpenseTypeTV;
        }

        public TextView getSeiCarTV() {
            return seiCarTV;
        }
    }
}
