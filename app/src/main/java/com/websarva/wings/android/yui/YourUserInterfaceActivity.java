package com.websarva.wings.android.yui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class YourUserInterfaceActivity extends AppCompatActivity
        implements View.OnClickListener, TextToSpeech.OnInitListener {

    /**
     * TTS
     */
    private TextToSpeech tts;
    private  static final String TAG = "TestTTS";

    /**
     * Recognizer
     */
    private static final int REQUEST_CODE = 1000;
    private TextView textView;

    private int lang ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_user_interface);

        //  TTSインスタンス生成
        tts = new TextToSpeech(this,this);

        Button ttsButton = findViewById(R.id.button_tts);
        ttsButton.setOnClickListener(this);


        /**
         * 音声認識
         */
        //  言語選択 0:日本語、1:英語、2:オフライン、その他:General
        lang = 0;

        //  認識結果を表示させる
        textView = (TextView)findViewById(R.id.text_view);

        Button buttonStart = (Button)findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  音声認識を開始
                speech();
            }
        });

    }

    /**
     * 音声認識
     */

    private void speech(){
        //  音声認識のIntentインスタンス
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        if(lang == 0){
            //  日本語
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPAN.toString() );
        }
        else if(lang == 1){
            //  英語
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString() );
        }
        else if(lang == 2){
            //  Off line mode
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        }
        else{
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        }

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力");

        try{
            //  インテント発行
            startActivityForResult(intent, REQUEST_CODE);
        }
        catch (ActivityNotFoundException e){
            e.printStackTrace();
            textView.setText(R.string.error);
        }
    }

    //  結果を受け取るために onActivityResult を設置
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            //  認識結果を ArrayList で取得
            ArrayList<String> candidates =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if(candidates.size() > 0){
                //  認識結果候補で一番有力なものを表示
                textView.setText( candidates.get(0) );
            }
        }
    }

    /**
     * TextToSpeech
     */

    @Override
    public void onInit(int status){
        //  TTS初期化
        if(TextToSpeech.SUCCESS == status){
            Log.d(TAG, "initialized");
        } else {
            Log.d(TAG, "failed to initialized");
        }
    }

    @Override
    public void onClick(View v){
        // 音声認識した文字列から、String型の文字列を取得
        TextView textRecognizer = findViewById(R.id.text_view);
        String string = textRecognizer.getText().toString();

        // 会話のパターンを条件分岐
        conditionalBranch(string);

        // TTS、発話
        speechText();
    }//TTS開始？

    private void conditionalBranch(String string){
        if( "こんにちは".equals(string) ){
            textView.setText("こんにちは、あるじさま");
        }
        if( "おはよう".equals(string) ){
            textView.setText("おはようございます、ごしゅじん");
        }
        if( "こんばんは".equals(string) ){
            textView.setText("こんばんは、マスター");
        }
        if( "ごきげんよう".equals(string) ){
            textView.setText("ごきげんよう、おじょうさま");
        }
    }

    private void shutDown(){
        if (null != tts){
            //  to release the resource of TextToSpeech
            tts.shutdown();
        }
    }

    private void speechText(){
        //EditText
        TextView editor = findViewById(R.id.text_view);//edit_text);
        //editor.selectAll();
        //  EditTextからテキストを取得
        String string = editor.getText().toString();

        if(0 < string.length()){
            if(tts.isSpeaking()){
                tts.stop();
                return;
            }
            setSpeechRate(1.0f);
            setSpeechPitch(1.0f);

            if (Build.VERSION.SDK_INT >= 21){
                //  SDK 21  以上
                tts.speak(string, TextToSpeech.QUEUE_FLUSH, null, "messageID");
            }
            else{
                //  tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)に
                //  KEY_PARAM_UTTERANCE_ID を HasMap で設定
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
                tts.speak(string, TextToSpeech.QUEUE_FLUSH, map);
            }

            setTtsListener();//ここでTTS開始？
        }

    }

    //  読み上げのスピード
    private void setSpeechRate(float rate){
        if(null != tts){
            tts.setSpeechRate(rate);
        }
    }

    //  読み上げのピッチ
    private void setSpeechPitch(float pitch){
        if (null != tts){
            tts.setPitch(pitch);
        }
    }

    //  読み上げの始まりと終わりを取得
    private void setTtsListener() {
        //  android version more than 15th
        if (Build.VERSION.SDK_INT >= 15) {
            int listenerResult =
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.d(TAG, "progress on Start" + utteranceId);
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.d(TAG, "progress on Done" + utteranceId);
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.d(TAG, "progress on Error" + utteranceId);
                        }
                    });

            if (listenerResult != TextToSpeech.SUCCESS) {
                Log.e(TAG, "failed to add utterance progress listener");

            }
        } else {
            Log.e(TAG, "Build VERSION is less than API 15");
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        shutDown();
    }
}
