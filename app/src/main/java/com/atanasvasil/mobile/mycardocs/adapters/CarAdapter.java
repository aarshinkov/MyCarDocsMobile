package com.atanasvasil.mobile.mycardocs.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.activities.cars.CarActivity;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private final List<Car> data;

    public CarAdapter(Context context, List<Car> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_car, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Car car = data.get(position);

        holder.getCarItemInfoTV().setText(context.getString(R.string.car_info, car.getBrand(), car.getModel()));
        holder.getCarItemYearTV().setText(String.valueOf(car.getYear()));
        holder.getCarItemLicensePlateTV().setText(car.getLicensePlate());

        holder.getCardView().setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CarActivity.class);
            intent.putExtra("carId", car.getCarId());
            Pair<View, String> p1 = Pair.create(holder.getCarItemInfoTV(), "carInfo");
            Pair<View, String> p2 = Pair.create(holder.getCarItemYearTV(), "carYear");
            Pair<View, String> p3 = Pair.create(holder.getCarItemLicensePlateTV(), "carLicensePlate");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.getCarItemInfoTV(), "carInfo");
            context.startActivity(intent, options.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // The layout wrapper
        private final CardView cardView;
        private final TextView carItemInfoTV;
        private final TextView carItemYearTV;
        private final TextView carItemLicensePlateTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.carCard);

            carItemInfoTV = itemView.findViewById(R.id.carItemInfoTV);
            carItemYearTV = itemView.findViewById(R.id.carItemYearTV);
            carItemLicensePlateTV = itemView.findViewById(R.id.carItemLicensePlateTV);
        }

        public CardView getCardView() {
            return cardView;
        }

        public TextView getCarItemInfoTV() {
            return carItemInfoTV;
        }

        public TextView getCarItemYearTV() {
            return carItemYearTV;
        }

        public TextView getCarItemLicensePlateTV() {
            return carItemLicensePlateTV;
        }
    }
}
