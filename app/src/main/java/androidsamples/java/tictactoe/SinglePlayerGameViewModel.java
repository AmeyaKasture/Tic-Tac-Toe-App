package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;

public class SinglePlayerGameViewModel extends ViewModel {

    private final MutableLiveData<String[][]> mGameBoard = new MutableLiveData<>();
    private final MutableLiveData<String> mDisplayMessage = new MutableLiveData<>();
    private boolean mIsPlayerXTurn = true; // Player starts first
    public   String playerChar = "X";
    public   String botChar = "O";
    private DatabaseReference  userReference=FirebaseDatabase.getInstance("https://tictactoe-4b442-default-rtdb.firebaseio.com/").getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    public boolean game_ended=false;

    public void handleBackPress(
            Context context,
            NavController navController
    ) {
        if (!game_ended) {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.confirm)
                    .setMessage(R.string.forfeit_game_dialog_message)
                    .setPositiveButton(R.string.yes, (d, which) -> {
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
                        navController.popBackStack();
                    })
                    .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                    .create();
            dialog.show();
        } else {
            navController.navigateUp();
        }
    }

    public SinglePlayerGameViewModel(String Mychar, String OtherChar) {
        playerChar=Mychar;
        botChar=OtherChar;
        resetGame();
    }

    public LiveData<String[][]> getGameBoard() {
        return mGameBoard;
    }

    public LiveData<String> getDisplayMessage() {
        return mDisplayMessage;
    }

    public void makeMove(int row, int col) {
        String[][] board = mGameBoard.getValue();

        if (board != null && board[row][col].isEmpty()) {
            board[row][col] = playerChar;
            mGameBoard.setValue(board);

            if (checkWinner(board)) {
                mDisplayMessage.setValue(playerChar + " wins!");
                game_ended=true;
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

                return;
            } else if (isBoardFull(board)) {
                mDisplayMessage.setValue("It's a draw!");
                game_ended=true;
                return;
            }

            // Bot's turn
            doRoboThings();
        }
    }

    public void doRoboThings() {
        String[][] board = mGameBoard.getValue();
        if (board == null) return;

//        // Check for draw before the bot makes its move
//        if (isBoardFull(board)) {
//            mDisplayMessage.setValue("It's a draw!");
//            return;
//        }

        Random rand = new Random();
        int row, col;

        do {
            row = rand.nextInt(3);
            col = rand.nextInt(3);
        } while (!board[row][col].isEmpty());

        board[row][col] = botChar;
        mGameBoard.setValue(board);

        // Check game status after bot's move
        if (checkWinner(board)) {
            mDisplayMessage.setValue(botChar + " wins!");
            game_ended=true;
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

        } else if (isBoardFull(board)) {
            mDisplayMessage.setValue("It's a draw!");
            game_ended=true;
        } else {
            mDisplayMessage.setValue("Player " + playerChar + "'s turn");
        }
    }

    private boolean checkWinner(String[][] board) {
        // Check rows, columns, and diagonals for a winner
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].isEmpty() && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2])) return true;
            if (!board[0][i].isEmpty() && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i])) return true;
        }
        return !board[0][0].isEmpty() && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]) ||
                !board[0][2].isEmpty() && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]);
    }

    private boolean isBoardFull(String[][] board) {
        for (String[] row : board) {
            for (String cell : row) {
                if (cell.isEmpty()) return false;
            }
        }
        return true;
    }

    public void resetGame() {
        mGameBoard.setValue(new String[][]{{"", "", ""}, {"", "", ""}, {"", "", ""}});
        mIsPlayerXTurn = true;
        mDisplayMessage.setValue("Player " + playerChar + "'s turn");
    }
}
