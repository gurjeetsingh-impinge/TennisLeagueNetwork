package com.tennisdc.tln.ui;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created  on 23-01-2015.
 */
public class EmptyRecyclerView extends RecyclerView {
    @Nullable
    View emptyView;

    int emptyViewLayoutId;

    public EmptyRecyclerView(Context context) { super(context); }

    public EmptyRecyclerView(Context context, AttributeSet attrs) { super(context, attrs); }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (emptyView == null) if (emptyViewLayoutId > 0)
            emptyView = LayoutInflater.from(getContext()).inflate(emptyViewLayoutId, this, false);

        if (emptyView != null) {
            Adapter adapter = getAdapter();
            emptyView.setVisibility((adapter != null && adapter.getItemCount() > 0) ? GONE : VISIBLE);
        }
    }

    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }
    };

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    public void setEmptyViewLayoutId(int emptyViewLayoutId) {
        this.emptyViewLayoutId = emptyViewLayoutId;
        checkIfEmpty();
    }
}