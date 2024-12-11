package androidsamples.java.tictactoe;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static com.google.firebase.database.FirebaseDatabase.getInstance;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import androidsamples.java.tictactoe.models.GameModel;

public class ExampleUnitTest {
    private MultiGameFragment gameFragment;


    @Mock
    private FirebaseDatabase mockedFirebaseDatabase;

    @Mock
    private DatabaseReference mockedDatabaseReference;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the behavior of FirebaseDatabase.getInstance()
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
        // Mocking any other behavior you need
        when(mockedFirebaseDatabase.getReference("yourReference")).thenReturn(mockedDatabaseReference);
    }


    @Test
    public void testGameModelInitialization() {
        // Arrange
        String host = "player1";
        String gameId = "123";

        // Act
        GameModel gameModel = new GameModel(host, gameId);

        // Assert
        assertNotNull(gameModel);
        assertEquals(host, gameModel.getHost());
        assertTrue(gameModel.getIsOpen());
        assertEquals(gameId, gameModel.getGameId());
        assertEquals(1, gameModel.getTurn());


        // Assert default game array is initialized
        assertEquals(Arrays.asList("", "", "", "", "", "", "", "", ""), gameModel.getGameArray());
    }

    @Test
    public void testGameModelTurnUpdate() {
        // Arrange
        GameModel gameModel = new GameModel("player1", "123");

        // Act
        gameModel.setTurn(2);

        // Assert
        assertEquals(2, gameModel.getTurn());
    }

    @Test
    public void testGameModelForfeitedUpdate() {
        // Arrange
        GameModel gameModel = new GameModel("player1", "123");

        // Act
        gameModel.setIsOpen(true);

        // Assert
        assertTrue(gameModel.getIsOpen());
    }
    @Test
    public void test4() {
        GameModel gameModel = new GameModel("player1", "123");

        // Your assertions or other test logic
        if(mockedFirebaseDatabase!=null){
            DatabaseReference  gameReference = mockedFirebaseDatabase.getReference("junit_test_games").child("test");
            List<String> gameArray=gameModel.getGameArray();
            gameArray.set(0,"x");
            gameArray.set(2,"O");
            gameArray.set(3,"x");
            gameArray.set(5,"O");

            gameArray.set(7,"x");
            // SET SOME 3 or 4
            gameModel.setGameArray(gameArray);
            //then
            gameReference.setValue(gameModel);
            verify(gameReference, times(1)).setValue(gameModel);



            gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This block will be called when data changes
                    // Retrieve the value from the dataSnapshot
                    GameModel   retrievedGameModel = dataSnapshot.getValue(GameModel.class);
                    assertEquals( gameModel.getGameArray().get(0),"X");
                    assertEquals( gameModel.getGameArray().get(2),"O");
                    assertEquals( gameModel.getGameArray().get(3),"X");
                    assertEquals( gameModel.getGameArray().get(5),"O");
                    assertEquals( gameModel.getGameArray().get(7),"X");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors if necessary
                }
            });

        }


    }





    @Test
    public void testCheckWin() {
        // Set up a winning game state
        MultiGameFragment gameFragment = new MultiGameFragment();
        gameFragment.gameArray = new String[]{"X", "X", "X", "", "", "", "", "", ""};
        // Check that the winning condition is detected correctly
        assertEquals(1, gameFragment.checkWin());
    }

    @Test
    public void testCheckLost() {
        // Set up a winning game state
        MultiGameFragment gameFragment = new MultiGameFragment();
        gameFragment.gameArray = new String[]{"O", "O", "X", "X", "O", "X", "O", "X", "O"};
        // Check that the winning condition is detected correctly

        assertEquals(-1, gameFragment.checkWin());
    }
    @Test
    public void testCheckDraw() {
        // Set up a draw game state
        MultiGameFragment gameFragment = new MultiGameFragment();
        gameFragment.gameArray = new String[]{"X", "O", "X", "X", "O", "O", "O", "X", "X"};
        // Check that the draw condition is detected correctly
        assertEquals(0, gameFragment.checkWin());
    }







}