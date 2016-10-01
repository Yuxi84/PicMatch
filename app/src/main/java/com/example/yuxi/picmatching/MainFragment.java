package com.example.yuxi.picmatching;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class MainFragment extends Fragment {
    private final static int ImageNumberTotal = 16;
    private final static int ImageNumberNeed = 8;
    private final static int TOTALCARDNUM = 16;
    private static ArrayList<Integer> TotalImages;
    private ArrayList<Integer> pickedImages;
    private int[] cards = new int[TOTALCARDNUM];

    private SoundPool mySoundPool;
    private int mySoundMatch, mySoundMisMatch;
    private float myVolume = 1f;

    private int stepCount;

    private ImageButton firstCard; //in click event remember first card
    private int firstCardImgPos;//remember first card[i]
    private int matchedCardsNum;

    private Handler hideHandler = null;
    private Handler restartHandler = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Retain this fragment across configuration changes
        setRetainInstance(true);
        //initialize the game
        initGame();

        mySoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,0);
        mySoundMatch = mySoundPool.load(getActivity(),R.raw.sergenious_moveo,1);
        mySoundMisMatch = mySoundPool.load(getActivity(), R.raw.sergenious_movex,1);

        hideHandler = new Handler();
        restartHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initView(rootView);
        return rootView;
    }


    public void initGame(){
        loadImages();
        pickImages();
    }

    private void initView(View rootView){
        stepCount = 0;
        matchedCardsNum = 0;
        final TextView stepView = (TextView) rootView.findViewById(R.id.steps);
        stepView.setText(R.string.step_count); //initialize stepView


        Resources res = getResources();
        for (int i = 0; i < TOTALCARDNUM; i++) {
            final int tmp_i = i;
            int id = res.getIdentifier(//IDs for ImageButton
                    "card" + i, "id", getActivity().getPackageName());

            final ImageButton ib = (ImageButton) rootView.findViewById(id);
            //TODO block ui? initialize ib
            if (ib.getDrawable() != null){
                ib.setImageDrawable(null);
            }
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    animate(view);
                    if (((ImageView)view).getDrawable()!=null){
                        //the user click the same card clicked before, ignore
                        return;
                    }else{
                        stepCount++;
                        stepView.setText(getString(R.string.step_count_update)+stepCount);
                        // (thread) show the image, viewtag, get card[i]
                        int imagePos = cards[tmp_i];
                        final int ImageID = (pickedImages.get(imagePos)).intValue();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Drawable backgroundImg = getResources().getDrawable(ImageID);
                                ib.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ib.setImageDrawable(backgroundImg);
                                    }
                                });
                            }
                        }).start();

                        //check whether first card based on count, if first save card[i]
                        if (stepCount%2 != 0){//first click
                            firstCard = ib;
                            firstCardImgPos = imagePos;
                        }else{// if second, check card[i] match (yes, see notes, no, hide( postdelaysetbackground resource to 0))
                            if (firstCardImgPos == imagePos){//cards match
                                matchedCardsNum +=2;
                                mySoundPool.play(mySoundMatch, myVolume, myVolume, 1, 0, 1f);
                                if (matchedCardsNum == TOTALCARDNUM){
                                    //Dialog to ask whether restart game
                                    askRestart();

                                }

                            }else{//cards mismatch
                                //disable users to click cards
                                ((MainActivity)getActivity()).DelayStart();
                                mySoundPool.play(mySoundMisMatch, myVolume, myVolume, 1, 0, 1f);
                                hideHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        firstCard.setImageDrawable(null);
                                        ib.setImageDrawable(null);
                                        //enable pick cards now
                                        ((MainActivity)getActivity()).DelayEnd();
                                    }
                                }, 1000);

                            }
                        }

                    }

                }
            });
        }
    }

    private void loadImages(){
        TotalImages = new ArrayList<>();
        TotalImages.add(R.drawable.card1);
        TotalImages.add(R.drawable.card2);
        TotalImages.add(R.drawable.card3);
        TotalImages.add(R.drawable.card4);
        TotalImages.add(R.drawable.card5);
        TotalImages.add(R.drawable.card6);
        TotalImages.add(R.drawable.card7);
        TotalImages.add(R.drawable.card8);
        TotalImages.add(R.drawable.card9);
        TotalImages.add(R.drawable.card10);
        TotalImages.add(R.drawable.card11);
        TotalImages.add(R.drawable.card12);
        TotalImages.add(R.drawable.card13);
        TotalImages.add(R.drawable.card14);
        TotalImages.add(R.drawable.card15);
        TotalImages.add(R.drawable.card16);
    }
    private void pickImages(){
        pickedImages = new ArrayList<>();
        Random imageR = new Random();
        for (int i=0; i<ImageNumberNeed;i++){
            //choose an image from original pool of image (start from 16)
            int pickedImagePosition = imageR.nextInt(ImageNumberTotal-i);
            Integer picked = TotalImages.remove(pickedImagePosition);
            pickedImages.add(picked);

        }

        //generate an Integer list to assign int value later to each card
        ArrayList<Integer> ImgPosForCards = new ArrayList<>();//size should be 16(TOTALCARDNUM)
        for (int j=0;j<ImageNumberNeed;j++){
            ImgPosForCards.add(new Integer(j));
            ImgPosForCards.add(new Integer(j));//do twice since later assign to 16 cards
        }

        //assign each card a randomly picked Image position
        for (int position=0;position<TOTALCARDNUM;position++){
            int pickedPos = imageR.nextInt(ImgPosForCards.size());
            int ImgPos = ImgPosForCards.get(pickedPos);
            ImgPosForCards.remove(pickedPos);
            cards[position]=ImgPos;
        }
    }

    public void animate(View tartgetView) {
        Animator anim = AnimatorInflater.loadAnimator(getActivity(),
                R.animator.animate_click);
        if (tartgetView != null) {
            anim.setTarget(tartgetView);
            anim.start();
        }
    }

    public void askRestart(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.restart_msg);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.restart_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restartGame();
                    }
                });
        builder.setNegativeButton(R.string.restart_no,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((MainActivity)getActivity()).finish();
                    }
                });
        final Dialog dialog = builder.create();
        restartHandler.post(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    private void restartGame() {
        //TODO:
        initGame();
        initView(getView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.restartHandler.removeCallbacksAndMessages(null);
        this.hideHandler.removeCallbacksAndMessages(null);
    }


}
