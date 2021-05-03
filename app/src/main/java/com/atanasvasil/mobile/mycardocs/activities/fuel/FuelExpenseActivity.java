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

import java.util.Locale;

public class FuelExpenseActivity extends AppCompatActivity {

    private EditText fuelExpensePricePerLitreET;
    private EditText fuelExpenseLitresET;
    private EditText fuelExpenseDiscountET;
    private EditText fuelExpenseTotalET;

    private TextView fuelExpensePricePerLitreSummaryTV;
    private TextView fuelExpenseLitresSummaryTV;
    private TextView fuelExpenseDiscountSummaryTV;
    private TextView fuelExpenseTotalSummaryTV;

    private Double pricePerLitre = null;
    private Double litres = null;
    private Double discount = null;
    private Double total = null;

    private Boolean isFromUser = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_expense);

        getSupportActionBar().setTitle(R.string.fuel_expense_title);

        final String zeroFormatted = String.format(Locale.getDefault(), "%.2f", 0.00);

        fuelExpensePricePerLitreET = findViewById(R.id.fuelExpensePricePerLitreET);
        fuelExpenseLitresET = findViewById(R.id.fuelExpenseLitresET);
        fuelExpenseDiscountET = findViewById(R.id.fuelExpenseDiscountET);
        fuelExpenseTotalET = findViewById(R.id.fuelExpenseTotalET);

        fuelExpensePricePerLitreSummaryTV = findViewById(R.id.fuelExpensePricePerLitreSummaryTV);
        fuelExpenseLitresSummaryTV = findViewById(R.id.fuelExpenseLitresSummaryTV);
        fuelExpenseDiscountSummaryTV = findViewById(R.id.fuelExpenseDiscountSummaryTV);
        fuelExpenseTotalSummaryTV = findViewById(R.id.fuelExpenseTotalSummaryTV);

        fuelExpensePricePerLitreSummaryTV.setText(zeroFormatted);
        fuelExpenseLitresSummaryTV.setText(zeroFormatted);
        fuelExpenseDiscountSummaryTV.setText(zeroFormatted);
        fuelExpenseTotalSummaryTV.setText(zeroFormatted);

        fuelExpensePricePerLitreET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isLitresActive = fuelExpenseLitresET.isEnabled() && litres != null;
                final boolean isTotalActive = fuelExpenseTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        pricePerLitre = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", pricePerLitre);
                        fuelExpensePricePerLitreSummaryTV.setText(formattedPrice);

                        if (isLitresActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculateLitres();
                        }
                    } else {
                        pricePerLitre = null;
                        fuelExpensePricePerLitreSummaryTV.setText(zeroFormatted);
                        fuelExpenseLitresET.setEnabled(true);
                        fuelExpenseTotalET.setEnabled(true);

                        if (isLitresActive) {
                            total = null;
                            isFromUser = false;
                            fuelExpenseTotalET.setText("");
                            fuelExpenseTotalSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            litres = null;
                            isFromUser = false;
                            fuelExpenseLitresET.setText("");
                            fuelExpenseLitresSummaryTV.setText(zeroFormatted);
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

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = fuelExpensePricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isTotalActive = fuelExpenseTotalET.isEnabled() && total != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        litres = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", litres);
                        fuelExpenseLitresSummaryTV.setText(formattedPrice);

                        if (isPricePerLitreActive) {
                            calculateTotal();
                        }

                        if (isTotalActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        litres = null;
                        fuelExpenseLitresSummaryTV.setText(zeroFormatted);
                        fuelExpensePricePerLitreET.setEnabled(true);
                        fuelExpenseTotalET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            total = null;
                            isFromUser = false;
                            fuelExpenseTotalET.setText("");
                            fuelExpenseTotalSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }

                        if (isTotalActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            fuelExpensePricePerLitreET.setText("");
                            fuelExpensePricePerLitreSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fuelExpenseDiscountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    discount = Double.parseDouble(s.toString());
                    final String formattedPrice = String.format(Locale.getDefault(), "%.2f", discount);
                    fuelExpenseDiscountSummaryTV.setText(formattedPrice);
                } else {
                    discount = null;
                    fuelExpenseDiscountSummaryTV.setText(zeroFormatted);
                }
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fuelExpenseTotalET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                final boolean isPricePerLitreActive = fuelExpensePricePerLitreET.isEnabled() && pricePerLitre != null;
                final boolean isLitresActive = fuelExpenseLitresET.isEnabled() && litres != null;

                if (isFromUser) {
                    if (!s.toString().isEmpty()) {
                        total = Double.parseDouble(s.toString());
                        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", total);
                        fuelExpenseTotalSummaryTV.setText(formattedPrice);

                        if (isPricePerLitreActive) {
                            calculateLitres();
                        }

                        if (isLitresActive) {
                            calculatePricePerLitre();
                        }
                    } else {
                        total = null;
                        fuelExpenseTotalSummaryTV.setText(zeroFormatted);
                        fuelExpensePricePerLitreET.setEnabled(true);
                        fuelExpenseLitresET.setEnabled(true);

                        if (isPricePerLitreActive) {
                            litres = null;
                            isFromUser = false;
                            fuelExpenseLitresET.setText("");
                            fuelExpenseLitresSummaryTV.setText(zeroFormatted);
                            isFromUser = true;
                        }

                        if (isLitresActive) {
                            pricePerLitre = null;
                            isFromUser = false;
                            fuelExpensePricePerLitreET.setText("");
                            fuelExpensePricePerLitreSummaryTV.setText(zeroFormatted);
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
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", pricePerLitre);
        fuelExpensePricePerLitreSummaryTV.setText(formattedPrice);
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
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", litres);
        fuelExpenseLitresSummaryTV.setText(formattedPrice);
        isFromUser = true;
    }

    @SuppressLint("SetTextI18n")
    private void calculateTotal() {
        total = pricePerLitre * litres;

        if (discount != null) {
            if (discount != 0.00) {
                total = total - discount;
            }
        }

        isFromUser = false;
        fuelExpenseTotalET.setEnabled(false);
        fuelExpenseTotalET.setText(total.toString());
        final String formattedPrice = String.format(Locale.getDefault(), "%.2f", total);
        fuelExpenseTotalSummaryTV.setText(formattedPrice);
        isFromUser = true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        onBackPressed();
        return true;
    }
}
