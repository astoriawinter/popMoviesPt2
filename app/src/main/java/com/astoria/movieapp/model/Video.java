
package com.astoria.movieapp.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Video {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<ResultVideo> resultVideos = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ResultVideo> getResultVideos() {
        return resultVideos;
    }

    public void setResultVideos(List<ResultVideo> resultVideos) {
        this.resultVideos = resultVideos;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("resultVideos", resultVideos).toString();
    }

}
