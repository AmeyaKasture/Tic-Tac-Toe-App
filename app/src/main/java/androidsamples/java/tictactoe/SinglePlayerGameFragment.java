package androidsamples.java.tictactoe;


import android.app.AlertDialog;
import android.graphics.Color;
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
import androidx.lifecycle.ViewModelProvider;
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

public class SinglePlayerGameFragment extends Fragment {

    private SinglePlayerGameViewModel mViewModel;
    private TextView mDisplayTv;
    private Button[][] mButtons = new Button[3][3];
    private Button mBackBtn;
    private String myChar = "X";
    private String otherChar = "O";
    private  final String TAG="SingleGameFragment";
    private NavController mNavController;

    public SinglePlayerGameFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Initialize ViewModel
        MultiGameFragmentArgs args = MultiGameFragmentArgs.fromBundle(getArguments());

        String Myicon=args.getCharacter();
        myChar=Myicon;
        if(Myicon.equals("X"))
            otherChar="O";
        else
            otherChar="X";

        SinglePlayerGameViewModelFactory factory = new SinglePlayerGameViewModelFactory(myChar, otherChar);
        mViewModel = new ViewModelProvider(this, factory).get(SinglePlayerGameViewModel.class);

        // Initialize UI components
        mDisplayTv = view.findViewById(R.id.display_tv);
        mBackBtn = view.findViewById(R.id.back_btn);

        mNavController = Navigation.findNavController(view);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                mViewModel.handleBackPress(
                        requireContext(),
                        mNavController
                );
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Initialize the buttons
        int[][] buttonIds = {
                {R.id.button0, R.id.button1, R.id.button2},
                {R.id.button3, R.id.button4, R.id.button5},
                {R.id.button6, R.id.button7, R.id.button8}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mButtons[i][j] = view.findViewById(buttonIds[i][j]);
                final int row = i, col = j;
                mButtons[i][j].setOnClickListener(v -> onCellClicked(row, col));
            }
        }

        // Observe LiveData for the game board
        mViewModel.getGameBoard().observe(getViewLifecycleOwner(), gameBoard -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mButtons[i][j].setText(gameBoard[i][j]);
                }
            }
        });

        // Observe LiveData for the display message
        mViewModel.getDisplayMessage().observe(getViewLifecycleOwner(), message -> mDisplayTv.setText(message));

        // Handle back button
        mBackBtn.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void onCellClicked(int row, int col) {
        mViewModel.makeMove(row, col);
        mButtons[row][col].setClickable(false);//// Disable the button after a move
        if(mViewModel.game_ended){
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    mButtons[i][j].setClickable(false);

                }
            }

        }
    }


}
