package jhhmoviles.com.tictactoe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToeActivity extends AppCompatActivity {
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mBoardButtons[];
    private int scoreH = 0;
    private int scoreA = 0;
    private int scoreT = 0;
    // Various text displayed
    private TextView mInfoTextView;
    private TextView stats;
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;
    private BoardView mBoardView;
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    private SharedPreferences mPrefs;

    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound1);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound2);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        // Restore the scores
        scoreH = mPrefs.getInt("scoreH", 0);
        scoreA = mPrefs.getInt("scoreA", 0);
        scoreT = mPrefs.getInt("scoreT", 0);


        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        mInfoTextView = (TextView) findViewById(R.id.information);
        stats = (TextView) findViewById(R.id.score);

        if(savedInstanceState  == null) {
            startNewGame();
        } else{
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGame.endGame = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            scoreH = savedInstanceState.getInt("scoreH");
            scoreA = savedInstanceState.getInt("scoreA");
            scoreT = savedInstanceState.getInt("scoreT");
            //mGame. = savedInstanceState.getChar("mGoFirst");
        }
        displayScores();
    }
    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("scoreH", scoreH);
        ed.putInt("scoreA", scoreA);
        ed.putInt("scoreT", scoreT);
        ed.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGame.endGame);
        outState.putInt("scoreH", Integer.valueOf(scoreH));
        outState.putInt("scoreA", Integer.valueOf(scoreA));
        outState.putInt("scoreT", Integer.valueOf(scoreT));
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", 'X');
    }

    private void displayScores() {
        stats.setText("Human: "+ scoreH +"   Ties: "+scoreT+"  Android: "+ scoreA);
    }

    public boolean onCreateOptionsMenuOld(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("New Game");
        return true;
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGame.endGame = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        scoreH = savedInstanceState.getInt("scoreH");
        scoreA = savedInstanceState.getInt("scoreA");
        scoreT = savedInstanceState.getInt("scoreT");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelectedOld(MenuItem item) {
        startNewGame();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.reset:
                scoreH = 0;
                scoreT = 0;
                scoreA = 0;
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT_ID);
                return true;
        }
        return false;
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        int selected;
        switch(id) {
            case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                selected = mGame.getDifficultyLevel().ordinal();
                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                // selected is the radio button that should be selected.

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();   // Close dialog
                                switch (item) {
                                    case 0:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                        startNewGame();
                                        break;
                                    case 1:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                        startNewGame();
                                        break;
                                    case 2:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                        startNewGame();
                                        break;
                                    // TODO: Set the diff level of mGame based on which item was selected.
                                }

                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                dialog = builder.create();

                break;
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;
            case DIALOG_ABOUT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.about)
                        .setCancelable(false)
                        .setPositiveButton(R.string.thaks, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();   // Close dialog
                            }
                        });
                dialog = builder.create();

                break;


        }

        return dialog;
    }



    // Set up the game board.
    private void startNewGame() {
       mGame.clearBoard();
        mGame.endGame = false;
        // Reset all buttons

        mBoardView.invalidate();   // Redraw the board

        // Human goes first
        mInfoTextView.setText(R.string.first_human);


    }
    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGame.endGame && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        //handle
                    }
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    mComputerMediaPlayer.start();    // Play the sound effect

                    winner = mGame.checkForWinner();
                }

                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    scoreT++;
                    mGame.endGame=true;
                    stats.setText("Human: "+ scoreH +"   Ties: "+scoreT+"  Android: "+ scoreA);
                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    scoreH++;
                    mGame.endGame=true;
                    stats.setText("Human: "+ scoreH +"   Ties: "+scoreT+"  Android: "+ scoreA);
                }
                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    scoreA++;
                    mGame.endGame=true;
                    stats.setText("Human: "+ scoreH +"   Ties: "+scoreT+"  Android: "+ scoreA);
                }
            }

// So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private boolean  setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate();   // Redraw the board
            mHumanMediaPlayer.start();    // Play the sound effect

            return true;
        }
        return false;

        /*mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));*/
    }


    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if(!mGame.endGame){
            if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    mComputerMediaPlayer.start();    // Play the sound effect
                    winner = mGame.checkForWinner();
                }

                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    scoreT++;
                    mGame.endGame=true;
                    stats.setText("Human: "+ scoreH +"   Ties: "+scoreT+"  Android: "+ scoreA);
                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    scoreH++;
                    mGame.endGame=true;
                    stats.setText("Human: "+ scoreH +"   Ties: "+scoreT+"  Android: "+ scoreA);
                }
                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    scoreA++;
                    mGame.endGame=true;
                    stats.setText("Human: "+ scoreH +"   Ties: "+scoreT+"  Android: "+ scoreA);
                }
            }}
        }

    }
}