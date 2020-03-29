package com.example.inputcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.inputcalculator.Calculator.Calculator;
import com.example.inputcalculator.exception.CalculateException;
import com.example.inputcalculator.exception.UserInputException;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_ERROR_MESSAGE = "error_message";
    private static final String KEY_RESULT = "result";
    Double result = null;
    String error_message = "";
    Calculator calc = new Calculator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button evaluateButton = findViewById(R.id.calculator_button);

        if (savedInstanceState != null) {
            if (savedInstanceState.getString(KEY_ERROR_MESSAGE, "") != "") {
                error_message = savedInstanceState.getString(KEY_ERROR_MESSAGE, "");
                TextView errorTextView = findViewById(R.id.error_text);
                errorTextView.setText(error_message);
            }
            if (savedInstanceState.getString(KEY_RESULT, null) != null) {
                result = Double.parseDouble(savedInstanceState.getString(KEY_RESULT, null));
                TextView answerTextView = findViewById(R.id.answer_text);
                answerTextView.setText(result.toString());
            }
        }

        evaluateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView errorTextView = findViewById(R.id.error_text);
                errorTextView.setText("");
                EditText userInput = findViewById(R.id.calculator_input);
                String input = userInput.getText().toString();
                try {
                    result = calc.calculate(input);
                    TextView answerTextView = findViewById(R.id.answer_text);
                    answerTextView.setText(result.toString());
                } catch (UserInputException e) {
                    error_message = e.getMessage();
                    errorTextView.setText(error_message);
                } catch (CalculateException e) {
                    error_message = e.getMessage();
                    errorTextView.setText(error_message);
                } catch (Exception e) {
                    error_message = getString(R.string.error_global);
                    errorTextView.setText(error_message);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(KEY_ERROR_MESSAGE, error_message);
        savedInstanceState.putString(KEY_RESULT, result.toString());
    }
}
