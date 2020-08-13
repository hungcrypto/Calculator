package edu.tacoma.uw.hungt94.calculator.FrontEnd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.speech.RecognizerIntent;

import android.app.Notification;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;
import java.util.ArrayList;
import java.util.Locale;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import edu.tacoma.uw.hungt94.calculator.R;
import edu.tacoma.uw.hungt94.calculator.authenticate.SignInActivity;

public class MainActivity extends AppCompatActivity {
    private Button one;
    private Button two;
    private Button three;
    private Button four;
    private Button five;
    private Button six;
    private Button seven;
    private Button eight;
    private Button nine;
    private Button zero;
    private Button add;
    private Button minus;
    private Button multiply;
    private Button divide;
    private Button equal;
    private Button decimal;
    private Button clearText;
    private Button clearSpeak;
    private Button dark;
    private Button light;
    private RelativeLayout layout;
    private ImageButton speak ;
    private TextView info;
    private TextView result;
    private final char ADDITION = '+';
    private final char MINUS = '-';
    private final char MULTIPLY = '*';
    private final char DIVISION = '/';

    private final char EQUAL = 0;
    private double value1 = Double.NaN;
    private double value2;
    private boolean isDecimal;


    private char ACTION;
    private TextToSpeech textSpeak;
    private boolean lastNumeric;

    private boolean stateError;

    private final int REQ_CODE_SPEECH_INPUT = 100;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUIViews();
        setupAction();
        // Find and set OnClickListener to numeric buttons
        setNumericOnClickListener();

        // Find and set OnClickListener to operator buttons, equal button and decimal point button
        setOperatorOnClickListener();
        setupTextToSpeech();

        setupDarkMode();

    }

    private void setupTextToSpeech(){
        textSpeak = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    textSpeak.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }
    public void onpause(){
        if(textSpeak != null){
            textSpeak.stop();
            textSpeak.shutdown();
        }
        super.onPause();
    }

    private void setupUIViews(){
        one = (Button)findViewById(R.id.button1);
        two = (Button)findViewById(R.id.button2);
        three = (Button)findViewById(R.id.button3);
        four = (Button)findViewById(R.id.button4);
        five = (Button)findViewById(R.id.button5);
        six = (Button)findViewById(R.id.button6);
        seven = (Button)findViewById(R.id.button7);
        eight = (Button)findViewById(R.id.button8);
        nine = (Button)findViewById(R.id.button9);
        zero = (Button)findViewById(R.id.button0);
        add = (Button)findViewById(R.id.buttonAdd);
        minus = (Button)findViewById(R.id.buttonMinus);
        multiply = (Button)findViewById(R.id.buttonMultiply);
        divide = (Button)findViewById(R.id.buttonDivide);
        equal = (Button)findViewById(R.id.buttonEqual);
        clearText = (Button)findViewById(R.id.buttonClear);
        clearSpeak = (Button)findViewById(R.id.buttonClearSpeak);
        decimal = (Button)findViewById(R.id.buttonDecimal);
        info = (TextView)findViewById(R.id.textInfo);
        result = (TextView)findViewById(R.id.textScreen);
        speak = (ImageButton)findViewById(R.id.speakButton);
        dark = (Button)findViewById(R.id.buttonDark);
        light = (Button)findViewById(R.id.buttonLight);
        layout = findViewById(R.id.relativelayout);

    }
    private void setupDarkMode(){
            dark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                layout.setBackgroundColor(Color.BLACK);
            }
        });
        light.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                layout.setBackgroundColor(Color.WHITE);
            }
        });
    }



    private void setNumericOnClickListener() {
        // Create a common OnClickListener
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Just append/set the text of clicked button
                Button button = (Button) v;
                if (stateError) {
                    // If current state is Error, replace the error message
                    result.setText(button.getText());
                    stateError = false;

                } else {
                    // If not, already there is a valid expression so append to it
                    result.append(button.getText());
                }

                // Set the flag
                lastNumeric = true;
            }
        };
    }
    /**
     * Find and set OnClickListener to operator buttons, equal button and decimal point button.
     */
    private void setOperatorOnClickListener() {
        // Create a common OnClickListener for operators

        new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the current state is Error do not append the operator
                // If the last input is number only, append the operator
                if (lastNumeric && !stateError) {
                    Button button = (Button) v;
                    result.append(button.getText());
                    lastNumeric = false;
                }
            }
        };

        // Clear button
        findViewById(R.id.buttonClearSpeak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("");  // Clear the screen
                info.setText("");  // Clear the input
                // Reset all the states and flags
                lastNumeric = false;
                stateError = false;
            }
        });


        findViewById(R.id.speakButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stateError) {
                    // If current state is Error, replace the error message
                    result.setText("Try Again");
                    stateError = false;
                } else {
                    // If not, already there is a valid expression so append to it
                    promptSpeechInput();
                }
                // Set the flag
                lastNumeric = true;

            }
        });
    }
    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Please say something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                   "Your device is not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Logic to calculate the solution.
     */
    private void onEqual() {
        // If the current state is error, nothing to do.
        // If the last input is a number only, solution can be found.
        if (lastNumeric && !stateError) {
            // Read the expression
            String inputNumber = info.getText().toString();

            // Create an Expression (A class from exp4j library)
            try {
                Expression expression = null;
                try {
                    expression = new ExpressionBuilder(inputNumber).build();
                    double outcome = expression.evaluate();
                    result.setText(Double.toString(outcome));
                    String i = new String("The result is" + outcome);
                    textSpeak.speak(i,TextToSpeech.QUEUE_FLUSH,null);
                } catch (Exception e){
                    result.setText("Error, please try again!");
                }

            } catch (ArithmeticException ex) {
                // Display an error message
                result.setText("Error, please try again!");
                stateError = true;
                lastNumeric = false;
            }
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String change=result.get(0);

                    // english-lang
                    change=change.replace("x","*");
                    change=change.replace("X","*");
                    change=change.replace("add","+");
                    change=change.replace("sub","-");
                    change=change.replace("to","2");
                    change=change.replace(" plus ","+");
                    change=change.replace(" minus ","-");
                    change=change.replace(" times ","*");
                    change=change.replace(" into ","*");
                    change=change.replace(" in2 ","*");
                    change=change.replace(" multiply by ","*");
                    change=change.replace(" divide by ","/");
                    change=change.replace("divide","/");
                    change=change.replace("equal","=");
                    change=change.replace("equals","=");

                    if(change.contains("=")){
                        change=change.replace("=","");
                        info.setText(change);
                        onEqual();
                    }else{
                        info.setText(change);
                    }
                }

                break;
            }

        }
    }


    private void compute(){
        if(!Double.isNaN(value1)){
            value2 = Double.parseDouble(info.getText().toString());

            switch(ACTION){
                case ADDITION:
                    value1 = value1 + value2;
                    isDecimal = false;
                    break;
                case MINUS:
                    value1 = value1 - value2;
                    isDecimal = false;
                    break;
                case MULTIPLY:
                    value1 = value1 * value2;
                    isDecimal = false;
                    break;
                case DIVISION:
                    value1 = value1 / value2;
                    isDecimal = false;
                    break;
                case EQUAL:
                    break;

            }
        }
        else{
            value1 = Double.parseDouble(info.getText().toString());
        }
    }
    private void setupAction(){
        zero.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "0");
            }
        });
        one.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "1");
            }
        });
        two.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "2");
            }
        });
        three.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "3");
            }
        });
        four.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "4");
            }
        });
        five.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "5");
            }
        });
        six.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "6");
            }
        });
        seven.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "7");
            }
        });
        eight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "8");
            }
        });
        nine.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                info.setText(info.getText().toString() + "9");
            }
        });
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                compute();
                ACTION = ADDITION;
                result.setText(String.valueOf(value1) + "+");
                info.setText(null);
            }
        });
        minus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                compute();
                ACTION = MINUS;
                result.setText(String.valueOf(value1) + "-");
                info.setText(null);
            }
        });
        multiply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                compute();
                ACTION = MULTIPLY;
                result.setText(String.valueOf(value1) + "*");
                info.setText(null);
            }
        });
        divide.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                compute();
                ACTION = DIVISION;
                result.setText(String.valueOf(value1) + "/");
                info.setText(null);
            }
        });

        equal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                compute();
                ACTION = EQUAL;
                result.setText(result.getText().toString() + String.valueOf(value2) + "=" + String.valueOf(value1));
                String i = new String("The result is" + value1);
                textSpeak.speak(i,TextToSpeech.QUEUE_FLUSH,null);

                if(ACTION == ADDITION){
                    add.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            compute();
                            ACTION = ADDITION;
                            result.setText(String.valueOf(value1) + "+");
                            info.setText(null);
                        }
                    });
                }
                if(ACTION == MINUS){
                    minus.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            compute();
                            ACTION = MINUS;
                            result.setText(String.valueOf(value1) + "-");
                            info.setText(null);
                        }
                    });
                }
                if(ACTION == MULTIPLY){
                    multiply.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            compute();
                            ACTION = MULTIPLY;
                            result.setText(String.valueOf(value1) + "*");
                            info.setText(null);
                        }
                    });
                }
                if(ACTION == DIVISION){
                    divide.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            compute();
                            ACTION = DIVISION;
                            result.setText(String.valueOf(value1) + "/");
                            info.setText(null);
                        }
                    });
                }

            }
        });
        decimal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isDecimal){
                    // do nothing
                }else{
                    info.setText(info.getText() + ".");
                    isDecimal = true;
                }
            }
        });
        clearText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(info.getText().length() > 0){
                    CharSequence name = info.getText().toString();
                    info.setText(name.subSequence(0,name.length()-1));
                }else{
                    value1 = Double.NaN;
                    value2 = Double.NaN;
                    isDecimal = false;
                    info.setText(null);
                    result.setText(null);
                }
            }
        });

    }
}