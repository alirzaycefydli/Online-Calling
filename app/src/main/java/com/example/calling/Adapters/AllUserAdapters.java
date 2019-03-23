package com.example.calling.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.calling.MainActivity;
import com.example.calling.Models.User;
import com.example.calling.R;

import java.util.List;

public class AllUserAdapters extends RecyclerView.Adapter<AllUserAdapters.UserViewHolder> {

    private List<User> userList;
    private Context context;

    public AllUserAdapters(Context context,List<User> users){
        this.userList=users;
        this.context=context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_user_layout,viewGroup,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, final int i) {

        userViewHolder.user_name.setText(userList.get(i).getName());

        userViewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user=userList.get(i);
                ((MainActivity)context).callUsers(user);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView user_name;
        private ImageView user_image;
        private Button callButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image=itemView.findViewById(R.id.single_user_image);
            user_name=itemView.findViewById(R.id.single_user_name);
            callButton=itemView.findViewById(R.id.single_user_call);
        }
    }
}
