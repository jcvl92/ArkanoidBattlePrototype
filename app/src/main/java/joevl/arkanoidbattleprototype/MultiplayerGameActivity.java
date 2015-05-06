package joevl.arkanoidbattleprototype;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.internal.api.RealTimeMultiplayerImpl;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import joevl.arkanoidbattleprototype.game_engine.GameEngine;
import joevl.arkanoidbattleprototype.game_modes.MultiplayerVersusGame;
import joevl.arkanoidbattleprototype.game_modes.VersusGame;


public class MultiplayerGameActivity extends GameActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RealTimeMessageReceivedListener, RoomStatusUpdateListener, RoomUpdateListener,
        OnInvitationReceivedListener {

    final static String TAG = "AKBMultiplayer";

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;

    final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;

    private GoogleApiClient mGoogleApiClient;

    private boolean mResolvingConnectionFailure = false;
    private static boolean isClient = true;

    String mRoomId = null;

    String mMyId = null;

    ArrayList<Participant> mParticipants = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        keepScreenOn();
        mGoogleApiClient.connect();
    }

    private void startMultiplayerGame() {
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 1);

        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);

        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfigBuilder.build());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                if (resultCode != Activity.RESULT_OK) {
                    Log.w(TAG, "*** select players UI cancelled, " + resultCode);
                    return;
                }

                Log.d(TAG, "Player Selection was successful");

                final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
                rtmConfigBuilder.addPlayersToInvite(invitees);
                rtmConfigBuilder.setMessageReceivedListener(this);
                rtmConfigBuilder.setRoomStatusUpdateListener(this);
//                switchToScreen(R.id.screen_wait);
                keepScreenOn();
//                resetGameVars();
                Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
                break;
            case RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI.
                if (resultCode == Activity.RESULT_OK) {
                    // ready to start playing
                    Log.d(TAG, "Starting game (waiting room returned OK).");
                    startGame();
                } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that they want to leave the room
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // Dialog was cancelled (user pressed back key, for instance). In our game,
                    // this means leaving the room too. In more elaborate games, this could mean
                    // something else (like minimizing the waiting room UI).
                }
                break;
            case RC_SIGN_IN:
                Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode=" +
                        resultCode + ", intent=" + data);
                if (resultCode == Activity.RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, this, requestCode);
                }
                break;
        }
    }

    void acceptInviteToRoom(String invId) {
        Log.d(TAG, "Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
//        switchToScreen(R.id.screen_wait);
        keepScreenOn();
//        resetGameVars();
        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
    }

    void showWaitingRoom(Room room) {
        Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room,
                MAX_OPPONENTS);
        startActivityForResult(intent, RC_WAITING_ROOM);
    }

    @Override
    public void onConnected(Bundle hint) {
        Log.d(TAG, "Connected");

        if (hint!= null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = hint.getParcelable(Multiplayer.EXTRA_INVITATION);

            if (inv != null && inv.getInvitationId() != null) {
                Log.d(TAG, "onConnected: connection hint has a room invite!");
                acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        } else {
            Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 1);
//        switchToScreen(R.id.screen_wait);
            isClient = false;
            startActivityForResult(intent, RC_SELECT_PLAYERS);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended: " + i);

    }

    void updateRoom(Room room) {
        Log.d(TAG, "Updating Room");
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        if (mParticipants != null) {
//            updatePeerScoresDisplay();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, connectionResult.toString());

        if (connectionResult.hasResolution()) {
            try {
                keepScreenOn();
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    connectionResult.getErrorCode(), this, 1);
            errorDialog.show();
        }

    }

    @Override
    public void onInvitationReceived(Invitation invitation) {

    }

    @Override
    public void onInvitationRemoved(String s) {

    }

    @Override
    public void onRoomConnecting(Room room) {

    }

    @Override
    public void onRoomAutoMatching(Room room) {

    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> strings) {

    }

    @Override
    public void onPeerDeclined(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onPeerJoined(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onConnectedToRoom(Room room) {
        updateRoom(room);
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        updateRoom(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> strings) {
        updateRoom(room);
    }

    @Override
    public void onP2PConnected(String s) {

    }

    @Override
    public void onP2PDisconnected(String s) {

    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
    }

    @Override
    public void onLeftRoom(int i, String s) {

    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            return;
        }
        updateRoom(room);
    }

    void startGame() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        gameView.gameEngine.paused = false;
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            /*AsyncTask syncLoop = new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object[] params) {*/
                                    synchronizeState();
                                    /*return null;
                                }
                            };*/

                            //syncLoop.execute();

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 100); //execute in every 50000 ms
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] receivedState = realTimeMessage.getMessageData();
        if(isClient)
            gameView.gameEngine.setSerializedState(receivedState);
        else
            ;//TODO: set the paddle position
    }

    void synchronizeState() {
        //TODO: if client do something different
        byte[] state = gameView.gameEngine.getSerializedState();
        for (Participant p : mParticipants) {
            if (p.getParticipantId().equals(mMyId)) {
                continue;
            }
            if (p.getStatus() != Participant.STATUS_JOINED) {
                continue;
            }
            Games.RealTimeMultiplayer.sendUnreliableMessage(mGoogleApiClient, state, mRoomId,
                    p.getParticipantId());
        }
    }

    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected GameEngine gameModeFactory() {
        return new MultiplayerVersusGame(gameView, (isClient ? 1 : 2));
    }

    @Override
    protected void pause() {}

    @Override
    public void onGameOver(String mode, String status, String score) {
        if(!destroyed) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
            super.onGameOver(mode, status, score);
        }
    }
}

