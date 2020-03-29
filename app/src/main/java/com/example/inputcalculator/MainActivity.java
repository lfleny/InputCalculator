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

    Double result = null;
    Calculator calc = new Calculator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button evaluateButton = findViewById(R.id.calculator_button);

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
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (UserInputException e) {
                    errorTextView.setText(e.getMessage());
                } catch (CalculateException e) {
                    errorTextView.setText(e.getMessage());
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    String sStackTrace = sw.toString(); // stack trace as a string
                    Log.d("ERROR", sStackTrace);
                    errorTextView.setText(sStackTrace);
                }
            }
        });
    }
}
