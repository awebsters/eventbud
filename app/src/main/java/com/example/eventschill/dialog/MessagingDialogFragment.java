package com.example.eventschill.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventschill.R;
import com.example.eventschill.data.model.LoggedInUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.StatusListener;

import java.util.ArrayList;
import java.util.List;

public class MessagingDialogFragment extends DialogFragment {

    final static String SERVER_TOKEN_URL = "https://amber-tortoise-3503.twil.io/chat-token";
    final static String TAG = "TwilioChat";

    LoggedInUser.User user;

    private RecyclerView mMessagesRecyclerView;
    private MessagesAdapter mMessagesAdapter;
    private ArrayList<Message> mMessages = new ArrayList<>();
    private EditText mWriteMessageEditText;
    private Button mSendChatMessageButton;

    private ChatClient mChatClient;
    private Channel mGeneralChannel;

    private String channel;
    public Handler handler;

    public static MessagingDialogFragment newInstance(LoggedInUser.User user) {
        MessagingDialogFragment fragment = new MessagingDialogFragment();
        Gson gson = new Gson();
        Bundle args = new Bundle();
        args.putString("user", gson.toJson(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    private CallbackListener<ChatClient> mChatClientCallback =
            new CallbackListener<ChatClient>() {
                @Override
                public void onSuccess(ChatClient chatClient) {
                    mChatClient = chatClient;
                    loadChannels();
                    Log.d(TAG, "Success creating Twilio Chat Client");
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    Log.e(TAG,"Error creating Twilio Chat Client: " + errorInfo.getMessage());
                }
            };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_user_view, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        Gson gson = new Gson();
        user = gson.fromJson(getArguments().getString("user"), LoggedInUser.User.class);

        ImageView photo = view.findViewById(R.id.photo);
        photo.setImageBitmap(user.getPhoto());

        TextView name = view.findViewById(R.id.name);
        name.setText(user.getFullName());

        TextView username = view.findViewById(R.id.time);
        username.setText("@" + user.getUsername());

        mMessagesRecyclerView = (RecyclerView) view.findViewById(R.id.messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setStackFromEnd(true);
        mMessagesRecyclerView.setLayoutManager(layoutManager);

        mMessagesAdapter = new MessagesAdapter();
        mMessagesRecyclerView.setAdapter(mMessagesAdapter);

        mWriteMessageEditText = (EditText) view.findViewById(R.id.message);
        mSendChatMessageButton = (Button) view.findViewById(R.id.sendMessage);

        handler = new Handler();
        mSendChatMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGeneralChannel != null) {
                    String messageBody = mWriteMessageEditText.getText().toString();
                    Message.Options options = Message.options().withBody(messageBody);

                    mGeneralChannel.getMessages().sendMessage(options, new CallbackListener<Message>() {
                        @Override
                        public void onSuccess(Message message) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // need to modify user interface elements on the UI thread
                                    mWriteMessageEditText.setText("");
                                }
                            });
                        }
                    });
                }
            }
        });

        ImageView closeButton = (ImageView)view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        String channelPos1 = LoggedInUser.getUser().getUsername() + "_" + user.getUsername();
        String channelPos2 = user.getUsername() + "_" + LoggedInUser.getUser().getUsername();
        if (LoggedInUser.getUser().channels.containsKey(channelPos1)) {
            channel = channelPos1;
        } else if (LoggedInUser.getUser().channels.containsKey(channelPos2)){
            channel = channelPos2;
        } else {
            channel = null;
        }
        retrieveAccessTokenfromServer(LoggedInUser.getUser().getUsername());
    }

    private void retrieveAccessTokenfromServer(String identity) {
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        String tokenURL = SERVER_TOKEN_URL + "?device=" + deviceId + "&identity=" + identity;

        Ion.with(this)
                .load(tokenURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            String accessToken = result.get("token").getAsString();

                            ChatClient.Properties.Builder builder = new ChatClient.Properties.Builder();
                            ChatClient.Properties props = builder.createProperties();
                            ChatClient.create(MessagingDialogFragment.this.getContext(), accessToken, props, mChatClientCallback);

                        } else {
                            Toast.makeText(MessagingDialogFragment.this.getContext(),
                                    R.string.error_retrieving_access_token, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private void loadChannels() {

        if (channel == null){
            mChatClient.getChannels().channelBuilder()
                    .withUniqueName(LoggedInUser.getUser().getUsername() + "_"+ user.getUsername())
                    .withType(Channel.ChannelType.PUBLIC)
                    .build(new CallbackListener<Channel>() {
                        @Override
                        public void onSuccess(Channel channel) {
                            if (channel != null) {
                                Log.d(TAG,"Success creating channel");
                                joinChannel(channel);
                            }
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            Log.e(TAG,"Error creating channel: " + errorInfo.getMessage());
                            getDialog().dismiss();
                        }
                    });
        } else{
            mChatClient.getChannels().getChannel(channel, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(Channel channel) {
                    if (channel != null) {
                        Log.d(TAG, "Joining Channel: " + channel);
                        joinChannel(channel);
                    } else {
                        Log.d(TAG, "Failed to join Channel: " + channel);
                        getDialog().dismiss();
                    }
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    getDialog().dismiss();
                }
            });
        }
    }

    private void joinChannel(final Channel channel) {
        Log.d(TAG, "Joining Channel: " + channel.getUniqueName());

        channel.join(new StatusListener() {
            @Override
            public void onSuccess() {
                mGeneralChannel = channel;
                Log.d(TAG, "Joined defaultimg channel");
                mGeneralChannel.addListener(mDefaultChannelListener);
                LoggedInUser.getUser().addChannel(channel.getUniqueName());
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Log.e(TAG,"Error joining channel: " + errorInfo.getMessage());
                mGeneralChannel = channel;
                mGeneralChannel.addListener(mDefaultChannelListener);

            }
        });
    }

    private ChannelListener mDefaultChannelListener = new ChannelListener() {


        @Override
        public void onMessageAdded(final Message message) {
            Log.d(TAG, "Message added");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // need to modify user interface elements on the UI thread
                    mMessages.add(message);
                    mMessagesAdapter.notifyDataSetChanged();
                    mMessagesRecyclerView.scrollToPosition(mMessages.size() - 1);
                }
            });
        }

        @Override
        public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {
            Log.d(TAG, "Message updated: " + message.getMessageBody());
        }

        @Override
        public void onMessageDeleted(Message message) {
            Log.d(TAG, "Message deleted");
        }

        @Override
        public void onMemberAdded(Member member) {
            Log.d(TAG, "Member added: " + member.getIdentity());
        }

        @Override
        public void onMemberUpdated(Member member, Member.UpdateReason updateReason) {
            Log.d(TAG, "Member updated: " + member.getIdentity());
        }

        @Override
        public void onMemberDeleted(Member member) {
            Log.d(TAG, "Member deleted: " + member.getIdentity());
        }

        @Override
        public void onTypingStarted(Channel channel, Member member) {
            Log.d(TAG, "Started Typing: " + member.getIdentity());
        }

        @Override
        public void onTypingEnded(Channel channel, Member member) {
            Log.d(TAG, "Ended Typing: " + member.getIdentity());
        }

        @Override
        public void onSynchronizationChanged(Channel channel) {
            if (channel.getSynchronizationStatus() == Channel.SynchronizationStatus.ALL){
                channel.getMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        channel.getMessages().getLastMessages(aLong.intValue(), new CallbackListener<List<Message>>() {
                            @Override
                            public void onSuccess(List<Message> messages) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMessages.addAll(messages);
                                        mMessagesAdapter.notifyDataSetChanged();
                                        mMessagesRecyclerView.scrollToPosition(mMessages.size() - 1);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {

            public TextView yourText;
            public TextView otherText;

            public ViewHolder(View itemView) {
                super(itemView);
                yourText = itemView.findViewById(R.id.yourText);
                otherText = itemView.findViewById(R.id.otherText);
            }
        }

        public MessagesAdapter() {

        }

        @Override
        public MessagesAdapter
                .ViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View messageTextView = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_text_view, parent, false);
            return new ViewHolder(messageTextView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message message = mMessages.get(position);

            if (message.getAuthor().equals(LoggedInUser.getUser().getUsername())){
                holder.otherText.setVisibility(View.GONE);
                holder.yourText.setVisibility(View.VISIBLE);
                holder.yourText.setText(message.getMessageBody());
            } else {
                holder.yourText.setVisibility(View.GONE);
                holder.otherText.setVisibility(View.VISIBLE);
                holder.otherText.setText(message.getMessageBody());
            }
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }
    }
}
