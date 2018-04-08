
package com.astoria.movieapp.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Movie {
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("results")
    @Expose
    private List<ResultMovie> resultMovies = null;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<ResultMovie> getResultMovies() {
        return resultMovies;
    }

    public void setResultMovies(List<ResultMovie> resultMovies) {
        this.resultMovies = resultMovies;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("page", page).append("totalResults", totalResults).append("totalPages", totalPages).append("resultMovies", resultMovies).toString();
    }

}
