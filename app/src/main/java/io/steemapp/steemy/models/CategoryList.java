package io.steemapp.steemy.models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("result")
    @Expose
    private List<Category> mCategoryList = new ArrayList<Category>();

    private Category mCurrent;

    public String getCurrent() {
        if(mCurrent == null)
            return "none";
        return mCurrent.getName();
    }

    public void setCurrent(Category mCurrent) {
        this.mCurrent = mCurrent;
    }

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The result
     */
    public List<Category> getCategoryList() {
        return mCategoryList;
    }

    /**
     *
     * @param mCategoryList
     * The result
     */
    public void setCategoryList(List<Category> mCategoryList) {
        this.mCategoryList = mCategoryList;
    }

    public void processCategories(){
        for(Category c : mCategoryList){
            c.formatTime();
        }
    }

    public ArrayList<String> getCategoryNames(){
        ArrayList<String> cats = new ArrayList<>();
        for(Category c : mCategoryList){
            cats.add(c.getName());
        }

        return cats;
    }

}