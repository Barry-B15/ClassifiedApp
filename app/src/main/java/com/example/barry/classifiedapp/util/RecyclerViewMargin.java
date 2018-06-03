package com.example.barry.classifiedapp.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by barry on 12/17/2017.
 */

// have the class extend RecyclerView.ItemDecorator
public class RecyclerViewMargin extends RecyclerView.ItemDecoration{

    private final int columns;
    private int margin;

    public RecyclerViewMargin(int columns, int margin) {
        this.columns = columns;
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);
        outRect.right = margin;
        outRect.bottom = margin;

        if(position < columns){
            outRect.top = margin;
        }

        if(position % columns == 0) {
            outRect.left = margin;
        }
    }

    /*private final int columns;
    private int margin;

    // insert constructors
    public RecyclerViewMargin(int columns, int margin) {
        this.columns = columns;
        this.margin = margin;
    }

    // insert override method "getItemOffsets()

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        outRect.right = margin;
        outRect.bottom = margin;

        // put margins to the items
        if (position < columns) {
            outRect.top = margin;
        }

        if (position % columns == 0) {
            outRect.left = margin;
        }
        // now add this decorator to the searchFragment
    }*/
}
