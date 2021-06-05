package com.aarshinkov.mobile.mycardocs.collections;

import com.aarshinkov.mobile.mycardocs.responses.expenses.fuel.FuelExpense;
import com.aarshinkov.mobile.mycardocs.utils.PageImpl;

import java.io.Serializable;
import java.util.List;

/**
 * @author Atanas Yordanov Arshinkov
 * @since 2.0.0
 */
public class FuelExpensesCollection implements Serializable {

    private PageImpl page;
    private List<FuelExpense> data;

    public PageImpl getPage() {
        return page;
    }

    public void setPage(PageImpl page) {
        this.page = page;
    }

    public List<FuelExpense> getData() {
        return data;
    }

    public void setData(List<FuelExpense> data) {
        this.data = data;
    }
}
