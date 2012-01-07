package jp.mstssk.twiccaplugins.voice_recognizer_search;

/*
 * twicca 音声検索プラグイン
 * 
 * @adakoda さんのソース（http://www.adakoda.com/android/000164.html）と
 * @yyaammaa のspeech2tweetのソース（http://bit.ly/5j68O0）を
 * 参考にさせていただきました。
 */

import java.util.ArrayList;
//import java.util.Locale;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;

public class VoiceRecognizerActivity extends Activity {

    // てきとう
    private final int REQUEST_CODE = 777;
    private boolean recognizerNotFoundFlg = false;
    private SharedPreferences preference = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.transparent);
    }
    
    @Override
    public void onStart(){
        super.onStart();
        recognize();
    }
    
    // 音声認識開始
    private void recognize(){
        recognizerNotFoundFlg = false;
        try {
            // インテント発行
            startActivityForResult(getRecognizerIntent(), REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // このインテントに応答できるアクティビティがインストールされていない場合
            onRecognizerActivityNotFound();
        }
    }
    
    // 音声認識インテント取得
    private Intent getRecognizerIntent() {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // ACTION_WEB_SEARCH
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                );
        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.recognizing_message));
//        intentによる言語選択は未実装らしい
//        無理にやるとするなら、システムの言語設定をintent投げている間だけ変更するとか
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
//                preference.getString(getString(R.string.key_language),
//                        Locale.getDefault().getLanguage()));
        return intent;
    }
    
    // Google音声検索がインストールされていない場合に呼び出す
    private void onRecognizerActivityNotFound(){
        recognizerNotFoundFlg = true;
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // marketでGoogle音声検索のページにとぶ
                    Intent intent = new Intent(Intent.ACTION_VIEW); 
                    intent.setData(Uri.parse(getString(R.string.market_uri)));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish(); // 終了しなくてもいいかな？
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // キャンセル時はactivityを終了してtwiccaに戻る
                    finish();
                }
            }).setCancelable(true).create().show();
    }
    
    // 音声認識アクティビティ終了時に呼び出される
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // 自分が投げたインテントであれば応答する
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String resultsString = "";
            Resources res = getResources();
            
            // 結果文字列リスト
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            
            // 文字列が複数あった場合に結合
//            for (int i = 0; i< results.size(); i++) {
//                resultsString += results.get(i);
//            }
            resultsString = results.get(0);
            
            // スペースを削除
            if (!preference.getBoolean(getString(R.string.key_insert_space), 
                    res.getBoolean(R.attr.default_insert_space))) {
                resultsString = delSpace(resultsString);
            }
            
            // twicca本体に返すintentをセット
            Intent keywordIntent = getIntent();
            keywordIntent.putExtra(Intent.EXTRA_TEXT, resultsString);
            setResult(RESULT_OK, keywordIntent);

            // 確認ダイアログ
            if (preference.getBoolean(getString(R.string.key_confirm_dialog), 
                    res.getBoolean(R.attr.default_confirm_dialog))) {
                new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_mic)
                .setTitle(getString(R.string.recognition_result))
                .setMessage(resultsString)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 結果をtwiccaに返す
                        finish(); // activityを終了
                    }
                }).setNeutralButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // もう一度
                        recognize();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // キャンセル時はactivityを終了してtwiccaに戻る
                        setResult(RESULT_CANCELED, null);
                        finish();
                    }
                }).setCancelable(true).create().show();
            } else {
                // 結果をtwiccaに返す
                finish(); // activityを終了
            }
            
        } else if (!recognizerNotFoundFlg) {
            // インストール確認ダイアログを出さない場合も終了
            finish();
        }
        
    }
    
    // スペース除去
    private String delSpace(String str) {
        return Pattern.compile(" ").matcher(str).replaceAll("");
    }
}