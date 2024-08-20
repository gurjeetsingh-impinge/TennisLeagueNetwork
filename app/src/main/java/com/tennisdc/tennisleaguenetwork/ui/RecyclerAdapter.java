package com.tennisdc.tennisleaguenetwork.ui;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created  on 21-01-2015.
 */
public abstract class RecyclerAdapter<T, T2 extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T2> {

    /*public static final int SELECTION_MODE_NONE = 0;
    public static final int SELECTION_MODE_ONE = 1;
    public static final int SELECTION_MODE_MULTI = 2;

    private int mSelectionMode = SELECTION_MODE_NONE;

    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();
*/
    private T[] mDataSet;

    public T getItem(int position) {
        return mDataSet[position];
    }

    public RecyclerAdapter(T[] dataSet) {
        mDataSet = dataSet;
    }

    public RecyclerAdapter(List<T> dataSet) {
        mDataSet = (T[]) dataSet.toArray();
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    /*public void setSelectionMode(int selectionMode) {
        mSelectionMode = selectionMode;
    }

    public void setItemChecked(int position, boolean isChecked) {
        if (mSelectionMode != SELECTION_MODE_NONE) {
            if (mSelectionMode == SELECTION_MODE_ONE) {
                if (getSelectedItemCount() > 0) clearSelections();
                mSelectedPositions.put(position, isChecked);
            } else mSelectedPositions.put(position, isChecked);
        }
    }

    public boolean isItemChecked(int position) {
        return mSelectionMode != SELECTION_MODE_NONE && mSelectedPositions.get(position);
    }

    public void clearSelections() {
        mSelectedPositions.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return mSelectedPositions.size();
    }*/

    public static class RecyclerSelectionHelper<T> {
        public static final int SELECTION_MODE_NONE = 0;
        public static final int SELECTION_MODE_ONE = 1;
        public static final int SELECTION_MODE_MULTI = 2;

        private int mSelectionMode = SELECTION_MODE_NONE;

        private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

        private RecyclerView mRecyclerView;

        public RecyclerSelectionHelper(RecyclerView recyclerView) {
            if (recyclerView == null) throw new RuntimeException("'recyclerView' cannot be null");
            mRecyclerView = recyclerView;
        }

        public void setSelectionMode(int selectionMode) {
            mSelectionMode = selectionMode;
        }

        public void setItemChecked(int position, boolean isChecked) {
            setItemChecked(position, isChecked, true);
        }

        public void setItemChecked(int position, boolean isChecked, boolean updateView) {
            if (mSelectionMode != SELECTION_MODE_NONE) {
                if (mSelectionMode == SELECTION_MODE_ONE)
                    if (getSelectedItemCount() > 0) clearSelections();

                if (!isChecked) mSelectedPositions.delete(position);
                else mSelectedPositions.put(position, true);

                if (updateView) {
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForItemId(position);
                    if (viewHolder != null) ((SelectionHelperCallbacks) viewHolder).setActivated(isChecked);
                }
            }
        }

        public SparseBooleanArray getSelectedPositions(){
            return mSelectedPositions;
        }

        public boolean isItemChecked(int position) {
            return mSelectionMode != SELECTION_MODE_NONE && mSelectedPositions.get(position);
        }

        public void clearSelections() {
            for (int i = 0; i < mSelectedPositions.size(); i++) {
                int position = mSelectedPositions.keyAt(i);
                if (mSelectedPositions.get(position)) {
                    ((SelectionHelperCallbacks) mRecyclerView.findViewHolderForLayoutPosition(position)).setActivated(false);
                }
            }

            mSelectedPositions.clear();
        }

        public int getSelectedItemCount() {
            return mSelectedPositions.size();
        }

        public static interface SelectionHelperCallbacks {

            public void setActivated(boolean active);

        }
    }

    public static interface ExpandableHolderCallbacks {

        View getChild();

        void expand();

        void collapse();

    }

    public static abstract class ExpandableViewHolder extends RecyclerView.ViewHolder implements ExpandableHolderCallbacks {

        private View mChildView;

        private ViewGroup mChildContainer;

        public ExpandableViewHolder(View itemView, int childContainerResId) {
            super(itemView);

            mChildContainer = (ViewGroup) itemView.findViewById(childContainerResId);
            mChildContainer.setVisibility(View.GONE);
        }

        public ViewGroup getChildContainer() {
            return mChildContainer;
        }

        @Override
        public void expand() {
            /* if child not initialised, do so and add to childContainer */
            if (mChildView == null) {
                mChildView = getChild();

                if (mChildView == null)
                    throw new RuntimeException("'getChild' returning null value");

                mChildContainer.removeAllViews();
                mChildView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mChildContainer.addView(mChildView);
            }

            mChildContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public void collapse() {
            mChildContainer.setVisibility(View.GONE);
        }

        public boolean isExpanded() {
            return mChildContainer.getVisibility() == View.VISIBLE;
        }

        public void toggle() {
            if (!isExpanded()) expand();
            else collapse();
        }

    }

}