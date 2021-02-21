package com.atanasvasil.mobile.mycardocs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.responses.cars.Car;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final List<Car> data;

    public CarAdapter(Context context, List<Car> data) {
        this.layoutInflater = LayoutInflater.from(context);
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

//            String name = car.getName();
//            holder.getSightItemNameTV().setText(name);
//
//            String imageUrl = BASE_URL + "images/sights/" + sight.getImage();
//            Picasso.get().load(imageUrl).into(holder.getSightItemImageIV());

        holder.getCarItemBrandTV().setText(car.getBrand());
        holder.getCarItemModelTV().setText(car.getModel());
//        holder.getHotelsStarsRB().setNumStars(sight.getStars());

//            holder.getSightItemLocationTV().setText(sight.getLocation());

        holder.getCardView().setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "CAR CLICKED: " + car.getCarId(), Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(v.getContext(), CarActivity.class);
//            intent.putExtra("carId", car.getCarId());
//            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // The layout wrapper
        private final CardView cardView;
        private final TextView carItemBrandTV;
        private final TextView carItemModelTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.carCard);

            carItemBrandTV = itemView.findViewById(R.id.carItemBrandTV);
            carItemModelTV = itemView.findViewById(R.id.carItemModelTV);
        }

        public CardView getCardView() {
            return cardView;
        }

        public TextView getCarItemBrandTV() {
            return carItemBrandTV;
        }

        public TextView getCarItemModelTV() {
            return carItemModelTV;
        }
    }
}
