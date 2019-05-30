package com.example.albertogv.yourcloset.view.chat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.albertogv.yourcloset.R;
import com.example.albertogv.yourcloset.model.Anuncio;
import com.example.albertogv.yourcloset.model.Chat;
import com.example.albertogv.yourcloset.model.Mensaje;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    DatabaseReference mRef;
    String uid;
    String chatKey;
    EditText txtMensaje;
    public FirebaseUser mUser;
    Anuncio anuncio = new Anuncio();
    String nameBuyer;
    String productKey;
    Object timeStamp;
    Calendar rightNow = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRef = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        uid = "uid-" + FirebaseAuth.getInstance().getUid();

        final Chat chat= new Chat();

        android.support.v7.widget.Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(chat.productName);
        chatKey = getIntent().getStringExtra("CHAT_KEY");

        if (chatKey != null) {
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
                                        showChatInfo(anuncio.mediaUrl, anuncio.tituloAnuncio);

                                    }
                                }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtMensaje = findViewById(R.id.etMessage);
                final String message = txtMensaje.getText().toString();
                final String messageKey = "message-" + mRef.push().getKey();

                if (chatKey == null) {
                    mRef.child("products/data").child(productKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Anuncio anuncio = dataSnapshot.getValue(Anuncio.class);

                            if (anuncio == null) return;

                            chatKey = "chat-" + mRef.push().getKey();

                            Chat chat = new Chat();
                            chat.productPhotoUrl = anuncio.mediaUrl;
                            chat.productName = anuncio.tituloAnuncio;
                            chat.lastMessage = message;
                            chat.buyerUid = uid;
                            chat.sellerUid = anuncio.uid;
                            chat.sellerDispalyName = anuncio.getDisplayName();
                            chat.dateCreation = String.valueOf(Calendar.DATE);

                            mRef.child("products/product-chats").child(productKey).child(uid).setValue(chatKey);
                            mRef.child("chats/data").child(chatKey).setValue(chat);
                            mRef.child("chats/user-chats").child(uid).child(chatKey).setValue(true);
                            mRef.child("chats/user-chats").child(anuncio.uid).child(chatKey).setValue(true);

                            mRef.child("chats/chat-messages").child(chatKey).child(messageKey).setValue(new Mensaje(uid, message, mUser.getPhotoUrl().toString() , nameBuyer,anuncio.getAuthorPhotoUrl(),timeStamp));
                            loadChatInfo(chatKey);
                            loadMessages(chatKey);
                            txtMensaje.setText("");

                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {

                    mRef.child("chats/chat-messages").child(chatKey).child(messageKey).setValue(new Mensaje(uid, message, mUser.getPhotoUrl().toString(), nameBuyer,anuncio.getAuthorPhotoUrl(),timeStamp));
                    loadMessages(chatKey);
                    loadChatInfo(chatKey);
                    txtMensaje.setText("");

                }
            }
        });
    }




    void loadChatInfo(final String chatKey) {
        mRef.child("chats/data").child(chatKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (chat != null) {
                        showChatInfo(chat.productPhotoUrl, chat.productName);
                    } else {
                        showChatInfo(chat.productPhotoUrl, chat.productName);
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    void showChatInfo(String productPhotoUrl, String productName) {

        ((TextView) findViewById(R.id.tvProductName)).setText(productName);
        Glide.with(ChatActivity.this).load(productPhotoUrl).into((ImageView) findViewById(R.id.ivProductPhoto));

    }



    void loadMessages(String chatKey) {

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Mensaje>()
                .setQuery(mRef.child("chats/chat-messages").child(chatKey), Mensaje.class)
                .setLifecycleOwner(this)
                .build();

        final MessagesAdapter adapter = new MessagesAdapter(options);
        final RecyclerView recyclerView = findViewById(R.id.rvMessages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });


    }


    class MessagesAdapter extends FirebaseRecyclerAdapter<Mensaje, MessagesAdapter.MessageViewHolder> {

        public MessagesAdapter(@NonNull FirebaseRecyclerOptions<Mensaje> options) {
            super(options);
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView message;
            CircleImageView ivphotoBuyer;
            TextView tvHourMessage;


            MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                message = itemView.findViewById(R.id.tvMessage);
                ivphotoBuyer = itemView.findViewById(R.id.ivPhotoUserChat);
                tvHourMessage = itemView.findViewById(R.id.tvTimeMessage);

            }
        }



         @Override
         public int getItemViewType(int position){
            Mensaje mensaje = getItem(position);
            if(mensaje.uid.equals(uid)){
                return 0;
            }

            return 1;
         }

         @NonNull
         public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
             LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
             View view = null;
             switch (viewType) {
                 case 0:
                     view = inflater.inflate(R.layout.message_viewholder2, viewGroup, false);
                     break;
                 case 1:
                     view =  inflater.inflate(R.layout.message_viewholder, viewGroup, false);
                     break;
             }
             return new MessageViewHolder(view);
         }

        @Override
        protected void onBindViewHolder(@NonNull MessageViewHolder holder, final int position, @NonNull final Mensaje mensaje) {
            holder.message.setText(mensaje.message);
            Glide.with(getApplicationContext()).load(mensaje.getPhotoUser()).into(holder.ivphotoBuyer);
            holder.tvHourMessage.setText(DateFormat.format("HH:mm ", mensaje.getCreatedTimestampLong()));

        }
    }
}
