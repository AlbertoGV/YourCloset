package com.example.albertogv.yourcloset;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.albertogv.yourcloset.model.Anuncio;
import com.example.albertogv.yourcloset.model.Chat;
import com.example.albertogv.yourcloset.model.Mensaje;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference mRef;
    String uid;
    String chatKey;
    String productKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRef = FirebaseDatabase.getInstance().getReference();
        uid = "uid-" + FirebaseAuth.getInstance().getUid();

        chatKey = getIntent().getStringExtra("CHAT_KEY");

        if(chatKey != null) {
            // se ha entrado desde "MyChats", el chat ya existe, cargo los mensajes
            loadChatInfo(chatKey);
            loadMessages(chatKey);
        } else {
            // se ha entrado desde "Products"
            productKey = getIntent().getStringExtra("PRODUCT_KEY");


            // consulto si existe un chat para ese producto
            mRef.child("products/product-chats").child(productKey).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatKey = dataSnapshot.getValue(String.class);

                    if (chatKey != null) {
                        // si ya existe un chat para ese producto cargo los mensajes
                        loadChatInfo(chatKey);
                        loadMessages(chatKey);
                    } else {
                        mRef.child("products/data").child(productKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Anuncio anuncio = dataSnapshot.getValue(Anuncio.class);
                                if (anuncio != null) {
                                    showChatInfo(anuncio.photoUrl, anuncio.description, anuncio.displayName);
                                }else{
                                    System.out.println("no rula");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = ((EditText) findViewById(R.id.etMessage)).getText().toString();
                final String messageKey = "message-" + mRef.push().getKey();

                if (chatKey == null) {
                    mRef.child("products/data").child(productKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Anuncio anuncio= dataSnapshot.getValue(Anuncio.class);

                            if(anuncio == null) return;

                            chatKey = "chat-" + mRef.push().getKey();

                            Chat chat = new Chat();
                            chat.productPhotoUrl = anuncio.photoUrl;
                            chat.productDescription = anuncio.description;
                            chat.lastMessage = message;

                            mRef.child("products/product-chats").child(productKey).child(uid).setValue(chatKey);
                            mRef.child("chats/data").child(chatKey).setValue(chat);
                            mRef.child("chats/user-chats").child(uid).child(chatKey).setValue(true);
                            mRef.child("chats/user-chats").child(anuncio.uid).child(chatKey).setValue(true);

                            mRef.child("chats/chat-messages").child(chatKey).child(messageKey).setValue(new Mensaje(uid, message));

                            loadChatInfo(chatKey);
                            loadMessages(chatKey);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                } else {
                    mRef.child("chats/chat-messages").child(chatKey).child(messageKey).setValue(new Mensaje(uid, message));
                }
            }
        });
    }

    void loadChatInfo(final String chatKey){
        mRef.child("chats/data").child(chatKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat.buyerUid == uid) {
                        showChatInfo(chat.productPhotoUrl, chat.productDescription, chat.sellerDispalyName);
                    } else {
                        showChatInfo(chat.productPhotoUrl, chat.productDescription, chat.buyerDisplayName);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    void showChatInfo(String productPhotoUrl, String productDescription, String otherDisplayName){
        ((TextView) findViewById(R.id.tvProductDescription)).setText(productDescription);
        Glide.with(ChatActivity.this).load(productPhotoUrl).into((ImageView) findViewById(R.id.ivProductPhoto));
        ((TextView) findViewById(R.id.tvOtherDisplayName)).setText(otherDisplayName);
    }

    void loadMessages(String chatId) {
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Mensaje>()
                .setQuery(mRef.child("chats/chat-messages").child(chatId), Mensaje.class)
                .setLifecycleOwner(this)
                .build();

        MessagesAdapter adapter = new MessagesAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.rvMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    class MessagesAdapter extends FirebaseRecyclerAdapter<Mensaje, MessagesAdapter.MessageViewHolder> {

        class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView message;

            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                message = itemView.findViewById(R.id.tvMessage);
            }
        }

        public MessagesAdapter(@NonNull FirebaseRecyclerOptions<Mensaje> options) {
            super(options);
        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            return new MessageViewHolder(inflater.inflate(R.layout.message_viewholder, viewGroup, false));
        }

        @Override
        protected void onBindViewHolder(@NonNull MessageViewHolder holder, final int position, @NonNull final Mensaje mensaje) {
            holder.message.setText(mensaje.message);
        }
    }
}