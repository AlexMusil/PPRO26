package cz.uhk.fim.ppro.ordinace.application.validation;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Nástroj pro validaci a normalizaci českého rodného čísla / čísla pojištěnce.
 * Ověřuje syntaktický formát, kalendářní platnost data narození i kontrolní součet modulo 11.
 */
public final class RodneCisloValidator {

    private static final Pattern RC_PATTERN = Pattern.compile("^(\\d{2})(\\d{2})(\\d{2})\\/?(\\d{3,4})$");

    private RodneCisloValidator() {
    }

    /**
     * Ověří, zda je rodné číslo syntakticky a sémanticky platné (formát, datum, modulo 11).
     *
     * @param rodneCislo Zadané rodné číslo (s lomítkem i bez něj).
     * @return true, pokud je číslo platné; jinak false.
     */
    public static boolean isValid(String rodneCislo) {
        if (rodneCislo == null) {
            return false;
        }

        String cleaned = rodneCislo.trim().replaceAll("\\s+", "");
        Matcher matcher = RC_PATTERN.matcher(cleaned);
        if (!matcher.matches()) {
            return false;
        }

        String digits = cleaned.replace("/", "");
        int len = digits.length();
        if (len != 9 && len != 10) {
            return false;
        }

        int yearPart = Integer.parseInt(digits.substring(0, 2));
        int monthPart = Integer.parseInt(digits.substring(2, 4));
        int dayPart = Integer.parseInt(digits.substring(4, 6));

        // Určení skutečného měsíce (ženy +50, alternativní číselné řady matrik +20 / +70)
        int realMonth = monthPart;
        if (realMonth > 70) {
            realMonth -= 70;
        } else if (realMonth > 50) {
            realMonth -= 50;
        } else if (realMonth > 20) {
            realMonth -= 20;
        }

        if (realMonth < 1 || realMonth > 12) {
            return false;
        }

        // Určení století
        int fullYear;
        if (len == 9) {
            // Devítimístná RČ byla přidělována do konce roku 1953
            fullYear = 1900 + yearPart;
        } else {
            // Desetimístná RČ od roku 1954
            int currentYear2Digits = LocalDate.now().getYear() % 100;
            if (yearPart <= currentYear2Digits) {
                fullYear = 2000 + yearPart;
            } else {
                fullYear = 1900 + yearPart;
            }
        }

        // Kontrola kalendářního data
        try {
            LocalDate birthDate = LocalDate.of(fullYear, realMonth, dayPart);
            if (birthDate.isAfter(LocalDate.now())) {
                return false;
            }
        } catch (DateTimeException e) {
            return false;
        }

        // Kontrola kontrolního součtu modulo 11 u 10místných rodných čísel
        if (len == 10) {
            long fullNumber = Long.parseLong(digits);
            long first9 = Long.parseLong(digits.substring(0, 9));
            int lastDigit = digits.charAt(9) - '0';
            long remainder = first9 % 11;

            if (remainder == 10) {
                // Výjimka u rodných čísel před rokem 1985: pokud je zbytek 10, poslední číslice může být 0
                if (lastDigit != 0) {
                    return false;
                }
            } else {
                if (fullNumber % 11 != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Ověří shodu mezi rodným číslem a explicitně zadaným rokem narození.
     *
     * @param rodneCislo  Zadané rodné číslo.
     * @param rokNarozeni Rok narození (např. 1985).
     * @return true, pokud rok souhlasí; jinak false.
     */
    public static boolean matchesBirthYear(String rodneCislo, Integer rokNarozeni) {
        if (rodneCislo == null || rokNarozeni == null) {
            return false;
        }

        String cleaned = rodneCislo.trim().replaceAll("[^0-9]", "");
        if (cleaned.length() < 6) {
            return false;
        }

        int rcYear2Digits = Integer.parseInt(cleaned.substring(0, 2));
        int birthYear2Digits = rokNarozeni % 100;

        if (rcYear2Digits != birthYear2Digits) {
            return false;
        }

        // Osoby narozené od 1954 musí mít 10místné RČ
        if (rokNarozeni >= 1954 && cleaned.length() == 9) {
            return false;
        }

        return true;
    }

    /**
     * Převede platné rodné číslo do standardizovaného formátu RRMMDD/XXXX.
     */
    public static String format(String rodneCislo) {
        if (rodneCislo == null) {
            return null;
        }
        String digits = rodneCislo.trim().replaceAll("[^0-9]", "");
        if (digits.length() == 9 || digits.length() == 10) {
            return digits.substring(0, 6) + "/" + digits.substring(6);
        }
        return rodneCislo.trim();
    }
}
