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
        View view = layoutInflater.inflate(R.layout.car_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Car car = data.get(position);

        holder.getCarItemInfoTV().setText(context.getString(R.string.car_info, car.getBrand(), car.getModel()));
        holder.getCarItemYearTV().setText(String.valueOf(car.getYear()));
        holder.getCarItemLicensePlateTV().setText(car.getLicensePlate());

//        holder.getCarItemBrandTV().setText(car.getBrand());
//        holder.getCarItemModelTV().setText(car.getModel());
//        holder.getCarItemColorTV().setText(car.getColor());
//        holder.getCarItemYearTV().setText(String.valueOf(car.getYear()));

//        holder.getCarItemLicensePlateTV().setText(car.getLicensePlate());

        holder.getCardView().setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CarActivity.class);
            intent.putExtra("carId", car.getCarId());
            v.getContext().startActivity(intent);
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
//            carItemModelTV = itemView.findViewById(R.id.carItemModelTV);
//            carItemColorTV = itemView.findViewById(R.id.carItemColorTV);
//            carItemYearTV = itemView.findViewById(R.id.carItemYearTV);
//            carItemLicensePlateTV = itemView.findViewById(R.id.carItemLicensePlateTV);
//            carItemAliasTV = itemView.findViewById(R.id.carItemAliasTV);
//            carItemAddedOnTV = itemView.findViewById(R.id.carItemAddedOnTV);
//            carItemEditedOnTV = itemView.findViewById(R.id.carItemEditedOnTV);
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
