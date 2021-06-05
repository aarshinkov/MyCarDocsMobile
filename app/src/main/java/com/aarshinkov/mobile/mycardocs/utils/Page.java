package com.aarshinkov.mobile.mycardocs.utils;

/**
 * @author Atanas Yordanov Arshinkov
 * @since 2.0.0
 */
public abstract class Page {

    protected Integer currentPage;
    protected Long localTotalElements;
    protected Long globalTotalElements;
    protected Integer maxElementsPerPage = 6;
    protected Integer startPage;
    protected Integer endPage;

    protected abstract Long getTotalPages();

    protected abstract boolean isFirst();

    protected abstract boolean isLast();

    protected abstract boolean hasNext(Integer currentPage);

    protected abstract boolean hasPrevious(Integer currentPage);

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Long getLocalTotalElements() {
        return localTotalElements;
    }

    public void setLocalTotalElements(Long localTotalElements) {
        this.localTotalElements = localTotalElements;
    }

    public Long getGlobalTotalElements() {
        return globalTotalElements;
    }

    public void setGlobalTotalElements(Long globalTotalElements) {
        this.globalTotalElements = globalTotalElements;
    }

    public Integer getMaxElementsPerPage() {
        return maxElementsPerPage;
    }

    public void setMaxElementsPerPage(Integer maxElementsPerPage) {
        this.maxElementsPerPage = maxElementsPerPage;
    }

    public Integer getStartPage() {
        return startPage;
    }

    public void setStartPage(Integer startPage) {
        this.startPage = startPage;
    }

    public Integer getEndPage() {
        return endPage;
    }

    public void setEndPage(Integer endPage) {
        this.endPage = endPage;
    }
}
