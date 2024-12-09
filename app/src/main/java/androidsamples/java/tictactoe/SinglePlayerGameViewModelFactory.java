package androidsamples.java.tictactoe;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SinglePlayerGameViewModelFactory implements ViewModelProvider.Factory {
    private final String playerChar;
    private final String botChar;

    public SinglePlayerGameViewModelFactory(String playerChar, String botChar) {
        this.playerChar = playerChar;
        this.botChar = botChar;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SinglePlayerGameViewModel.class)) {
            return (T) new SinglePlayerGameViewModel(playerChar, botChar);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
