package jp.mstssk.twiccaplugins.voice_recognizer_search;

//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;

public class SettingActivity extends Activity {
    
    private SharedPreferences preference;
    private CheckBox checkbox_insert_space;
    private CheckBox checkbox_confirm_dialog;
//  private ArrayList<String> locales;
//  private Spinner spinner;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transparent);

        // SharedPreferencesオブジェクト取得
        preference = PreferenceManager.getDefaultSharedPreferences(this);
/*
        // 現在の環境で使えるロケールの一覧を取得
        String[] system_locales = getAssets().getLocales();
        Arrays.sort(system_locales);
        locales = new ArrayList<String>(0);
        for (int i = 0; i < system_locales.length; i++) {
            String locale_symbol = system_locales[i];
            if (locale_symbol.length() == 2) {
                locales.add(locale_symbol);
            }
        }
*/
    }
    
    @Override
    public void onStart(){
        super.onStart();
        
        View view = LayoutInflater.from(this).inflate(R.layout.setting_dialog, null);
        
/*
        // 言語設定Spinnerの値
        String[] list = new String[locales.size()];
        for (int i = 0; i < list.length; i++) {
            Locale locale = new Locale(locales.get(i));
            list[i] = locale.getDisplayName(locale);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                                    android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner)view.findViewById(R.id.SettingDialogLangSpinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(locales.indexOf(preference.getString(
                getString(R.string.key_language), Locale.getDefault().getLanguage())));
*/
        
        // 空白挿入Checkboxの値
        checkbox_insert_space = (CheckBox)view.findViewById(R.id.SettingDialogInsertSpaceCheckbox);
        checkbox_insert_space.setChecked(preference.getBoolean(getString(R.string.key_insert_space), 
                    getResources().getBoolean(R.attr.default_insert_space)));
        
        // 確認ダイアログCheckboxの値
        checkbox_confirm_dialog = (CheckBox)view.findViewById(R.id.SettingDialogConfirmDialogCheckbox);
        checkbox_confirm_dialog.setChecked(preference.getBoolean(getString(R.string.key_confirm_dialog), 
                    getResources().getBoolean(R.attr.default_confirm_dialog)));
        
        // 設定用ダイアログ表示
        new AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = preference.edit();
//                    editor.putString(getString(R.string.key_language), locales.get(spinner.getSelectedItemPosition()));
                    editor.putBoolean(getString(R.string.key_insert_space), checkbox_insert_space.isChecked());
                    editor.putBoolean(getString(R.string.key_confirm_dialog), checkbox_confirm_dialog.isChecked());
                    editor.commit();
                    finish();
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            })
            .setCancelable(true).show();
        
    }
    
}
