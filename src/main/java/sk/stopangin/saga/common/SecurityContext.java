package sk.stopangin.saga.common;

public class SecurityContext {

  private static ThreadLocal<String> user = new ThreadLocal<>();

  public static void setContext(String userId) {
    user = ThreadLocal.withInitial(() -> userId);
  }

  public static String getContext() {
    if (user.get() == null) {
      setContext("gajaka");
    }
    return user.get();
  }

}
