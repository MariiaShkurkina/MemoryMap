Checkstyle / pmd / spotbugs можно мьютить в тех случаях, когда они не могут быть исправлены или выдают ошибку, которая
не имеет смысла исправлять. Ниже примеры, как это делается.

```java
package ru.tbank.education;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

//Пример, на случай проблем с чекстайлом:
//@SuppressWarnings("checkstyle:LineLength")
// PMD:OFF
@SuppressFBWarnings("NP") // Пример заглушки для SpotBugs
public class Example {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        // Пример, на случай проблем с чекстайлом:
        // CHECKSTYLE:OFF LineLength
        String longString =
                "очень длинная строка очень длинная строкаочень длинная строка очень длинная строка очень длинная строка ";
        // CHECKSTYLE:ON

        String s = "";
        s = null;
        System.out.println(s.length());
    }
}
```