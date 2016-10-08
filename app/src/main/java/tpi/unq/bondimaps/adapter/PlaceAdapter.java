package tpi.unq.bondimaps.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tpi.unq.bondimaps.PlaceDetailActivity;
import tpi.unq.bondimaps.PlaceDetailFragment;
import tpi.unq.bondimaps.R;
import tpi.unq.bondimaps.model.Place;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private final List<Place> mValues;
    private final boolean mTwoPane;
    private FragmentManager fragmentManager;

    public PlaceAdapter(List<Place> places, boolean mTwoPane, FragmentManager fragmentManager) {
        mValues = places;
        this.mTwoPane = mTwoPane;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNamePlaceView.setText(mValues.get(position).getName());
        holder.mAddressPlaceView.setText(mValues.get(position).getAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(PlaceDetailFragment.ARG_ITEM_ID, holder.mItem.getName());
                    PlaceDetailFragment fragment = new PlaceDetailFragment();
                    fragment.setArguments(arguments);
                    fragmentManager.beginTransaction()
                            .replace(R.id.place_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PlaceDetailActivity.class);
                    intent.putExtra(PlaceDetailFragment.ARG_ITEM_ID, holder.mItem.getName());

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNamePlaceView;
        public final TextView mAddressPlaceView;
        public Place mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNamePlaceView = (TextView) view.findViewById(R.id.namePlace);
            mAddressPlaceView = (TextView) view.findViewById(R.id.addressPlace);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAddressPlaceView.getText() + "'";
        }
    }
}