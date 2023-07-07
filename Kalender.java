package zeug;

import java.time.LocalDateTime;
import javax.swing.JOptionPane;

public abstract class Kalender {

    public static void main(String[] args) {

//        goTag();
//        goMonat();
        goJahr();

    }

    public static int getInputJahr() {
        int j;
        while (true) {
            try {
                j = Integer.parseInt(JOptionPane.showInputDialog("Bitte Jahr eingeben.").trim());
                if (j == 0) {
                    JOptionPane.showMessageDialog(null, "Ein Jahr 0 hat es nie gegeben");
                    continue;
                } else if (j > 4000) {
                    JOptionPane.showMessageDialog(null, "Angaben für Jahre nach 4000 können durch die ausstehende Korrektur der kumulativen Abweichung fehlerhaft sein.");
                }
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Bitte gültige Zahl eingeben");
            } catch (NullPointerException e) {
                System.exit(0);
            }
        }
        return j;
    }

    public static int getInputMonat() {
        int m;
        while (true) {
            try {
                m = Integer.parseInt(JOptionPane.showInputDialog("Bitte Monat eingeben.").trim());
                if (m < 1 || m > 12) {
                    JOptionPane.showMessageDialog(null, "Bitte gültigen Monats eingeben (1-12)");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Bitte gültige Zahl eingeben");
            } catch (NullPointerException e) {
                System.exit(0);
            }
        }
        return m;
    }

    public static int getInputTag(int j, int m) {
        int t;
        while (true) {
            try {
                t = Integer.parseInt(JOptionPane.showInputDialog("Bitte Tag eingeben.").trim());
                if (t < 1 || t > 31) {
                    JOptionPane.showMessageDialog(null, "Bitte gültigen Tag eingeben (1-31).");
                    continue;
                } else if (m == 2 && t > getMonatslänge(j, m) && isVergangenheit(j, m, t)) {
                    JOptionPane.showMessageDialog(null, "Der Februar" + intToJahr(j) + " hatte nur " + getMonatslänge(j, m) + " Tage.");
                    continue;
                } else if (m == 2 && t > getMonatslänge(j, m)) {
                    JOptionPane.showMessageDialog(null, "Der Februar" + intToJahr(j) + " hat nur " + getMonatslänge(j, m) + " Tage.");
                    continue;
                } else if (t > getMonatslänge(j, m)) {
                    JOptionPane.showMessageDialog(null, "Der " + intToMonat(m) + " " + "hat nur 30 Tage");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Bitte gültige Zahl eingeben");
            } catch (NullPointerException e) {
                System.exit(0);
            }
        }
        return t;
    }

    public static void goTag() {
        int j = getInputJahr();
        int m = getInputMonat();
        int t = getInputTag(j, m);
        JOptionPane.showMessageDialog(null, getOutput(j, m, t));
    }

    public static String getOutput(int j, int m, int t) {
        if (j < 1582 || j == 1582 && m <= 10 && t <= 4) {
            if (m == 2 && t == 24 && isSchaltjahrJulianisch(j)) {
                int w = getWochentagJulianisch(j, m, t);
                return "Der 24. Februar " + intToJahr(j) + " war ein " + intToWochentag(w) + " und ein " + intToWochentag(++w) + ".";
            } else {
                return "Der " + t + ". " + intToMonat(m) + intToJahr(j) + " war ein " + intToWochentag(getWochentagJulianisch(j, m, t)) + ".";
            }
        } else if (j == 1582 && m == 10 && t > 4 && t < 15) {
            return "Der 5.-14. Oktober 1582 wurde wegen der Umstellung auf den gregorianischen Kalender ausgelassen.";
        } else if (isVergangenheit(j, m, t)) {
            return "Der " + t + ". " + intToMonat(m) + " " + j + " war ein " + intToWochentag(getWochentagNew(j, m, t)) + ".";
        } else {
            return "Der " + t + ". " + intToMonat(m) + " " + j + " ist ein " + intToWochentag(getWochentagNew(j, m, t)) + ".";
        }
    }

    public static boolean isVergangenheit(int j, int m, int t) {
        return LocalDateTime.of(j, m, t, 23, 59, 59, 999).isBefore(LocalDateTime.now());
    }

    @Deprecated
    public static int getWochentag(int j, int m, int t) {
        int w = 6;
        for (int i = 1583;; i++) {
            for (int k = 1; k <= 12; k++) {
                for (int l = 1; l <= getMonatslänge(i, k); l++) {
                    if (i == j && k == m && l == t) {
                        return w;
                    } else if (w == 7) {
                        w = 1;
                    } else {
                        w++;
                    }
                }
            }
        }
    }

    public static int getWochentagJulianisch(int j, int m, int t) {
        int w = 4;
        for (int i = 1582;; i--) {
            if (i == 0) {
                i--;
            }
            for (int k = 12; k >= 1; k--) {
                if (i == 1582 && k == 12) {
                    k = 10;
                }
                for (int n = getMonatslänge(i, k); n >= 1; n--) {
                    if (i == 1582 && k == 10 && n == 31) {
                        n = 4;
                    } else if (isSchaltjahrJulianisch(i) && k == 2 && n == 24) {
                        if (w == 1) {
                            w = 7;
                        } else {
                            w--;
                        }
                    }
                    if (i == j && k == m && n == t) {
                        return w;
                    } else if (w == 1) {
                        w = 7;
                    } else {
                        w--;
                    }
                }
            }
        }
    }

    public static int getWochentagNew(int j, int m, int t) {
        if (m < 3) {
            j--;
        }
        return (int) ((t + Math.floor(2.6 * ((m + 9) % 12 + 1) - 0.2) + j % 100 + Math.floor(j % 100 / 4) + Math.floor(j / 400) - 2 * Math.floor(j / 100) - 1) % 7 + 7) % 7 + 1;
    }

    public static String intToWochentag(int w) {
        switch (w) {
            case 1:
                return "Montag";
            case 2:
                return "Dienstag";
            case 3:
                return "Mittwoch";
            case 4:
                return "Donnerstag";
            case 5:
                return "Freitag";
            case 6:
                return "Samstag";
            case 7:
                return "Sonntag";
            case 8:
                return "Montag";
            default:
                return null;
        }
    }

    public static int getMonatslänge(int j, int m) {
        if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12) {
            return 31;
        } else if (m == 4 || m == 6 || m == 9 || m == 11) {
            return 30;
        } else if (m == 2 && isSchaltjahr(j)) {
            return 29;
        } else if (m == 2) {
            return 28;
        } else {
            return 0;
        }
    }

    public static boolean isSchaltjahr(int j) {
        if (j < 1583) {
            return false;
        } else if (j % 400 == 0) {
            return true;
        } else if (j % 100 == 0) {
            return false;
        } else return j % 4 == 0;
    }

    public static boolean isSchaltjahrJulianisch(int j) {
        if (j < 1) {
            j++;
        }
        return j % 4 == 0;
    }

    public static String intToMonat(int m) {
        switch (m) {
            case 1:
                return "Januar";
            case 2:
                return "Ferbruar";
            case 3:
                return "März";
            case 4:
                return "April";
            case 5:
                return "Mai";
            case 6:
                return "Juni";
            case 7:
                return "Juli";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "Oktober";
            case 11:
                return "Novbember";
            case 12:
                return "Dezember";
            default:
                return null;
        }
    }

    public static String intToJahr(int j) {
        if (j > 0) {
            return j + "";
        } else if (j < 0) {
            return Math.abs(j) + " v. Chr.";
        } else {
            return null;
        }
    }

    public static void goMonat() {
        int j = getInputJahr();
        int m = getInputMonat();
        System.out.println(getMonat(j, m, true));
    }

    public static String getMonat(int j, int m, boolean t) {
        String output;
        if (j >= 1583 || j == 1582 && m > 10) {
            return getMonatGregorianisch(j, m, t);
        } else if (j == 1582 && m == 10) {
            return "     Oktober 1582 \n Mo Di Mi Do Fr Sa So \n  1  2  3  4 15 16 17 \n 18 19 20 21 22 23 24 \n 25 26 27 28 29 30 31 ";
        } else {
            return getMonatJulianisch(j, m, t);
        }
    }

    public static String getMonatGregorianisch(int j, int m, boolean t) {
        String k = intToMonat(m);
        if (t) {
            k = k + " " + intToJahr(j);
        }
        k = getAbstand(k) + k + " \n Mo Di Mi Do Fr Sa So \n ";
        int w = 1;
        for (int i = 1; i < getWochentagNew(j, m, 1); i++) {
            k = k + "   ";
            w++;
        }
        for (int i = 1; i <= getMonatslänge(j, m); i++) {
            if (w > 7) {
                k = k + "\n ";
                w = 1;
            }
            if (i < 10) {
                k = k + " " + i + " ";
            } else {
                k = k + i + " ";
            }
            w++;
        }
        return k;
    }

    public static String getMonatJulianisch(int j, int m, boolean t) {
        String k = intToMonat(m);
        if (t) {
            k = k + " " + intToJahr(j);
        }
        k = getAbstand(k) + k + " \n Mo Di Mi Do Fr Sa So \n ";
        int w = 1;
        for (int i = 1; i < getWochentagJulianisch(j, m, 1); i++) {
            k = k + "   ";
            w++;
        }
        boolean x = true;
        for (int i = 1; i <= getMonatslänge(j, m); i++) {

            if (w > 7) {
                k = k + "\n ";
                w = 1;
            }
            if (i < 10) {
                k = k + " " + i + " ";
            } else {
                k = k + i + " ";
            }
            if (m == 2 && i == 24 && isSchaltjahrJulianisch(j) && x) {
                x = false;
                i--;
            }
            w++;
        }
        return k;
    }

    public static void goJahr() {
        System.out.println(getJahr(getInputJahr()));
    }
    
    public static String getJahr(int j) {
        String k = intToJahr(j);
        k = getAbstand(k) + k;
        for (int i = 1; i <= 12; i++) {
            k = k + "\n" + getMonat(j, i, false) + "\n";            
        }
        return k;
    }

    public static String getAbstand(String k) {
        String a = "";
        int i = (10 - (k.length() / 2));
        for (int j = 0; j < i; j++) {
            a = a + " ";
        }
        return a;
        }
    
    public static void thisTag() {
        LocalDateTime d = LocalDateTime.now();
        int t = d.getDayOfMonth();
        int m = d.getMonthValue();
        int j = d.getYear();
        JOptionPane.showMessageDialog(null, "Heute ist " + intToWochentag(getWochentagNew(j, m, t)) + " der " + t + ". " + intToMonat(m) + " " + j + ".");        
    }
    
    public static void thisMonat() {
        LocalDateTime d = LocalDateTime.now();
        System.out.println(getMonatGregorianisch(d.getYear(), d.getMonthValue(), true));
    }
    
    public static void thisJahr() {
        System.out.println(getJahr(LocalDateTime.now().getYear()));
    }
}
