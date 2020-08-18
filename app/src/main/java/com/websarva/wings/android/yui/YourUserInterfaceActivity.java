package com.websarva.wings.android.yui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class YourUserInterfaceActivity extends AppCompatActivity
        implements View.OnClickListener, TextToSpeech.OnInitListener {

  /**
   * Alarm
   */
  private AlarmManager am;
  private PendingIntent pending;
  private int requestCode = 1;

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

    /**
     * Alarm
     */
//    Button button_alarm = this.findViewById(R.id.button_alarm1);
//    String str_alarm = "Alarm Start";
//    button_alarm.setText(str_alarm);

    /**
     * Alarm2
     */

//    Button buttonStart1 = this.findViewById(R.id.button_start);
//    buttonStart1.setOnClickListener(new View.OnClickListener(){
//      @Override
//      public void onClick(View v) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        //  10sec
//        calendar.add(Calendar.SECOND, 10);
//
//        Intent intent = new Intent(getApplicationContext(), AlarmNotification.class);
//        intent.putExtra("RequestCode", requestCode);
//
//        pending = PendingIntent.getBroadcast(
//                getApplicationContext(), requestCode, intent, 0);
//
//        //  アラームをセットする
//        am = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//        if (am != null) {
//          am.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pending);
//
//          //  トーストで設定されたことを表示
//          Toast.makeText(getApplicationContext(), "alarm start", Toast.LENGTH_SHORT).show();
//
//          Log.d("debug", "start");
//        }
//      }
//    });

    //  アラームの取り消し
//    Button buttonCancel = findViewById(R.id.button_cancel);
//    buttonCancel.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        Intent intent = new Intent(getApplicationContext(), AlarmNotification.class);
//        PendingIntent pending = PendingIntent.getBroadcast(
//                getApplicationContext(), requestCode, intent, 0);
//
//        //  アラームを解除する
//        AlarmManager am = (AlarmManager)YourUserInterfaceActivity.this.getSystemService(ALARM_SERVICE);
//        if (am != null) {
//          am.cancel(pending);
//          Toast.makeText(getApplicationContext(), "alarm cancel", Toast.LENGTH_SHORT).show();
//          Log.d("debug","cancel");
//        }
//        else{
//          Log.d("debug", "null");
//        }
//
//      }
//    });



    /**
     * TTSインスタンス生成
     */

    tts = new TextToSpeech(this,this);

    Button ttsButton = findViewById(R.id.button_tts);
    ttsButton.setOnClickListener(this);

    /**
     * Alarm method
     */
//    button_alarm.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        //  時間をセットする
//        Calendar calendar = Calendar.getInstance();
//        //  Calendarを使って現在の時間をミリ秒で取得
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        //  5秒後に設定
//        calendar.add(Calendar.SECOND, 5);
//
//        //  明示的なBroadCast
//        Intent intent = new Intent(YourUserInterfaceActivity.this, AlarmBroadcastReceiver.class);
//        PendingIntent pending = PendingIntent.
//                getBroadcast(getApplicationContext(), 0, intent, 0);
//
//        //  アラームをセットする
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        if(am != null){
//          am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
//
//          Toast.makeText(getApplicationContext(), "Set Alarm", Toast.LENGTH_SHORT).show();
//        }
//
//      }
//    });


    /**
     * 音声認識 method
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
    textToSpeechByConditionalBranch();
  }//TTS開始？

  public void textToSpeechByConditionalBranch(){
    // 音声認識した文字列から、String型の文字列を取得
    TextView textRecognizer = findViewById(R.id.text_view);
    String string = textRecognizer.getText().toString();

    // 会話のパターンを条件分岐
    talkPatternConditionalBranch(string);

    // TTS、発話
    speechText();
  }

  private void talkPatternConditionalBranch(String string){
    switch(string){
      case "こんにちは":
        textView.setText("こんにちは、あるじさま");
        break;
      case "おはよう":
        textView.setText("おはようございます、ご主人");
        break;
      case "こんばんは":
        textView.setText("こんばんは、マスター");
        break;
      case "ごきげんよう":
        textView.setText("ごきげんよう、おじょうさま");
        break;
      case "さようなら":
        textView.setText("さようなら、ご主人");
        break;
      case "さよなら":
        textView.setText("さようなら、ご主人");
        break;
      case "バイバイ":
        textView.setText("バイバイ、マスター");
        break;
      case "ハロウィン":
        textView.setText("トリック・オア・トリート、お菓子くれなきゃイタズラしちゃうぞ");
        break;
    }
    if(string.length() > 10){
      textView.setText("そうなんだね");
    }

        /*
        if( "こんにちは".equals(string) ){
            textView.setText("こんにちは、あるじさま");
        }
        if( "おはよう".equals(string) ){
            textView.setText("おはようございます、ご主人");
        }
        if( "こんばんは".equals(string) ){
            textView.setText("こんばんは、マスター");
        }
        if( "ごきげんよう".equals(string) ){
            textView.setText("ごきげんよう、おじょうさま");
        }
        if( "さようなら".equals(string) ){
            textView.setText("さようなら、ご主人");
        }
        */
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
