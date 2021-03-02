package com.atanasvasil.mobile.mycardocs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.atanasvasil.mobile.mycardocs.utils.Utils.getStringResource;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<Policy> data;

    public PolicyAdapter(Context context, List<Policy> data) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.policy_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Policy policy = data.get(position);

        holder.getStatus().setBackgroundColor(context.getResources().getColor(R.color.success));
        holder.getStatus().setTooltipText("Valid policy.");
        holder.getStatusIcon().setImageResource(R.drawable.ic_check);

        String type = getStringResource(context, "policy_type_" + policy.getType());
        holder.getTypeTV().setText(type);

        holder.getNumberTV().setText(policy.getNumber());

        holder.getInsurerNameTV().setText(policy.getInsName());
        holder.getLicensePlateTV().setText(policy.getCar().getLicensePlate());

        holder.getStatus().setTooltipText("Valid policy.");

        Date now = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        Date date = new Date();
        date.setTime(policy.getStartDate().getTime());
        holder.getStartDateTV().setText(sdf.format(date));

        if (now.before(date)) {
            holder.getStatus().setBackgroundColor(context.getResources().getColor(R.color.warning));
            holder.getStatus().setTooltipText("Pending policy.");
            holder.getStatusIcon().setImageResource(R.drawable.ic_time);
        }

        date = new Date();
        date.setTime(policy.getEndDate().getTime());
        holder.getEndDateTV().setText(sdf.format(date));

        if (now.after(date)) {
            holder.getStatus().setBackgroundColor(context.getResources().getColor(R.color.danger));
            holder.getStatus().setTooltipText("Expired policy.");
            holder.getStatusIcon().setImageResource(R.drawable.ic_close);
        }
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView typeTV;
        private final TextView numberTV;
        private final TextView insurerNameTV;
        private final TextView licensePlateTV;
        private final TextView startDateTV;
        private final TextView endDateTV;
        private final View status;
        private final ImageView statusIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.policyCard);
            typeTV = itemView.findViewById(R.id.policyItemTypeTV);
            numberTV = itemView.findViewById(R.id.policyItemNumberTV);
            insurerNameTV = itemView.findViewById(R.id.policyItemInsurerNameTV);
            licensePlateTV = itemView.findViewById(R.id.policyItemLicensePlateTV);
            startDateTV = itemView.findViewById(R.id.policyItemStartDateTV);
            endDateTV = itemView.findViewById(R.id.policyItemEndDateTV);
            status = itemView.findViewById(R.id.carItemCarIconView);
            statusIcon = itemView.findViewById(R.id.policyItemStatusIcon);
        }

        public CardView getCardView() {
            return cardView;
        }

        public TextView getTypeTV() {
            return typeTV;
        }

        public TextView getNumberTV() {
            return numberTV;
        }

        public TextView getInsurerNameTV() {
            return insurerNameTV;
        }

        public TextView getLicensePlateTV() {
            return licensePlateTV;
        }

        public TextView getStartDateTV() {
            return startDateTV;
        }

        public TextView getEndDateTV() {
            return endDateTV;
        }

        public View getStatus() {
            return status;
        }

        public ImageView getStatusIcon() {
            return statusIcon;
        }
    }
}
