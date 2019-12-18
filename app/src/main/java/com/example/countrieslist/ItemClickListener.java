package com.example.countrieslist;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private Context context;
    private GestureDetector gestureDetector;

    public ItemClickListener(Context c) {
        context=c;
        gestureDetector =new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        super.onTouchEvent(rv, e);
        if (gestureDetector.onTouchEvent(e)){ //Test event for Single Tap
            int id = rv.getChildAdapterPosition(rv.findChildViewUnder(e.getX(),e.getY())); //Find position of clicked Item in List
            if (id == RecyclerView.NO_POSITION) return false; //If clicking empty area...
            Intent intent = new Intent(context,CountryInfo.class);
            intent.putExtra("country",((CountryAdapter)rv.getAdapter()).dataSet.get(id));
            context.startActivity(intent);
            return true; //Event consumed, do nothing more
        }
        return false; //Event is not consumed, continue normal behaviour;
    }
}
