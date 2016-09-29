package com.apps.jorge.monkeybisfire.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apps.jorge.monkeybisfire.R;
import com.apps.jorge.monkeybisfire.data.Constant;
import com.apps.jorge.monkeybisfire.model.Friend;
import com.apps.jorge.monkeybisfire.util.Database;
import com.apps.jorge.monkeybisfire.util.Image;
import com.apps.jorge.monkeybisfire.widget.CircleTransform;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> implements Filterable {

    private List<Friend> mFriends;
    private List<Friend> mFriendsFil;
    private ItemFilter mFilter = new ItemFilter();

    private Context ctx;

    private OnItemClickListener mOnItemClickListener;

    private DatabaseReference mFriendsRef;

    private OnItemLongClickListener mOnItemLongClickListener;
    private SparseBooleanArray selectedItems;


    public interface OnItemClickListener {
        void onItemClick(View view, Friend obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // for item long click listener
    public interface OnItemLongClickListener {
        void onItemClick(View view, Friend obj, int position);
    }

    public void setOnItemLongClickListener(final OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendsListAdapter(Context context) {
        mFriends = new ArrayList<>();
        mFriendsFil = new ArrayList<>();
        //Database Reference
        mFriendsRef = Database.getDatabase().getReference().child("users");
        mFriendsRef.keepSynced(true);
        mFriendsRef.addChildEventListener(new UsersChildEventListener());

        selectedItems = new SparseBooleanArray();
        ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public ImageView photo;
        public LinearLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            photo = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    public Filter getFilter() {
        return mFilter;
    }


    @Override
    public FriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_friends, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Friend c = mFriendsFil.get(position);
        String photoPath = Image.LOCAL_RESOURCE_PATH + c.getPhoto();
        holder.name.setText(c.getName());
        Picasso.with(ctx).load(new File(photoPath)).resize(100, 100).transform(new CircleTransform()).into(holder.photo);
//        Picasso.with(ctx).load(c.getPhoto()).resize(100, 100).transform(new CircleTransform()).into(holder.image);

        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, c, position);
                }
            }
        });

        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemClick(view, c, position);
                }
                return false;
            }
        });

        holder.lyt_parent.setActivated(selectedItems.get(position, false));
    }

    public Friend getItem(int position){
        return mFriendsFil.get(position);
    }

    /**
     * Here is the key method to apply the animation
     */
    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position){
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mFriendsFil.size();
    }

    /**
     * For multiple selection
     */
    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void removeSelectedItem(){
        List<Friend> items = getSelectedItems();
        for (Friend mf : items) {
               mFriendsRef.child(mf.getKey()).removeValue();
                //mFriendsFil.removeAll(items);
                notifyDataSetChanged();
            }
        }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Friend> getSelectedItems() {
        List<Friend> items = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(mFriendsFil.get(selectedItems.keyAt(i)));
        }
        return items;
    }

    // Rest Friend
    public void add(Friend friend) {
        mFriendsRef.push().setValue(friend);

//        TODO: Remove the next line(s) and use Firebase instead
//        mMovieQuotes.add(0, movieQuote);
//        notifyDataSetChanged();
    }

    public void update(Friend friend, String newName, String newCity, String newPhoto) {
        friend.setName(newName);
        friend.setCity(newCity);
        friend.setPhoto(newPhoto);
        mFriendsRef.child(friend.getKey()).setValue(friend);

        //TODO: Remove the next line(s) and use Firebase instead
//        notifyDataSetChanged();
    }

    // Search Filter
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            final List<Friend> list = mFriends;
            final List<Friend> result_list = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                String str_title = list.get(i).getName();
                if (str_title.toLowerCase().contains(query)) {
                    result_list.add(list.get(i));
                }
            }

            results.values = result_list;
            results.count = result_list.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFriendsFil = (List<Friend>) results.values;
            notifyDataSetChanged();
        }
    }

    private class UsersChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Friend friend = dataSnapshot.getValue(Friend.class);
            friend.setKey(dataSnapshot.getKey());
            mFriendsFil.add(0, friend);
            mFriends.add(0, friend);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Friend updateFriend = dataSnapshot.getValue(Friend.class);
            for (Friend mf : mFriendsFil) {
                if (mf.getKey().equals(key)) {
                    mf.setValues(updateFriend);
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for (Friend mf : mFriends) {
                if (mf.getKey().equals(key)) {
                    mFriends.remove(mf);
                    mFriendsFil.remove(mf);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            //nothing to do
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(Constant.TAG, "Database error:"  +databaseError);
        }
    }


}