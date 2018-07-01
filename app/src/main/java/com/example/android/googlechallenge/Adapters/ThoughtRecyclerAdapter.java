package com.example.android.googlechallenge.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.googlechallenge.EditorActivity;
import com.example.android.googlechallenge.Models.Thought;
import com.example.android.googlechallenge.R;

import java.util.List;

public class ThoughtRecyclerAdapter extends RecyclerView.Adapter<ThoughtRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private final List<Thought> mThoughts;
    private static final String EDIT_INTENT_KEY = "EDIT_THOUGHT_KEY";

    public ThoughtRecyclerAdapter(Context context, List<Thought> thoughts) {
        mContext = context;
        mThoughts = thoughts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View currentView = LayoutInflater.from(mContext).inflate(R.layout.thought_item, parent, false);
        return new ViewHolder(currentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    public void clear() {
        mThoughts.clear();
    }

    public void addAll(List<Thought> thoughts) {
        mThoughts.addAll(thoughts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mThoughts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Thought currentThought = mThoughts.get(position);
            // setup thought title
            TextView titleTv = itemView.findViewById(R.id.thought_title);
            titleTv.setText(currentThought.getTitle());
            // setup thought content
            TextView contentTv = itemView.findViewById(R.id.thought_content);
            contentTv.setText(currentThought.getContent());
        }

        @Override
        public void onClick(View v) {
            int selectedThoughtIndex = getAdapterPosition();
            Thought selectedThought = mThoughts.get(selectedThoughtIndex);
            Intent editIntent = new Intent(mContext, EditorActivity.class);
            editIntent.putExtra(EDIT_INTENT_KEY, selectedThought);
            mContext.startActivity(editIntent);
        }
    }

    public static class ThoughtItemsDivider extends RecyclerView.ItemDecoration {
        private final Drawable mDivider;

        public ThoughtItemsDivider(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.thougt_item_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
