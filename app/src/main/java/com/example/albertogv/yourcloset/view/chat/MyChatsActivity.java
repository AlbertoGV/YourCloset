package com.example.albertogv.yourcloset.view.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.albertogv.yourcloset.R;
import com.example.albertogv.yourcloset.model.Chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyChatsActivity extends AppCompatActivity {

    DatabaseReference mRef;
    String lastMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Mensajes");
        mRef = FirebaseDatabase.getInstance().getReference();
        String uid = "uid-"+ FirebaseAuth.getInstance().getUid();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Chat>()
                .setIndexedQuery(mRef.child("chats/user-chats").child(uid), mRef.child("chats/data"), Chat.class)
                .setLifecycleOwner(this)
                .build();
        lastMessage();
        ChatsAdapter chatsAdapter = new ChatsAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.rvMyChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatsAdapter);

    }
    private void lastMessage( ){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat= snapshot.getValue(Chat.class);
                    if(chat!=null) {
                        lastMessage = chat.lastMessage;
                        }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    class ChatsAdapter extends FirebaseRecyclerAdapter<Chat, ChatsAdapter.ChatViewHolder> {
        class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView lastMessage, title, name, date;
            ImageView photo;


            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                lastMessage = itemView.findViewById(R.id.tvLastMessage);
                title = itemView.findViewById(R.id.tvProductName);
                photo = itemView.findViewById(R.id.ivPhoto);
                name = itemView.findViewById(R.id.tvUserName);
                date = itemView.findViewById(R.id.tvFechaChats);
            }
        }

        public ChatsAdapter(@NonNull FirebaseRecyclerOptions<Chat> options) {
            super(options);
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            return new ChatViewHolder(inflater.inflate(R.layout.chat_viewholder, viewGroup, false));
        }

        @Override
        protected void onBindViewHolder(@NonNull ChatViewHolder holder, final int position, @NonNull final Chat chat) {
            final String chatKey = getRef(position).getKey();

            holder.title.setText(chat.productName);
            holder.name.setText(chat.sellerDispalyName);
            holder.lastMessage.setText(chat.lastMessage);
            holder.date.setText(chat.dateCreation);
            Glide.with(holder.itemView.getContext()).load(chat.productPhotoUrl).into(holder.photo);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    intent.putExtra("CHAT_KEY", chatKey);
                    view.getContext().startActivity(intent);
                }
            });



        }

    }
}
