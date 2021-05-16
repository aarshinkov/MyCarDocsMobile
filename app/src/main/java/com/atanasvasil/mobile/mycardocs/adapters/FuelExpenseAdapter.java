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

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.atanasvasil.mobile.mycardocs.utils.Utils.getMonthName;

public class FuelExpenseAdapter extends RecyclerView.Adapter<FuelExpenseAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private final List<FuelExpense> data;

    public FuelExpenseAdapter(Context context, List<FuelExpense> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public FuelExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_fuel_expense, parent, false);
        return new FuelExpenseAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FuelExpenseAdapter.ViewHolder holder, int position) {

        FuelExpense fuelExpense = data.get(position);

        long timestamp = fuelExpense.getCreatedOn().getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);

        int date = cal.get(Calendar.DATE);
        int monthNum = cal.get(Calendar.MONTH) + 1;
        String month = getMonthName(context, monthNum);
        int year = cal.get(Calendar.YEAR);
//        fuelExpense.getCreatedOn().get

        holder.getFeiDateTV().setText(String.valueOf(date));
        holder.getFeiMonthTV().setText(month.toUpperCase());
        holder.getFeiYearTV().setText(String.valueOf(year));

        Double total;
        if (fuelExpense.getDiscount() != null) {
            total = (fuelExpense.getPricePerLitre() * fuelExpense.getLitres()) - fuelExpense.getDiscount();
        } else {
            total = fuelExpense.getPricePerLitre() * fuelExpense.getLitres();
        }

        final String totalFormatted = String.format(Locale.getDefault(), "%.2f", total);

        // NEGATIVE
//        holder.getFeiTotalTV().setText(context.getString(R.string.negative_number, context.getString(R.string.price_bgn, totalFormatted)));
        holder.getFeiTotalTV().setText(context.getString(R.string.price_bgn, totalFormatted));

        final Car car = fuelExpense.getCar();

        holder.getFeiCarTV().setText(car.getBrand() + " " + car.getModel() + " (" + car.getLicensePlate() + ")");

        holder.getFeiCardView().setOnClickListener(v -> {
            Intent intent = new Intent(context.getApplicationContext(), FuelExpenseActivity.class);
            intent.putExtra("fuelExpenseId", fuelExpense.getFuelExpenseId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView feiCardView;
        private final TextView feiDateTV;
        private final TextView feiMonthTV;
        private final TextView feiYearTV;
        private final TextView feiTotalTV;
        private final TextView feiCarTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            feiCardView = itemView.findViewById(R.id.feiCardView);
            feiDateTV = itemView.findViewById(R.id.feiDateTV);
            feiMonthTV = itemView.findViewById(R.id.feiMonthTV);
            feiYearTV = itemView.findViewById(R.id.feiYearTV);
            feiTotalTV = itemView.findViewById(R.id.feiTotalTV);
            feiCarTV = itemView.findViewById(R.id.feiCarTV);
        }

        public CardView getFeiCardView() {
            return feiCardView;
        }

        public TextView getFeiDateTV() {
            return feiDateTV;
        }

        public TextView getFeiMonthTV() {
            return feiMonthTV;
        }

        public TextView getFeiYearTV() {
            return feiYearTV;
        }

        public TextView getFeiTotalTV() {
            return feiTotalTV;
        }

        public TextView getFeiCarTV() {
            return feiCarTV;
        }
    }
}
