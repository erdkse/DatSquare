package com.erdkse.apps.datsquare;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends Activity {

    private RelativeLayout panel_rl;
    private RelativeLayout game_panel;
    private ImageView iv_btn_go;
    private UtilFunctions utilFunctions;
    private ArrayList<ImageView> imageViews;
    private Boolean onPlay = false;
    private Boolean onHeart = false;
    //private Button ok_to_go;
    private int click_count = 0;
    private int leftHearts = 1;
    private TextView tv_time;
    private TextView tv_left_guess;
    private int wait_time = 1000;
    private int game_time = 8000;
    private SoundPool sp;
    private int soundIds[] = new int[3];

    private int[] bases;
    private TextView tv_stage;
    private TimeCounter timeCounter;
    private PanelCounter panelCounter;

    private float width;

    private int stage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundIds[0] = sp.load(getApplicationContext(), R.raw.coin_1, 1);
        soundIds[1] = sp.load(getApplicationContext(), R.raw.girl_screem, 1);
        soundIds[2] = sp.load(getApplicationContext(), R.raw.success_1, 1);

        InitUI();

        iv_btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (onPlay == false) {
                    setGame(stage, width);
                    onPlay = true;
                    startGame(imageViews);
                    tv_left_guess.setText(getResources().getString(R.string.left) + bases[2]);
                    //ok_to_go.setText(getResources().getString(R.string.reload));
                    if (leftHearts > 0) {
                        onHeart = true;
                        iv_btn_go.setBackgroundResource(R.drawable.heart95);
                        leftHearts--;
                    } else {
                        onHeart = false;
                        iv_btn_go.setBackgroundResource(R.drawable.heart95);
                    }

                } else {
                    onPlay = false;
                    tv_left_guess.setText(getResources().getString(R.string.left) + " -");

                    setGame(stage, width);
                    //ok_to_go.setText(getResources().getString(R.string.start_Level) + " " + stage);
                    if (onHeart) {
                        timeCounter.cancel();
                        panelCounter.cancel();
                        iv_btn_go.setBackgroundResource(R.drawable.play);
                    } else {
                        iv_btn_go.setBackgroundResource(R.drawable.play);
                    }

                    setBlankImg(imageViews);
                }

            }
        });


    }

    private void InitUI() {

        utilFunctions = new UtilFunctions(MainActivity.this);
        panel_rl = (RelativeLayout) findViewById(R.id.panel_rl);
        game_panel = (RelativeLayout) findViewById(R.id.game_panel);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_left_guess = (TextView) findViewById(R.id.tv_left_guess);
        //ok_to_go = (Button) findViewById(R.id.ok_to_go_btn);
        iv_btn_go = (ImageView) findViewById(R.id.iv_btn_go);
        tv_stage = (TextView) findViewById(R.id.tv_stage);
        imageViews = new ArrayList<ImageView>();
        panelCounter = new PanelCounter(wait_time, 1000);
        timeCounter = new TimeCounter(game_time, 10);

        width = utilFunctions.getScreenWidth(MainActivity.this);
        iv_btn_go.setBackgroundResource(R.drawable.play);

        setGame(stage, width);

        // Get tracker.
        Tracker t = ((MyApplication) getApplication()).getTracker(
                MyApplication.TrackerName.APP_TRACKER);

        // Enable Advertising Features.
        t.enableAdvertisingIdCollection(true);

        // Set screen name.
        t.setScreenName("Main");

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());

        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private void setGame(int stage, float width) {
        imageViews.clear();
        panel_rl.removeAllViews();
        tv_time.setText(game_time / 1000 + " " + getResources().getText(R.string.seconds_Left));
        tv_time.setTextColor(getResources().getColor(R.color.black));
        tv_stage.setText(String.valueOf(stage));
        tv_left_guess.setText(getResources().getString(R.string.left) + " -");

        //final float width = utilFunctions.getScreenWidth(MainActivity.this);

        bases = getResources().getIntArray(getResources().
                getIdentifier("level_" + String.valueOf(stage), "array", MainActivity.this.getPackageName()));

        game_panel.setMinimumWidth((int) width);
        game_panel.setMinimumHeight((int) width);


        Dagit(bases[1], bases[0], width, panel_rl);

    }

    private void showRandomImg(ArrayList<ImageView> iv, ArrayList<Integer> random) {
        for (int i = 0; i < iv.size(); i++) {

            if (random.contains(i)) {
                iv.get(i).setImageResource(R.color.black);
            }
        }
    }

    private void startGame(final ArrayList<ImageView> iv)

    {
        panelCounter.start();
        timeCounter.start();
        click_count = 0;
        final ArrayList<Integer> randomNumbers = generateRandomNumber(0, bases[0], bases[2]);
        showRandomImg(imageViews, randomNumbers);

        final ArrayList<Integer> clicked_btns = new ArrayList<Integer>();

        for (int i = 0; i < iv.size(); i++) {

            final int temp_i = i;
            iv.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click_count++;

                    if (randomNumbers.contains(temp_i)) {
                        iv.get(temp_i).setImageResource(R.color.green);
                        iv.get(temp_i).setClickable(false);
                        sp.play(soundIds[0], 1, 1, 1, 0, (float) 1.0);
                        clicked_btns.add(temp_i);
                        int a = bases[2] - click_count;
                        tv_left_guess.setText(getResources().getString(R.string.left) + " " + a);

                        if (click_count == 1) {
                            panelCounter.cancel();
                            for (int i = 0; i < iv.size(); i++) {
                                iv.get(i).setImageResource(R.color.blue);
                                iv.get(i).setClickable(true);
                            }
                            iv.get(temp_i).setImageResource(R.color.green);
                            iv.get(temp_i).setClickable(false);
                        }
                        //Success Situation
                        else if (click_count == randomNumbers.size()) {
                            timeCounter.cancel();
                            tv_time.setText(R.string.done);
                            tv_time.setTextColor(getResources().getColor(R.color.green));

                            stage++;
                            if (stage > 32) {
                                stage = 32;
                            }
                            //ok_to_go.setText(R.string.go_To_Next_Level);
                            iv_btn_go.setBackgroundResource(R.drawable.up);

                            sp.play(soundIds[2], 1, 1, 1, 0, (float) 1.0);

                            stopGame(iv);
                        }
                    } else {
                        // Fail Situation
                        panelCounter.cancel();
                        timeCounter.cancel();
                        iv.get(temp_i).setImageResource(R.color.red);
                        iv.get(temp_i).setClickable(false);
                        sp.play(soundIds[1], 1, 1, 1, 0, (float) 1.0);
                        tv_time.setText(R.string.ups);
                        tv_time.setTextColor(getResources().getColor(R.color.red));

                        stopGame(iv);
                        stage = stage - 3;
                        if (stage < 1) {
                            stage = 1;
                        }
                        //ok_to_go.setText(getResources().getString(R.string.go_To_Level) + " " + stage);
                        iv_btn_go.setBackgroundResource(R.drawable.down);

                        for (int j = 0; j < randomNumbers.size(); j++) {
                            if (!clicked_btns.contains(randomNumbers.get(j))) {
                                iv.get(randomNumbers.get(j)).setImageResource(R.color.gray);
                            }
                        }
                    }
                }
            });
        }
    }

    private void stopGame(final ArrayList<ImageView> iv) {
        for (int i = 0; i < iv.size(); i++) {
            iv.get(i).setClickable(false);
        }
        onHeart = false;
        //onPlay=false;
    }

    private void setBlankImg(ArrayList<ImageView> iv) {
        for (int i = 0; i < iv.size(); i++) {
            iv.get(i).setImageResource(R.color.blue);
        }
    }

    private ArrayList generateRandomNumber(int min, int max, int size) {
        int rnd;
        int maxi = max - 1;
        Random rand = new Random();
        int[] randNo = new int[size];
        ArrayList numbers = new ArrayList();
        for (int k = 0; k < size; k++) {
            rnd = rand.nextInt((maxi - min) + 1) + min;
            if (k == 0) {
                randNo[0] = rnd;
                numbers.add(randNo[0]);
            } else {
                while (numbers.contains(new Integer(rnd))) {
                    rnd = rand.nextInt((maxi - min) + 1) + min;
                }
                randNo[k] = rnd;
                numbers.add(randNo[k]);
            }
        }

        return numbers;
    }

    public void Dagit(int row, int total, float screen_width, RelativeLayout parentRL) {

        int cell = (int) screen_width / row;
        int padding = (int) cell / 17;
        int size = (int) cell - padding;

        int rows = total / row; // the number of rows that results from limit
        int leftOver = total % row; // see if we have incomplete rows
        if (leftOver != 0) {
            rows += 1;
        }
        int id = 1000; // the ids of the ImageViews 1000, 1001, 1002 etc
        int position = 0;
        int belowId = 40; // this id will be used to position the ImageView on another row
        while (rows > 0) {
            int realItemsPerRow = row;
            if (leftOver != 0 & rows == 1) {
                realItemsPerRow = Math.min(row, leftOver);
            }
            for (int i = 0; i < realItemsPerRow; i++) {
                final ImageView imageView = new ImageView(this);
                imageViews.add(position, imageView);
                imageView.setId(id);
                imageView.setImageResource(R.color.blue);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(size, size);
                //imageView.setPadding(padding, padding, 0, 0);
                lp.setMargins(padding, padding, 0, 0);
                imageView.setAdjustViewBounds(true);
                imageView.setMaxHeight(size);
                imageView.setMaxWidth(size);
                parentRL.setPadding(0, 0, padding, 0);

                if (i == 0) {
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                } else {
                    lp.addRule(RelativeLayout.RIGHT_OF, imageView.getId() - 1);
                }
                lp.addRule(RelativeLayout.BELOW, belowId);
                imageView.setLayoutParams(lp);
                parentRL.addView(imageView);
                id++;
                position++;
            }
            belowId = id - 1;
            rows--;
        }
    }

    public class TimeCounter extends CountDownTimer {

        public TimeCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            tv_time.setText(R.string.timeUP);   //TextView object should be defined in onCreate
            tv_time.setTextColor(getResources().getColor(R.color.red));
            sp.play(soundIds[1], 1, 1, 1, 0, (float) 1.0);
            stopGame(imageViews);
            onPlay = false;
            stage = stage - 3;
            if (stage < 1) {
                stage = 1;
            }
            //ok_to_go.setText(getResources().getString(R.string.go_To_Level) + " " + stage);
            iv_btn_go.setBackgroundResource(R.drawable.down);

        }

        @Override
        public void onTick(long millisUntilFinished) {

            tv_time.setText(millisUntilFinished / 1000
                    + "," + (millisUntilFinished % 1000) / 10 + " " +
                    getResources().getString(R.string.seconds_Left));// This will be called every Second.

        }
    }

    public class PanelCounter extends CountDownTimer {

        public PanelCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

            for (int i = 0; i < imageViews.size(); i++) {
                click_count = 0;
                final int ix = i;
                imageViews.get(i).setImageResource(R.color.blue);
                imageViews.get(i).setClickable(true);
            }

        }
    }
}
