package androidsamples.java.tictactoe;

import static java.lang.Thread.sleep;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.ClosedFileSystemException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import androidsamples.java.tictactoe.models.GameModel;

public class MultiGameFragment extends Fragment {
    private static final String TAG = "GameFragmentMulti";
    private static final int GRID_SIZE = 9;

    private final Button[] mButtons = new Button[GRID_SIZE];
    private Button quitGame;
    private NavController mNavController;
    private TextView display;

    private boolean isSinglePlayer = true;
    private String myChar = "X";
    private String otherChar = "O";
    public boolean Mychance = true;
    public String[] gameArray = new String[]{"", "", "", "", "", "", "", "", ""};
    private boolean  gameEnded;
    private GameModel game;
    private boolean isHost = true;
    private DatabaseReference gameReference, userReference;
    private int my_turn=1;
    private int opp_turn;
    private int current_turn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Needed to display the action menu for this fragment

        // Extract the argument passed with the action in a type-safe way
        MultiGameFragmentArgs args = MultiGameFragmentArgs.fromBundle(getArguments());
        Log.d(TAG, "New game type = " + args.getGameType());
        isSinglePlayer = (args.getGameType().equals("One-Player"));
        String Myicon=args.getCharacter();
        myChar=Myicon;
        if(Myicon.equals("X"))
            otherChar="O";
        else
            otherChar="X";

        userReference = FirebaseDatabase.getInstance("https://tictactoe-4b442-default-rtdb.firebaseio.com/").getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());


        gameReference = FirebaseDatabase.getInstance("https://tictactoe-4b442-default-rtdb.firebaseio.com/").getReference("games").child(args.getGameId());
        gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: ");
                game = snapshot.getValue(GameModel.class);
                assert game != null;
                gameArray = (game.getGameArray()).toArray(new String[9]);
                if(!(game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                    gameReference.child("isOpen").setValue(false);

                }
                gameEnded=!(game.winner==0);
                isHost=(game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()));
                if(isHost){
                    my_turn=1;
                    opp_turn=2;
                }
                else {
                    my_turn = 2;
                    opp_turn=1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Game setup error", error.getMessage());
            }
        });
//



        // Handle the back press by adding a confirmation dialog
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                if (!gameEnded) {
                    AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.confirm)
                            .setMessage(R.string.forfeit_game_dialog_message)
                            .setPositiveButton(R.string.yes, (d, which) -> {
                                game.setTurn(opp_turn);
                                game.winner=opp_turn;
                                game.setIsOpen(false);
                                Log.d(TAG, "handleOnBackPressed: "+game.getIsOpen());
                                gameReference.setValue(game);
                                gameEnded=true;
                                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int value = Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
                                        value = value + 1;
                                        dataSnapshot.getRef().child("lost").setValue(value);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                mNavController.popBackStack();
                            })
                            .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                            .create();
                    dialog.show();
                } else {
                    assert getParentFragment() != null;
                    NavHostFragment.findNavController(getParentFragment()).navigateUp();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        display = view.findViewById(R.id.display_tv);
        quitGame = view.findViewById(R.id.back_btn);
        quitGame.setOnClickListener(v -> getActivity().onBackPressed());
        boolean check = false;
        gameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: Second time");
                GameModel l = snapshot.getValue(GameModel.class);
                game.updateGameArray(l);
                gameArray = (game.getGameArray()).toArray(new String[9]);
                Log.d(TAG, "Winner is "+game.winner);
                updateUI();
                if(game.winner!=0){

                    endGame(game.winner,my_turn,opp_turn,3);
                }
                else{
                    current_turn = game.getTurn();
                    Mychance = game.getTurn() == my_turn;
                    Log.d(TAG, "" + my_turn + "  :  " + game.getTurn());
                    if (!Mychance) {
                        display.setText(R.string.waiting);
                    } else {
                        display.setText(R.string.your_turn);
                    }


            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mNavController = Navigation.findNavController(view);

        mButtons[0] = view.findViewById(R.id.button0);
        mButtons[1] = view.findViewById(R.id.button1);
        mButtons[2] = view.findViewById(R.id.button2);

        mButtons[3] = view.findViewById(R.id.button3);
        mButtons[4] = view.findViewById(R.id.button4);
        mButtons[5] = view.findViewById(R.id.button5);

        mButtons[6] = view.findViewById(R.id.button6);
        mButtons[7] = view.findViewById(R.id.button7);
        mButtons[8] = view.findViewById(R.id.button8);

        for (int i = 0; i < mButtons.length; i++) {
            int finalI = i;
            mButtons[i].setOnClickListener(v -> {
                if (Mychance){

                    Log.d(TAG, "Button " + finalI + " clicked");
                    ((Button) v).setText(myChar);
                    gameArray[finalI] = myChar;
                    v.setClickable(false);
                    int win = checkWin();
                    current_turn=updateTurn(current_turn);
                    game.setTurn(current_turn);
                    game.setGameArray(Arrays.asList(gameArray));
                    if(win==1){
                        endGame(my_turn,my_turn,opp_turn,3);
                        return;
                    }
                    else if (checkDraw()) {
                        endGame(3, my_turn, opp_turn, 3);
                        return;
                    }
                    updateDB();
//                    waitForOtherPlayer();

                }
                else {
                    Toast.makeText(getContext(), "Please wait for your turn!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean checkDraw() {
        if (checkWin() != 0) return false;
        Log.i("CHECKING WIN IN DRAW", "Complete: " + checkWin());
        for (int i = 0; i < 9; i++) {
            if (gameArray[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void endGame(int win,int my_turn,int opp_turn,int draw) {
            int init_winner= game.winner;
            if(win == my_turn) {
                display.setText(R.string.you_win);
                game.winner=my_turn;
                if (!gameEnded) {
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int value = Integer.parseInt(dataSnapshot.child("won").getValue().toString());
                            value = value + 1;
                            dataSnapshot.getRef().child("won").setValue(value);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            else if(win==opp_turn) {
                display.setText(R.string.you_lose);
                game.winner=opp_turn;
                if (!gameEnded) {
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int value = Integer.parseInt(dataSnapshot.child("lost").getValue().toString());
                            value = value + 1;
                            dataSnapshot.getRef().child("lost").setValue(value);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            else {
                display.setText(R.string.draw);
                game.winner=3;
            }
        for (int i = 0; i < 9; i++) {
            mButtons[i].setClickable(false);
        }
        gameEnded = true;
        quitGame.setText(R.string.go_back);
        if(init_winner==0){
            updateDB();
        }

    }

    private boolean isEmpty() {
        for (String cell : gameArray) {
            if (!cell.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void waitForOtherPlayer() {
        Log.d(TAG, "waitForOtherPlayer: ");
        display.setText(R.string.waiting);

    }

    public int updateTurn(int turn) {
        if(turn==1)return 2;
        return 1;
    }

    private void updateUI() {
        for (int i = 0; i < 9; i++) {
            String v = gameArray[i];
            if (!v.isEmpty()) {
                mButtons[i].setText(v);
                mButtons[i].setClickable(false);
            }
        }
    }

    private void updateDB() {
        gameReference.setValue(game);

    }



    public int checkWin() {
        String winChar = "";
        if  (gameArray[0].equals(gameArray[1]) && gameArray[1].equals(gameArray[2]) && !gameArray[0].isEmpty()) winChar = gameArray[0];
        else if (gameArray[3].equals(gameArray[4]) && gameArray[4].equals(gameArray[5]) && !gameArray[3].isEmpty()) winChar = gameArray[3];
        else if (gameArray[6].equals(gameArray[7]) && gameArray[7].equals(gameArray[8]) && !gameArray[6].isEmpty()) winChar = gameArray[6];
        else if (gameArray[0].equals(gameArray[3]) && gameArray[3].equals(gameArray[6]) && !gameArray[0].isEmpty()) winChar = gameArray[0];
        else if (gameArray[4].equals(gameArray[1]) && gameArray[1].equals(gameArray[7]) && !gameArray[1].isEmpty()) winChar = gameArray[1];
        else if (gameArray[2].equals(gameArray[5]) && gameArray[5].equals(gameArray[8]) && !gameArray[2].isEmpty()) winChar = gameArray[2];
        else if (gameArray[0].equals(gameArray[4]) && gameArray[4].equals(gameArray[8]) && !gameArray[0].isEmpty()) winChar = gameArray[0];
        else if (gameArray[6].equals(gameArray[4]) && gameArray[4].equals(gameArray[2]) && !gameArray[2].isEmpty()) winChar = gameArray[2];
        else return 0;

        return (winChar.equals(myChar)) ? 1 : -1;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_logout, menu);
        // this action menu is handled in MainActivity
    }


}