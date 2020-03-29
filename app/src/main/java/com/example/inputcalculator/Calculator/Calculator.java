package com.example.inputcalculator.Calculator;

import android.util.Log;

import com.example.inputcalculator.exception.CalculateException;
import com.example.inputcalculator.exception.UserInputException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.StringTokenizer;

public class Calculator {

    /**
     * Преобразовать строку в массив
     * @param eq Входная строка
     * @return Входная строка, разделенная на члены
     */
    private static String[] split(String eq) {
        String[] strings = eq.replaceAll("\\s+","")
                .replaceAll(",",".")
                .replaceAll("×","*")
                .replaceAll("÷","/")
                .split("((?<=\\+)|(?=\\+))|((?<=\\*)|(?=\\*))|((?<=/)|(?=/))|((?<=-)|(?=-))|((?<=\\))|(?=\\)))|((?<=\\())|(?=\\()");
        return strings;
    }

    /**
     * Преобразовать строку в обратную польскую нотацию
     * @param convertedString Входная строка
     * @return Выходная строка в обратной польской нотации
    */
    public static String[] inputStringConverter(String[] convertedString) {
        ArrayList<String> stack = new ArrayList<>();
        ArrayList<String> out = new ArrayList<>();

        String cTmp;
        boolean may_unary = true;

        for (String elem: convertedString) {
            if (elem.equals("(")) {
//                if (stack.contains("(") && stack.contains("unary_minus")) {
//                    stack.remove("unary_minus");
//                }
                stack.add(elem);
                may_unary = true;
            } else if (elem.equals(")")) {
                cTmp = stack.get(stack.size()-1);
                while (!cTmp.equals("(")) {
                    if (stack.size() < 1) {
                        throw new UserInputException("Ошибка разбора скобок. Проверьте правильность выражения.");
                    }
                    if (!cTmp.equals("unary_minus")) {
                        out.add(cTmp);
                    }
                    stack.remove(stack.size()-1);
                    if (stack.size() == 0)  {
                        throw new UserInputException("Ошибка разбора скобок. Проверьте правильность выражения.");
                    }
                    stack.remove("unary_minus");
                    cTmp = stack.get(stack.size()-1);
                }
                stack.remove(stack.size()-1);
                may_unary = false;
            } else if (isOperator(elem)) {
                if (stack.size() == 0 && out.size() == 0 && elem.equals("-") && may_unary) {
                    stack.add("unary_minus");
                    may_unary = true;
                } else if (stack.size() != 0 && stack.get(stack.size()-1).equals("(") && elem.equals("-") && may_unary) {
                    stack.add("unary_minus");
                    may_unary = true;
                } else {
                    while (stack.size() > 0) {
                        cTmp = stack.get(stack.size() - 1);
                        if (((isOperator(cTmp) || cTmp.equals("("))&& elem.equals("-")) && may_unary) {
                            elem = "unary_minus";
                            break;
                        } else if (isOperator(cTmp) && (operatorPriority(elem) <= operatorPriority(cTmp))) {
                            out.add(cTmp);
                            stack.remove(stack.size() - 1);
                        } else {
                            break;
                        }
                    }
                    stack.add(elem);
                    may_unary = true;
                }
            } else {
                // Если символ не оператор - добавляем в выходную последовательность
                if (may_unary && stack.size() != 0) {
                    if (stack.get(stack.size() - 1).equals("unary_minus")) {
                        out.add("-" + elem);
                        stack.remove(stack.size()-1);
                    } else if (stack.contains("unary_minus") && stack.contains("(")) {
                        out.add("-" + elem);
                    } else {
                        out.add(elem);
                    }
                } else {
                    out.add(elem);
                }
                may_unary = false;
            }
        }

        // Если в стеке остались операторы, добавляем их в входную строку
        while (stack.size() > 0) {
            if (!stack.get(stack.size()-1).equals("unary_minus")) {
                out.add(stack.get(stack.size()-1));
            }
            stack.remove(stack.size()-1);
        }

        return  out.toArray(new String[out.size()]);
    }

    /**
     * Функция проверяет, является ли текущий символ оператором
     */
    private static boolean isOperator(String c) {
        switch (c) {
            case "-":
            case "+":
            case "*":
            case "/":
                return true;
        }
        return false;
    }

    /**
     * Возвращает приоритет операции
     * @param op String
     * @return int
     */
    private static byte operatorPriority(String op) {
        switch (op) {
            case "unary_minus":
                return 4;
            case "*":
            case "/":
                return 2;
        }
        return 1; // Тут остается + и -
    }

    /**
     * Считает выражение, записанное в обратной польской нотации
     * @return double result
     */
    public double calculate(String inputEquation) {
        String[] unformattedEquation = split(inputEquation);
        String[] polishString;
        try {
            polishString = inputStringConverter(unformattedEquation);
        } catch (UserInputException e) {
            throw new UserInputException(e.getMessage());
        }
        StringBuilder sb = new StringBuilder();
        for (String s : polishString)
        {
            sb.append(s);
            sb.append("\t");
        }

        String sIn = sb.toString();

        double dA = 0, dB = 0;
        String sTmp;
        Deque<Double> stack = new ArrayDeque<Double>();
        StringTokenizer st = new StringTokenizer(sIn);
        while(st.hasMoreTokens()) {
            try {
                sTmp = st.nextToken().trim();
                if (isOperator(sTmp)) {
                    if (stack.size() < 2) {
                        throw new CalculateException("Неверное количество данных в стеке для операции " + sTmp);
                    }
                    dB = stack.pop();
                    dA = stack.pop();
                    switch (sTmp.charAt(0)) {
                        case '+':
                            dA += dB;
                            break;
                        case '-':
                            dA -= dB;
                            break;
                        case '/':
                            dA /= dB;
                            break;
                        case '*':
                            dA *= dB;
                            break;
                        case '%':
                            dA %= dB;
                            break;
                        case '^':
                            dA = Math.pow(dA, dB);
                            break;
                        default:
                            throw new CalculateException("Недопустимая операция " + sTmp);
                    }
                    stack.push(dA);
                } else {
                    dA = Double.parseDouble(sTmp);
                    stack.push(dA);
                }
            } catch (Exception e) {
                throw new CalculateException("Недопустимый символ в выражении");
            }
        }

        if (stack.size() > 1) {
            throw new CalculateException("Количество операторов не соответствует количеству операндов");
        }

        return stack.pop();
    }
}