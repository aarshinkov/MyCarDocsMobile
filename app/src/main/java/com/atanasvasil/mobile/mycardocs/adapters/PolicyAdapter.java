package com.atanasvasil.mobile.mycardocs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.atanasvasil.mobile.mycardocs.R;
import com.atanasvasil.mobile.mycardocs.responses.policies.Policy;

import java.util.List;

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
        String type = getStringResource(context, "policy_type_" + policy.getType());
        holder.getPolicyItemTypeTV().setText(type);
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView policyItemTypeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.policyCard);
            policyItemTypeTV = itemView.findViewById(R.id.policyItemTypeTV);
        }

        public CardView getCardView() {
            return cardView;
        }

        public TextView getPolicyItemTypeTV() {
            return policyItemTypeTV;
        }
    }

}
