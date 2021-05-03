package com.atanasvasil.mobile.mycardocs.activities.fuel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.atanasvasil.mobile.mycardocs.R;

public class FuelExpenseActivity extends AppCompatActivity {

    private EditText fuelExpensePricePerLitreET;
    private EditText fuelExpenseLitresET;
    private EditText fuelExpenseTotalET;

    private TextView fuelExpensePricePerLitreSummaryET;
    private TextView fuelExpenseLitresSummaryET;
    private TextView fuelExpenseTotalSummaryET;

    private Double pricePerLitre = null;
    private Double litres = null;
    private Double total = null;

    private Boolean isFromUser = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_expense);

        getSupportActionBar().setTitle(R.string.fuel_expense_title);

        fuelExpensePricePerLitreET = findViewById(R.id.fuelExpensePricePerLitreET);
        fuelExpenseLitresET = findViewById(R.id.fuelExpenseLitresET);
        fuelExpenseTotalET = findViewById(R.id.fuelExpenseTotalET);

        fuelExpensePricePerLitreSummaryET = findViewById(R.id.fuelExpensePricePerLitreSummaryET);
        fuelExpenseLitresSummaryET = findViewById(R.id.fuelExpenseLitresSummaryET);
        fuelExpenseTotalSummaryET = findViewById(R.id.fuelExpenseTotalSummaryET);

        fuelExpensePricePerLitreET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isLitresActive = fuelExpenseLitresET.isEnabled() && litres != null;
                final boolean isTotalActive = fuelExpenseTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        pricePerLitre = Double.parseDouble(s.toString());

                        if (isLitresActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculateLitres();
                        }
                    } else {
                        pricePerLitre = null;
                        fuelExpenseLitresET.setEnabled(true);
                        fuelExpenseTotalET.setEnabled(true);

                        if (isLitresActive) {
                            total = null;
                            isFromUser = false;
                            fuelExpenseTotalET.setText("");
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            litres = null;
                            isFromUser = false;
                            fuelExpenseLitresET.setText("");
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fuelExpenseLitresET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = fuelExpensePricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isTotalActive = fuelExpenseTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        litres = Double.parseDouble(s.toString());

                        if (isPricePerLitreActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        litres = null;
                        fuelExpensePricePerLitreET.setEnabled(true);
                        fuelExpenseTotalET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            total = null;
                            isFromUser = false;
                            fuelExpenseTotalET.setText("");
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            fuelExpensePricePerLitreET.setText("");
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fuelExpenseTotalET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = fuelExpensePricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isLitresActive = fuelExpenseLitresET.isEnabled() && litres != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        total = Double.parseDouble(s.toString());

                        if (isPricePerLitreActive) {
                            calculateLitres();
                        }

                        if (isLitresActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        total = null;
                        fuelExpensePricePerLitreET.setEnabled(true);
                        fuelExpenseLitresET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            litres = null;
                            isFromUser = false;
                            fuelExpenseLitresET.setText("");
                            isFromUser = true;
                        }

                        if (isLitresActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            fuelExpensePricePerLitreET.setText("");
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void calculatePricePerLitre() {
        if (litres == 0.00) {
            pricePerLitre = 0.00;
        } else {
            pricePerLitre = total / litres;
        }
        isFromUser = false;
        fuelExpensePricePerLitreET.setEnabled(false);
        fuelExpensePricePerLitreET.setText(pricePerLitre.toString());
        isFromUser = true;
    }

    @SuppressLint("SetTextI18n")
    private void calculateLitres() {
        if (pricePerLitre == 0.00) {
            litres = 0.00;
        } else {
            litres = total / pricePerLitre;
        }
        isFromUser = false;
        fuelExpenseLitresET.setEnabled(false);
        fuelExpenseLitresET.setText(litres.toString());
        isFromUser = true;
    }

    @SuppressLint("SetTextI18n")
    private void calculateTotal() {
        total = pricePerLitre * litres;
        isFromUser = false;
        fuelExpenseTotalET.setEnabled(false);
        fuelExpenseTotalET.setText(total.toString());
        isFromUser = true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        onBackPressed();
        return true;
    }
}
