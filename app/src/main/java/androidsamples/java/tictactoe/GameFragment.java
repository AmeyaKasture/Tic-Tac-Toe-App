package androidsamples.java.tictactoe;

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

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import androidsamples.java.tictactoe.models.GameModel;

public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";
  private static final int GRID_SIZE = 9;

  private final Button[] mButtons = new Button[GRID_SIZE];
  private Button quitGame;
  private NavController mNavController;
  private TextView display;

  private boolean isSinglePlayer = true;
  private String myChar = "X";
  private String otherChar = "O";
  public boolean myTurn = true;
  public String[] gameArray = new String[]{"", "", "", "", "", "", "", "", ""};
  private boolean gameEnded = false;
  private GameModel game;
  private boolean isHost = true;
  private DatabaseReference gameReference, userReference;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true); // Needed to display the action menu for this fragment

    // Extract the argument passed with the action in a type-safe way
    GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
    Log.d(TAG, "New game type = " + args.getGameType());
    isSinglePlayer = (args.getGameType().equals("One-Player"));
    String Myicon=args.getCharacter();
    myChar=Myicon;
    if(Myicon.equals("X"))
      otherChar="O";
    else
      otherChar="X";

    userReference = FirebaseDatabase.getInstance("https://tictactoe-4b442-default-rtdb.firebaseio.com/").getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

    if (!isSinglePlayer) {
      gameReference = FirebaseDatabase.getInstance("https://tictactoe-4b442-default-rtdb.firebaseio.com/").getReference("games").child(args.getGameId());
      gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          Log.d(TAG, "onDataChange: ");
          game = snapshot.getValue(GameModel.class);
          assert game != null;
          gameArray = (game.getGameArray()).toArray(new String[9]);
          if (game.getTurn() == 1) {
            if (game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
              isHost = true;
              myTurn = true;
              myChar = "X";
              otherChar = "O";
              display.setText(R.string.your_turn);
            } else {
              isHost = false;
              myTurn = false;
              myChar = "O";
              otherChar = "X";
              display.setText(R.string.waiting);
            }
          }
          else {
            if (!game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
              myTurn = true;
              myChar = "X";
              otherChar = "O";
              isHost = false;
              display.setText(R.string.your_turn);
            } else {
              isHost = true;
              myTurn = false;
              myChar = "O";
              otherChar = "X";
              display.setText(R.string.waiting);
            }
          }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          Log.e("Game setup error", error.getMessage());
        }
      });
    }


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
                    if (!isSinglePlayer) {
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

    if (!isSinglePlayer) {
      boolean check = false;
      for (String s : gameArray) {
        if (!s.isEmpty()) {
          check = true;
          break;
        }
      }
      if (!check) {
        waitForOtherPlayer();
      }
    }
    else {
      display.setText(R.string.your_turn);
    }

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
        if (myTurn){
//          display.setText(R.string.your_turn);
          Log.d(TAG, "Button " + finalI + " clicked");
          ((Button) v).setText(myChar);
          gameArray[finalI] = myChar;
          v.setClickable(false);
//          display.setText(R.string.waiting);
          if (!isSinglePlayer) {
            updateDB();
            myTurn = updateTurn(game.getTurn());
          }
          int win = checkWin();
          if (win == 1 || win == -1) {
            endGame(win);
            return;
          }
          else if (checkDraw()) {
            endGame(0);
            return;
          }
          myTurn = !myTurn;

          if (isSinglePlayer) {
            doRoboThings();
          } else {
            waitForOtherPlayer();
          }
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

  private void endGame(int win) {
    switch (win) {
      case 1:
        display.setText(R.string.you_win);
        if(!gameEnded) {
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
        break;
      case -1:
        display.setText(R.string.you_lose);
        if(!gameEnded) {
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
        break;
      case 0:
        display.setText(R.string.draw);
        break;
      default:
        display.setText(R.string.error);
        Log.i("CHECKING DRAW", "Error: " + win);
        break;
    }
    for (int i = 0; i < 9; i++) {
      mButtons[i].setClickable(false);
    }
    gameEnded = true;
    quitGame.setText(R.string.go_back);

    if(!isSinglePlayer)
      updateDB();
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
    gameReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        Log.d(TAG, "onDataChange: Second time");
        GameModel l = snapshot.getValue(GameModel.class);
        game.updateGameArray(l);
        gameArray = (game.getGameArray()).toArray(new String[9]);
        Log.d(TAG, ""+game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        if(!isEmpty()){
          if (game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            display.setText(R.string.waiting);
          } else {
            display.setText(R.string.your_turn);
          }
        }
        updateUI();
        myTurn = updateTurn(game.getTurn());
//        display.setText(R.string.your_turn);
        int win = checkWin();
        Log.d(TAG, "Winner is "+win);
        if (! (win==0) )endGame(win);
        else if (checkDraw()) endGame(0);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });
  }

  public boolean updateTurn(int turn) {
    return (turn == 1) == isHost;
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
    gameReference.child("gameArray").setValue(Arrays.asList(gameArray));
    gameReference.child("isOpen").setValue(!gameEnded);
    if(!gameEnded){
      if (game.getTurn() == 1) {
        game.setTurn(2);
      } else {
        game.setTurn(1);
      }
      gameReference.child("turn").setValue(game.getTurn());
    }

  }


  private void doRoboThings() {
    Random rand = new Random();
    int x = rand.nextInt(9);
    if (checkDraw()) {
      endGame(0);
      return;
    }
    while (!gameArray[x].isEmpty()) x = rand.nextInt(9);
    Log.i("CHECKING CONDITIONS", "Complete");
    gameArray[x] = otherChar;
    mButtons[x].setText(otherChar);
    mButtons[x].setClickable(false);
    myTurn = !myTurn;
    display.setText(R.string.your_turn);
    int win = checkWin();
    if (win == 1 || win == -1) endGame(win);
    else if (checkDraw()) endGame(0);
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