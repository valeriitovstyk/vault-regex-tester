import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  private static final Pattern TOKEN_PATTERN = Pattern.compile("\\b(?<prefix>vgs|tok)_(?<environment>[A-Za-z][A-Za-z0-9]+)_(?<identifier>[A-Za-z0-9]{1,22})\\b");

  public static void main(String[] args) {
    verify("quick brown fox tok_dev_abcdef123456", true, "end of line is ok");
    verify("tok_dev_abcdef123456 jumps over the lazy dog", true, "start of line is ok");
    verify("quick brown fox tok_dev_abcdef123456 jumps over the lazy dog", true, "typical case");
    verify("quick brown fox vgs_dev_abcdef123456 jumps over the lazy dog", true, "typical case with 'vgs' prefix");
    verify("quick brown fox tok_ab_c jumps over the lazy dog", true, "short env and token");
    verify("quick brown fox <test>tok_ab_c</test> jumps over the lazy dog", true, "with special characters around");
    verify("quick brown fox tok_dev_abcdef123456abcdef1234 jumps over the lazy dog", true, "identifier is 22 characters which is OK");
    verify("quick brown fox tok_DEV_ABCDEF123456 jumps over the lazy dog", true, "upper case");
    verify("quick brown fox tok_DEV_123456123456 jumps over the lazy dog", true, "numbers are fine");

    verify("quick brown fox tokk_dev_abcdef123456 jumps over the lazy dog", false, "invalid prefix");
    verify("quick brown fox tok_a_abcdef123456 jumps over the lazy dog", false, "environment is too short");
    verify("quick brown fox tok_1a_abcdef123456 jumps over the lazy dog", false, "environment start with number");
    verify("quick brown fox tok__dev_abcdef123456 jumps over the lazy dog", false, "double underscores");
    verify("quick brown fox tok_dev_abcdef123456abcdef12345 jumps over the lazy dog", false, "identifier is too long, 23 characters");

    // Cases under discussion
//    verify("quick brown fox -tok_dev_abcdef123456 jumps over the lazy dog", true, "weird but still valid");
//    verify("quick brown fox _tok_dev_abcdef123456 jumps over the lazy dog", true, "weird but still valid");
//    verify("quick brown fox _tok_dev_abcdef123456/ jumps over the lazy dog", true, "weird but still valid");
  }

  private static void verify(String body, boolean matches, String details) {
    Matcher matcher = TOKEN_PATTERN.matcher(body);

    assert (matcher.find() == matches);

    if (matches) {
      System.out.println(String.format("%7s :: %-32s :: %s :: %s", "VALID", matcher.group("prefix") + "_" + matcher.group("environment") + "_" + matcher.group("identifier"), body, details));
    } else {
      System.out.println(String.format("%7s :: %s :: %s", "INVALID", body, details));
    }
  }
}
