/**
 * Klientská validace a UX asistenty pro formulář pacienta.
 * Zajišťuje ergonomii pro recepční paní Věru:
 * 1. Automatické formátování rodného čísla (vložení lomítka).
 * 2. Okamžitá kontrola platnosti rodného čísla (formát, datum, modulo 11).
 * 3. Křížová kontrola souladu roku narození a rodného čísla.
 * 4. Kontrola jména, příjmení a telefonního čísla.
 * 5. Zamezení odeslání chybného formuláře.
 */
document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('patientForm');
    if (!form) return;

    const jmenoInput = document.getElementById('jmeno');
    const prijmeniInput = document.getElementById('prijmeni');
    const rokInput = document.getElementById('rokNarozeni');
    const rcInput = document.getElementById('cisloPojistence');
    const telefonInput = document.getElementById('telefon');
    const pojistovnaSelect = document.getElementById('pojistovna');

    // Automatické formátování rodného čísla při psaní
    rcInput.addEventListener('input', function (e) {
        let val = rcInput.value.replace(/\s+/g, '');
        // Pokud uživatel zadá 6 číslic a nepíše lomítko, vložíme lomítko
        const digitsOnly = val.replace(/\D/g, '');
        if (digitsOnly.length > 6 && !val.includes('/')) {
            rcInput.value = digitsOnly.substring(0, 6) + '/' + digitsOnly.substring(6, 10);
        }
        validateField(rcInput);
        if (rokInput.value) {
            validateField(rokInput);
        }
    });

    rokInput.addEventListener('input', function () {
        validateField(rokInput);
        if (rcInput.value) {
            validateField(rcInput);
        }
    });

    jmenoInput.addEventListener('input', () => validateField(jmenoInput));
    prijmeniInput.addEventListener('input', () => validateField(prijmeniInput));
    telefonInput.addEventListener('input', () => validateField(telefonInput));
    if (pojistovnaSelect) {
        pojistovnaSelect.addEventListener('change', () => validateField(pojistovnaSelect));
    }

    // Pomocná funkce pro zobrazení chyby
    function setError(input, message) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        let errorElem = input.parentElement.querySelector('.live-error');
        if (!errorElem) {
            errorElem = document.createElement('div');
            errorElem.className = 'live-error';
            input.parentElement.appendChild(errorElem);
        }
        errorElem.textContent = message;
        errorElem.style.display = 'block';
    }

    function clearError(input) {
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
        const errorElem = input.parentElement.querySelector('.live-error');
        if (errorElem) {
            errorElem.style.display = 'none';
            errorElem.textContent = '';
        }
    }

    // Validace českého rodného čísla (formát, datum, dělitelnost 11)
    function validateRc(rc) {
        if (!rc || !rc.trim()) {
            return { valid: false, message: 'Číslo pojištěnce (rodné číslo) je povinné.' };
        }
        const cleaned = rc.trim().replace(/\s+/g, '');
        const match = cleaned.match(/^(\d{2})(\d{2})(\d{2})\/?(\d{3,4})$/);
        if (!match) {
            return { valid: false, message: 'Zadejte platný formát RČ (např. 850512/1009 nebo 9 číslic před r. 1954).' };
        }

        const digits = cleaned.replace('/', '');
        if (digits.length !== 9 && digits.length !== 10) {
            return { valid: false, message: 'Rodné číslo musí mít 9 nebo 10 číslic.' };
        }

        const yearPart = parseInt(digits.substring(0, 2), 10);
        const monthPart = parseInt(digits.substring(2, 4), 10);
        const dayPart = parseInt(digits.substring(4, 6), 10);

        let realMonth = monthPart;
        if (realMonth > 70) realMonth -= 70;
        else if (realMonth > 50) realMonth -= 50;
        else if (realMonth > 20) realMonth -= 20;

        if (realMonth < 1 || realMonth > 12) {
            return { valid: false, message: 'Neplatný měsíc v rodném čísle (' + monthPart + ').' };
        }

        const currentYear2Digits = new Date().getFullYear() % 100;
        const fullYear = (digits.length === 9) ? (1900 + yearPart) :
            (yearPart <= currentYear2Digits ? (2000 + yearPart) : (1900 + yearPart));

        // Ověření platnosti dne v měsíci (včetně přestupných roků)
        const daysInMonth = new Date(fullYear, realMonth, 0).getDate();
        if (dayPart < 1 || dayPart > daysInMonth) {
            return { valid: false, message: 'Neplatný den v rodném čísle (den ' + dayPart + ' pro měsíc ' + realMonth + ').' };
        }

        // Kontrola kontrolního součtu modulo 11 u 10místných čísel
        if (digits.length === 10) {
            const fullNum = parseInt(digits, 10);
            const first9 = parseInt(digits.substring(0, 9), 10);
            const lastDigit = parseInt(digits.charAt(9), 10);
            const remainder = first9 % 11;

            if (remainder === 10) {
                if (lastDigit !== 0) {
                    return { valid: false, message: 'Neplatný kontrolní součet RČ (zbytek 10 vyžaduje koncovou nulu).' };
                }
            } else {
                if (fullNum % 11 !== 0) {
                    return { valid: false, message: 'Neplatný kontrolní součet (10místné RČ musí být dělitelné 11).' };
                }
            }
        }

        return { valid: true };
    }

    function validateField(input) {
        const val = input.value.trim();

        if (input === jmenoInput) {
            if (!val) {
                setError(input, 'Křestní jméno je povinné.');
                return false;
            }
            if (val.length < 2 || val.length > 50) {
                setError(input, 'Jméno musí mít 2 až 50 znaků.');
                return false;
            }
            if (!/^[a-zA-Zá-žÁ-Žč-žČ-Žď-žĎ-Ž\s\-']+$/.test(val)) {
                setError(input, 'Jméno smí obsahovat pouze písmena, mezeru nebo pomlčku.');
                return false;
            }
            clearError(input);
            return true;
        }

        if (input === prijmeniInput) {
            if (!val) {
                setError(input, 'Příjmení je povinné.');
                return false;
            }
            if (val.length < 2 || val.length > 50) {
                setError(input, 'Příjmení musí mít 2 až 50 znaků.');
                return false;
            }
            if (!/^[a-zA-Zá-žÁ-Žč-žČ-Žď-žĎ-Ž\s\-']+$/.test(val)) {
                setError(input, 'Příjmení smí obsahovat pouze písmena, mezeru nebo pomlčku.');
                return false;
            }
            clearError(input);
            return true;
        }

        if (input === rokInput) {
            if (!val) {
                setError(input, 'Rok narození je povinný.');
                return false;
            }
            const rok = parseInt(val, 10);
            const currentYear = new Date().getFullYear();
            if (isNaN(rok) || rok < 1900 || rok > currentYear) {
                setError(input, 'Rok narození musí být mezi 1900 a ' + currentYear + '.');
                return false;
            }

            // Křížová kontrola s RČ
            const rcVal = rcInput.value.replace(/\D/g, '');
            if (rcVal.length >= 2) {
                const rcYearPart = parseInt(rcVal.substring(0, 2), 10);
                if (rcYearPart !== (rok % 100)) {
                    setError(input, 'Rok narození (' + rok + ') neodpovídá roku v rodném čísle (' + rcYearPart + ').');
                    return false;
                }
            }

            clearError(input);
            return true;
        }

        if (input === rcInput) {
            const res = validateRc(val);
            if (!res.valid) {
                setError(input, res.message);
                return false;
            }

            // Křížová kontrola s rokem narození
            const digits = val.replace(/\D/g, '');
            if (rokInput.value) {
                const rok = parseInt(rokInput.value, 10);
                const rcYearPart = parseInt(digits.substring(0, 2), 10);
                if (rcYearPart !== (rok % 100)) {
                    setError(input, 'Rok v rodném čísle neodpovídá zadanému roku narození (' + rok + ').');
                    return false;
                }
            }

            clearError(input);
            return true;
        }

        if (input === telefonInput) {
            if (!val) {
                setError(input, 'Telefonní číslo je povinné.');
                return false;
            }
            if (!/^(\+420|\+421)?\s*[0-9]{3}\s*[0-9]{3}\s*[0-9]{3}$/.test(val)) {
                setError(input, 'Zadejte platné telefonní číslo (např. +420 777 123 456 nebo 777123456).');
                return false;
            }
            clearError(input);
            return true;
        }

        if (input === pojistovnaSelect) {
            if (!val) {
                setError(input, 'Výběr zdravotní pojišťovny je povinný.');
                return false;
            }
            clearError(input);
            return true;
        }

        return true;
    }

    form.addEventListener('submit', function (e) {
        let isFormValid = true;
        let firstInvalidField = null;

        const fieldsToValidate = [jmenoInput, prijmeniInput, rokInput, rcInput, telefonInput, pojistovnaSelect];

        for (const f of fieldsToValidate) {
            if (!f) continue;
            const valid = validateField(f);
            if (!valid) {
                isFormValid = false;
                if (!firstInvalidField) {
                    firstInvalidField = f;
                }
            }
        }

        if (!isFormValid) {
            e.preventDefault();
            if (firstInvalidField) {
                firstInvalidField.focus();
            }
        }
    });
});
