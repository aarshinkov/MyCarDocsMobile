package com.atanasvasil.mobile.mycardocs.collections;

import com.atanasvasil.mobile.mycardocs.responses.expenses.service.ServiceExpense;
import com.atanasvasil.mobile.mycardocs.utils.PageImpl;

import java.io.Serializable;
import java.util.List;

/**
 * @author Atanas Yordanov Arshinkov
 * @since 2.0.0
 */
public class ServiceExpensesCollection implements Serializable {

    private PageImpl page;
    private List<ServiceExpense> data;

    public PageImpl getPage() {
        return page;
    }

    public void setPage(PageImpl page) {
        this.page = page;
    }

    public List<ServiceExpense> getData() {
        return data;
    }

    public void setData(List<ServiceExpense> data) {
        this.data = data;
    }
}
