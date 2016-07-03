package com.octopocus.octopocus;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView objectText;
    private EditText editText;

    private ClipboardManager clipboardManager;
    private ClipData clip;

    private MenuItem pasteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objectText = (TextView) findViewById(R.id.object_name); // --> not used anymore
        editText = (EditText) findViewById(R.id.editText);

        // source: https://developer.android.com/guide/topics/text/copy-paste.html
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }



    public void writeDollar(Dollar dollar){   // --> not used anymore
        objectText.setText("Object: " + dollar.result.Name + " Score: " + dollar.result.Score);

    }

    public void executeCommand(String name) {
        String text = editText.getText().toString();
        if (name.equals("Copy")) {
            if (text != null && text != "") {
                System.out.println("Copy " + text);
                clip = ClipData.newPlainText("simple text", text); // saving the text on the clipboard
                clipboardManager.setPrimaryClip(clip);
            }
            // instructions
        } else if (name.equals("Paste")) {
            System.out.println("Paste");

            ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);
            String pasteData = item.getText().toString();
            if (pasteData != null || pasteData == "") {

                String newText = "";

                if (editText.hasSelection()) {
                    int start = editText.getSelectionStart();
                    int end = editText.getSelectionEnd();
                    newText = text.substring(0, start);
                    newText += pasteData;
                    // ---> You can always only select everything anyway with the gesture technique
                    // ---> So, newText = pasteData would be sufficient
                    newText += text.substring(end, text.length());
                    editText.setText(newText);
                    editText.setSelection(end); // setting the cursor to the end of the text

                } else {
                    int cursor_pos = editText.getSelectionStart();
                    newText = text.substring(0, cursor_pos);
                    newText += pasteData;
                    newText += text.substring(cursor_pos, text.length()); // fitting the stored text into the apparent text
                    editText.setText(newText);
                    editText.setSelection(cursor_pos + (pasteData.length())); // setting the cursor after the inserted text
                }

            }

        } else if (name.equals("Select")) {
            editText.setSelection(0, text.length()); // selecting the whole text

        } else if (name.equals("Cut")){

            if(editText.hasSelection()){

                editText.setText(""); // Deleting text, if selected
            }
        }
    }
}
