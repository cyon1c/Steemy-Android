package io.steemapp.steemy.events;

import java.util.List;

import io.steemapp.steemy.models.Category;
import io.steemapp.steemy.models.CategoryList;

/**
 * Created by John on 7/28/2016.
 */
public class CategoriesEvent {
    public CategoryList mList;

    public CategoriesEvent(CategoryList list){
        mList = list;
    }
}
